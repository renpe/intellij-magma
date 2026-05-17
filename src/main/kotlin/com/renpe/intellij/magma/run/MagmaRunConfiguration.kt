package com.renpe.intellij.magma.run

import com.intellij.execution.ExecutionException
import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.LocatableConfigurationBase
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunConfigurationWithSuppressedDefaultDebugAction
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project

class MagmaRunConfiguration(
    project: Project,
    factory: ConfigurationFactory,
    name: String,
) : LocatableConfigurationBase<MagmaRunConfigurationOptions>(project, factory, name),
    RunConfigurationWithSuppressedDefaultDebugAction {

    private val opts: MagmaRunConfigurationOptions
        get() = options as MagmaRunConfigurationOptions

    var scriptPath: String
        get() = opts.scriptPath.orEmpty()
        set(value) { opts.scriptPath = value }

    var scriptArgs: String
        get() = opts.scriptArgs.orEmpty()
        set(value) { opts.scriptArgs = value }

    var workingDirectory: String
        get() = opts.workingDirectory.orEmpty()
        set(value) { opts.workingDirectory = value }

    var interpreterPath: String
        get() = opts.interpreterPath.orEmpty()
        set(value) { opts.interpreterPath = value }

    var wslDistribution: String
        get() = opts.wslDistribution.orEmpty()
        set(value) { opts.wslDistribution = value }

    var threads: String
        get() = opts.threads.orEmpty()
        set(value) { opts.threads = value }

    var seed: String
        get() = opts.seed.orEmpty()
        set(value) { opts.seed = value }

    var startupFile: String
        get() = opts.startupFile.orEmpty()
        set(value) { opts.startupFile = value }

    var ignoreStartupFile: Boolean
        get() = opts.ignoreStartupFile
        set(value) { opts.ignoreStartupFile = value }

    var memoryLimit: String
        get() = opts.memoryLimit.orEmpty()
        set(value) { opts.memoryLimit = value }

    var extraArgs: String
        get() = opts.extraArgs.orEmpty()
        set(value) { opts.extraArgs = value }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> =
        MagmaRunSettingsEditor(project)

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState {
        if (scriptPath.isBlank()) {
            throw ExecutionException("No Magma script selected. Edit the run configuration and pick a .m file.")
        }
        return MagmaCommandLineState(this, environment)
    }

    override fun suggestedName(): String? {
        val path = scriptPath.takeIf { it.isNotBlank() } ?: return null
        return path.substringAfterLast('/').substringAfterLast('\\').ifEmpty { null }
    }
}
