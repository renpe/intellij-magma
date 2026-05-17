package com.renpe.intellij.magma.run

import com.intellij.execution.process.KillableProcessHandler
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

/**
 * KillableProcessHandler for magma running inside WSL.
 *
 * `wsl.exe` with redirected stdio does NOT propagate SIGTERM to its Linux
 * children (Microsoft/WSL#3766, #2496), so the Windows-side wrapper dying
 * leaves magma running, adopted by /init. Our shell wrapper writes its own
 * PID to [pidFilePath] right before `exec`ing into magma — POSIX guarantees
 * the PID survives exec(2). On stop we open a second wsl.exe and signal that
 * PID directly.
 *
 * The base class is constructed with a Process plus a presentation string,
 * so the run-tool-window shows the magma invocation instead of the
 * `wsl.exe -d <dist> -- /bin/sh -c "..."` wrapper boilerplate.
 */
class MagmaWslProcessHandler(
    process: Process,
    displayCommandLine: String,
    charset: Charset,
    private val distribution: String,
    private val pidFilePath: String,
) : KillableProcessHandler(process, displayCommandLine, charset) {

    init {
        setShouldDestroyProcessRecursively(true)
    }

    override fun destroyProcessImpl() {
        if (distribution.isNotBlank() && pidFilePath.isNotBlank()) {
            // The kill is dispatched on a pool thread so the EDT (which the
            // Stop button runs on) doesn't block on process.waitFor and trip
            // IntelliJ's freeze detector.
            ApplicationManager.getApplication().executeOnPooledThread {
                killWslSideProcess()
            }
        }
        super.destroyProcessImpl()
    }

    private fun killWslSideProcess() {
        // The wrapper writes its PID immediately, but a user hitting Stop in
        // the first few milliseconds could observe an empty PIDFILE. Poll up
        // to ~0.5s so the rare fast-stop race doesn't leak a magma process.
        val script = "i=0; " +
            "while [ \$i -lt 10 ] && [ ! -s '$pidFilePath' ]; do sleep 0.05; i=\$((i+1)); done; " +
            "pid=\$(cat '$pidFilePath' 2>/dev/null) || pid=''; " +
            "if [ -n \"\$pid\" ]; then " +
            "  kill -TERM \"\$pid\" 2>/dev/null; " +
            "  for i in 1 2 3 4 5; do kill -0 \"\$pid\" 2>/dev/null || break; sleep 0.3; done; " +
            "  kill -KILL \"\$pid\" 2>/dev/null; " +
            "fi; " +
            "rm -f '$pidFilePath' 2>/dev/null"

        try {
            // ProcessBuilder + Redirect.DISCARD so the kill subprocess's stdio
            // can't fill an unread pipe buffer and deadlock waitFor(). The
            // kill script is essentially silent, but explicit discard keeps
            // us safe from future changes / WSL stderr noise.
            val pb = ProcessBuilder("wsl.exe", "-d", distribution, "--", "sh", "-c", script)
                .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                .redirectError(ProcessBuilder.Redirect.DISCARD)
            val process = pb.start()
            if (!process.waitFor(5, TimeUnit.SECONDS)) {
                process.destroyForcibly()
                LOG.warn("Timed out waiting for WSL-side kill of magma (pidfile=$pidFilePath)")
            } else {
                LOG.info("WSL-side kill finished with exit=${process.exitValue()}")
            }
        } catch (t: Throwable) {
            LOG.warn("Failed to run WSL-side kill for magma (pidfile=$pidFilePath)", t)
        }
    }

    companion object {
        private val LOG = Logger.getInstance(MagmaWslProcessHandler::class.java)
    }
}
