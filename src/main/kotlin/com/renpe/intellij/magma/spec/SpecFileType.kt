package com.renpe.intellij.magma.spec

import com.renpe.intellij.magma.lang.MagmaIcons
import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

object SpecFileType : LanguageFileType(SpecLanguage) {
    override fun getName(): String = "Magma Spec"
    override fun getDescription(): String = "Magma spec file"
    override fun getDefaultExtension(): String = "spec"
    override fun getIcon(): Icon = MagmaIcons.FILE
}
