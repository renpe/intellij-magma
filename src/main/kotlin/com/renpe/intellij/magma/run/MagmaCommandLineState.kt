package com.renpe.intellij.magma.run

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.ParametersList
import com.intellij.execution.process.KillableProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.SystemInfo
import com.renpe.intellij.magma.run.wsl.MagmaWslSupport
import com.renpe.intellij.magma.settings.MagmaSettings
import java.io.File
import java.util.UUID
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class MagmaCommandLineState(
    private val configuration: MagmaRunConfiguration,
    environment: ExecutionEnvironment,
) : CommandLineState(environment) {

    private data class ResolvedConfig(
        val interpreter: String,
        val threads: String,
        val seed: String,
        val startupFile: String,
        val ignoreStartupFile: Boolean,
        val memoryLimitBytes: Long?,
        val extraArgs: String,
        val wslDistribution: String,
        val useWsl: Boolean,
    )

    override fun startProcess(): ProcessHandler {
        cleanupStaleTempScriptsOnce()
        val resolved = resolve()

        val scriptHostPath = configuration.scriptPath
        if (scriptHostPath.isBlank()) throw ExecutionException("No Magma script selected.")
        if (!File(scriptHostPath).isFile) throw ExecutionException("Magma script not found: $scriptHostPath")

        val scriptForMagma = if (resolved.useWsl) {
            MagmaWslSupport.toWslPath(resolved.wslDistribution, scriptHostPath)
        } else scriptHostPath

        val workingDirHost = configuration.workingDirectory.ifBlank { File(scriptHostPath).parent ?: "" }
        val workingDirForWsl = if (resolved.useWsl && workingDirHost.isNotBlank()) {
            MagmaWslSupport.toWslPath(resolved.wslDistribution, workingDirHost)
        } else ""
        val startupFileForMagma = if (resolved.useWsl && resolved.startupFile.isNotBlank()) {
            MagmaWslSupport.toWslPath(resolved.wslDistribution, resolved.startupFile)
        } else resolved.startupFile

        val magmaArgs = buildMagmaArgs(
            resolved = resolved,
            scriptForMagma = scriptForMagma,
            startupFileForMagma = startupFileForMagma,
        )

        // Path (inside WSL) where the wrapper script writes magma's PID so we
        // can signal it from a second wsl.exe at Stop time (Microsoft/WSL#3766
        // means wsl.exe with redirected stdio cannot propagate SIGTERM itself).
        val pidFilePath = if (resolved.useWsl) "/tmp/magma-${UUID.randomUUID()}.pid" else ""

        val commandLine = if (resolved.useWsl) {
            buildWslCommandLine(
                interpreter = resolved.interpreter,
                magmaArgs = magmaArgs,
                workingDir = workingDirForWsl,
                memoryLimitBytes = resolved.memoryLimitBytes,
                pidFilePath = pidFilePath,
                wslDistribution = resolved.wslDistribution,
            )
        } else {
            buildNativeCommandLine(
                interpreter = resolved.interpreter,
                magmaArgs = magmaArgs,
                workingDir = if (workingDirHost.isNotBlank()) File(workingDirHost) else null,
                memoryLimitBytes = resolved.memoryLimitBytes,
            )
        }

        val handler: KillableProcessHandler = if (resolved.useWsl) {
            // Start the process ourselves so we can hand the handler a
            // "presentable" command line (just the magma call) instead of
            // the wsl.exe + /bin/sh wrapper, which is what the run-tool-window
            // would otherwise print as its initial system message.
            val process = try {
                commandLine.createProcess()
            } catch (e: com.intellij.execution.ExecutionException) {
                throw e
            }
            val displayLine = buildDisplayCommandLine(resolved.interpreter, magmaArgs)
            MagmaWslProcessHandler(
                process,
                displayLine,
                Charsets.UTF_8,
                resolved.wslDistribution,
                pidFilePath,
            )
        } else {
            KillableProcessHandler(commandLine)
        }

        ProcessTerminatedListener.attach(handler)
        return handler
    }

    private fun buildDisplayCommandLine(interpreter: String, magmaArgs: List<String>): String =
        (listOf(interpreter) + magmaArgs).joinToString(" ", transform = ::quoteForDisplay)

    private fun quoteForDisplay(arg: String): String {
        if (arg.isEmpty()) return "\"\""
        val needsQuoting = arg.any { it.isWhitespace() || it == '"' || it == '\\' }
        if (!needsQuoting) return arg
        val escaped = arg.replace("\\", "\\\\").replace("\"", "\\\"")
        return "\"$escaped\""
    }

    private fun resolve(): ResolvedConfig {
        val s = MagmaSettings.getInstance().state

        val interpreter = configuration.interpreterPath.ifBlank { s.interpreterPath }
            .ifBlank { throw ExecutionException("Magma interpreter not set. Open Settings → Tools → Magma.") }
        val wsl = configuration.wslDistribution.ifBlank { s.defaultWslDistribution }
        val useWsl = SystemInfo.isWindows && wsl.isNotBlank()

        val threads = configuration.threads.ifBlank { s.defaultThreads }
        val seed = configuration.seed.ifBlank { s.defaultSeed }
        val startupFile = configuration.startupFile.ifBlank { s.defaultStartupFile }
        val ignoreStartup = configuration.ignoreStartupFile || s.defaultIgnoreStartupFile

        val memLimitInput = configuration.memoryLimit.ifBlank { s.defaultMemoryLimit }
        val memLimit = if (memLimitInput.isBlank()) null else MagmaMemoryLimit.parseBytes(memLimitInput)
            ?: throw ExecutionException("Invalid memory limit '$memLimitInput'. Use a number with optional K, M or G suffix.")

        val extra = listOf(s.defaultExtraArgs, configuration.extraArgs)
            .filter { it.isNotBlank() }
            .joinToString(" ")

        return ResolvedConfig(
            interpreter = interpreter,
            threads = threads,
            seed = seed,
            startupFile = startupFile,
            ignoreStartupFile = ignoreStartup,
            memoryLimitBytes = memLimit,
            extraArgs = extra,
            wslDistribution = wsl,
            useWsl = useWsl,
        )
    }

    private fun buildMagmaArgs(
        resolved: ResolvedConfig,
        scriptForMagma: String,
        startupFileForMagma: String,
    ): List<String> {
        val args = mutableListOf("-b")  // batch mode is always on; not user-configurable
        if (resolved.threads.isNotBlank()) {
            args += "-t"; args += resolved.threads
        }
        if (resolved.seed.isNotBlank()) {
            args += "-S"; args += resolved.seed
        }
        if (resolved.ignoreStartupFile) {
            args += "-n"
        } else if (startupFileForMagma.isNotBlank()) {
            args += "-s"; args += startupFileForMagma
        }
        if (resolved.extraArgs.isNotBlank()) {
            args += ParametersList.parse(resolved.extraArgs).toList()
        }
        args += scriptForMagma
        if (configuration.scriptArgs.isNotBlank()) {
            args += ParametersList.parse(configuration.scriptArgs).toList()
        }
        return args
    }

    private fun buildNativeCommandLine(
        interpreter: String,
        magmaArgs: List<String>,
        workingDir: File?,
        memoryLimitBytes: Long?,
    ): GeneralCommandLine {
        var cmd = GeneralCommandLine()
            .withExePath(interpreter)
            .withParameters(magmaArgs)
            .withCharset(Charsets.UTF_8)
        if (workingDir != null) cmd = cmd.withWorkDirectory(workingDir)
        if (memoryLimitBytes != null) cmd.environment["MAGMA_MEMORY_LIMIT"] = memoryLimitBytes.toString()
        return cmd
    }

    private fun buildWslCommandLine(
        interpreter: String,
        magmaArgs: List<String>,
        workingDir: String,
        memoryLimitBytes: Long?,
        pidFilePath: String,
        wslDistribution: String,
    ): GeneralCommandLine {
        val magmaCall = (listOf(shellQuote(interpreter)) + magmaArgs.map(::shellQuote)).joinToString(" ")

        // Build the wrapper as a multi-line shell script. We write it to a
        // Windows temp file and pass its WSL path to /bin/sh — this sidesteps
        // wsl.exe's quirky -c argument handling (collapsing/mangling spaces
        // and quotes), which previously turned `VAR=val; cmd` into the POSIX
        // `VAR=val cmd` form and produced `cannot create :` at runtime.
        //
        // POSIX guarantees exec(2) preserves the PID, so $$ captured before
        // exec is exactly the PID our kill handler later targets.
        //
        // `set -C` (no-clobber) makes the redirect refuse to follow a
        // pre-existing path. Combined with the unguessable UUID, this
        // defeats symlink races on /tmp on multi-user WSL distros.
        val script = buildString {
            append("PIDFILE=").append(shellQuote(pidFilePath)).append('\n')
            append("set -C\n")
            append("echo \$\$ > \"\$PIDFILE\" || exit 1\n")
            append("set +C\n")
            append("trap 'rm -f \"\$PIDFILE\"' EXIT\n")
            if (memoryLimitBytes != null) {
                append("export MAGMA_MEMORY_LIMIT=").append(memoryLimitBytes).append('\n')
            }
            if (workingDir.isNotBlank()) {
                append("cd ").append(shellQuote(workingDir)).append(" || true\n")
            }
            append("exec ").append(magmaCall).append('\n')
        }

        val winScript = java.nio.file.Files.createTempFile("magma-runner-", ".sh")
        winScript.toFile().deleteOnExit()
        java.nio.file.Files.writeString(
            winScript,
            script,
            Charsets.UTF_8,
            java.nio.file.StandardOpenOption.TRUNCATE_EXISTING,
        )
        val wslScript = MagmaWslSupport.toWslPath(wslDistribution, winScript.toAbsolutePath().toString())

        // wsl.exe direct, not WSLDistribution.patchCommandLine — the 2026.1
        // patch path runs through IJent and asserts not-on-EDT.
        return GeneralCommandLine()
            .withExePath("wsl.exe")
            .withParameters("-d", wslDistribution, "--", "/bin/sh", wslScript)
            .withCharset(Charsets.UTF_8)
    }

    private fun shellQuote(s: String): String {
        if (s.isEmpty()) return "''"
        if (s.all { it.isLetterOrDigit() || it in "/_-.:,@+=" }) return s
        return "'" + s.replace("'", "'\\''") + "'"
    }

    companion object {
        private val LOG = Logger.getInstance(MagmaCommandLineState::class.java)
        private val cleanupDone = AtomicBoolean(false)

        // `deleteOnExit()` only fires on graceful JVM shutdown; IDE crashes
        // leave magma-runner-*.sh files in %TEMP%. Once per IDE session we
        // sweep files older than 24 h off the EDT, best-effort.
        private fun cleanupStaleTempScriptsOnce() {
            if (!cleanupDone.compareAndSet(false, true)) return
            ApplicationManager.getApplication().executeOnPooledThread {
                try {
                    val tempDir = java.nio.file.Paths.get(System.getProperty("java.io.tmpdir"))
                    val cutoff = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(24)
                    java.nio.file.Files.list(tempDir).use { stream ->
                        stream.forEach { path ->
                            val name = path.fileName.toString()
                            if (!name.startsWith("magma-runner-") || !name.endsWith(".sh")) return@forEach
                            try {
                                val mtime = java.nio.file.Files.getLastModifiedTime(path).toMillis()
                                if (mtime < cutoff) java.nio.file.Files.deleteIfExists(path)
                            } catch (_: Throwable) {
                                // best-effort
                            }
                        }
                    }
                } catch (t: Throwable) {
                    LOG.info("Stale magma-runner-*.sh sweep failed", t)
                }
            }
        }
    }
}
