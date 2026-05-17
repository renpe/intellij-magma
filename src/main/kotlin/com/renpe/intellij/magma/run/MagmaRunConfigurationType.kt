package com.renpe.intellij.magma.run

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.renpe.intellij.magma.lang.MagmaIcons
import javax.swing.Icon

class MagmaRunConfigurationType : ConfigurationType {
    val factory = MagmaRunConfigurationFactory(this)

    override fun getDisplayName(): String = "Magma"
    override fun getConfigurationTypeDescription(): String = "Magma script run configuration"
    override fun getIcon(): Icon = MagmaIcons.FILE
    override fun getId(): String = "MagmaRunConfiguration"
    override fun getConfigurationFactories(): Array<ConfigurationFactory> = arrayOf(factory)

    companion object {
        fun getInstance(): MagmaRunConfigurationType =
            ConfigurationTypeUtil.findConfigurationType(MagmaRunConfigurationType::class.java)
    }
}
