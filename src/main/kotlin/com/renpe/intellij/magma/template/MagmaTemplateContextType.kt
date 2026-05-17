package com.renpe.intellij.magma.template

import com.renpe.intellij.magma.lang.MagmaFileType
import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateContextType

class MagmaTemplateContextType : TemplateContextType("Magma") {
    override fun isInContext(templateActionContext: TemplateActionContext): Boolean {
        val file = templateActionContext.file
        return file.fileType == MagmaFileType
    }
}
