package com.renpe.intellij.magma.lang

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object MagmaIcons {
    val FILE: Icon = IconLoader.getIcon("/icons/magma.png", MagmaIcons::class.java)
}

object MagmaFileType : LanguageFileType(MagmaLanguage) {
    override fun getName(): String = "Magma"
    override fun getDescription(): String = "Magma source file"
    override fun getDefaultExtension(): String = "m"
    override fun getIcon(): Icon = MagmaIcons.FILE
}
