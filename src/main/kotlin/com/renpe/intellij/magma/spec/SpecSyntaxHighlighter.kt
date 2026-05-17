package com.renpe.intellij.magma.spec

import com.renpe.intellij.magma.highlight.MagmaColors
import com.renpe.intellij.magma.lang.MagmaTokenTypes
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.tree.IElementType

class SpecSyntaxHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer = SpecLexer()
    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> = when (tokenType) {
        MagmaTokenTypes.INCLUDE_DIRECTIVE -> arrayOf(MagmaColors.INCLUDE_DIRECTIVE)
        MagmaTokenTypes.IDENTIFIER -> arrayOf(MagmaColors.IDENTIFIER)
        MagmaTokenTypes.BAD_CHARACTER -> arrayOf(MagmaColors.BAD_CHARACTER)
        else -> TextAttributesKey.EMPTY_ARRAY
    }
}

class SpecSyntaxHighlighterFactory : SyntaxHighlighterFactory() {
    override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?): SyntaxHighlighter =
        SpecSyntaxHighlighter()
}
