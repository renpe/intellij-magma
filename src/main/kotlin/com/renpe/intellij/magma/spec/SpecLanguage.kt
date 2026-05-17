package com.renpe.intellij.magma.spec

import com.intellij.lang.Language

object SpecLanguage : Language("MagmaSpec") {
    override fun getDisplayName(): String = "Magma Spec"
    private fun readResolve(): Any = SpecLanguage
}
