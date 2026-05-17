package com.renpe.intellij.magma.lang

import com.intellij.lang.Language

object MagmaLanguage : Language("Magma") {
    override fun getDisplayName(): String = "Magma"
    private fun readResolve(): Any = MagmaLanguage
}
