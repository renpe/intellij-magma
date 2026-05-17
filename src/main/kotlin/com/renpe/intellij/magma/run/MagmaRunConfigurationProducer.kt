package com.renpe.intellij.magma.run

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.LazyRunConfigurationProducer
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import com.renpe.intellij.magma.lang.MagmaFileType

class MagmaRunConfigurationProducer : LazyRunConfigurationProducer<MagmaRunConfiguration>() {

    override fun getConfigurationFactory(): ConfigurationFactory =
        MagmaRunConfigurationType.getInstance().factory

    override fun setupConfigurationFromContext(
        configuration: MagmaRunConfiguration,
        context: ConfigurationContext,
        sourceElement: Ref<PsiElement>,
    ): Boolean {
        val file = context.location?.virtualFile ?: return false
        if (file.fileType !is MagmaFileType) return false
        val path = file.path
        configuration.scriptPath = path
        configuration.name = configuration.suggestedName() ?: file.name
        return true
    }

    override fun isConfigurationFromContext(
        configuration: MagmaRunConfiguration,
        context: ConfigurationContext,
    ): Boolean {
        val file = context.location?.virtualFile ?: return false
        return file.fileType is MagmaFileType && configuration.scriptPath == file.path
    }
}
