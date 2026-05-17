package com.renpe.intellij.magma.run

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.project.Project

class MagmaRunConfigurationFactory(type: ConfigurationType) : ConfigurationFactory(type) {

    override fun getId(): String = "MagmaRunConfigurationFactory"

    override fun createTemplateConfiguration(project: Project): RunConfiguration =
        MagmaRunConfiguration(project, this, "Magma")

    override fun getOptionsClass(): Class<out BaseState> = MagmaRunConfigurationOptions::class.java
}
