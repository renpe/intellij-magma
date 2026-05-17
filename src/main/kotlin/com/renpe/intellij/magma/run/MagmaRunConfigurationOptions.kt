package com.renpe.intellij.magma.run

import com.intellij.execution.configurations.LocatableRunConfigurationOptions

class MagmaRunConfigurationOptions : LocatableRunConfigurationOptions() {
    var scriptPath: String? by string()
    var scriptArgs: String? by string()
    var workingDirectory: String? by string()
    var interpreterPath: String? by string()
    var wslDistribution: String? by string()
    var threads: String? by string()
    var seed: String? by string()
    var startupFile: String? by string()
    var ignoreStartupFile: Boolean by property(false)
    var memoryLimit: String? by string()
    var extraArgs: String? by string()
}
