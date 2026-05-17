package com.renpe.intellij.magma.brace

import com.renpe.intellij.magma.lang.MagmaTokenTypes
import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType

class MagmaBraceMatcher : PairedBraceMatcher {
    override fun getPairs(): Array<BracePair> = PAIRS
    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?): Boolean = true
    override fun getCodeConstructStart(file: PsiFile, openingBraceOffset: Int): Int = openingBraceOffset

    private companion object {
        val PAIRS: Array<BracePair> = arrayOf(
            BracePair(MagmaTokenTypes.LPAREN, MagmaTokenTypes.RPAREN, false),
            BracePair(MagmaTokenTypes.LBRACK, MagmaTokenTypes.RBRACK, false),
            BracePair(MagmaTokenTypes.LBRACE, MagmaTokenTypes.RBRACE, true),
        )
    }
}
