package com.renpe.intellij.magma.highlight

import com.renpe.intellij.magma.lang.MagmaLexer
import com.renpe.intellij.magma.lang.MagmaTokenTypes
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType

class MagmaSyntaxHighlighter : SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer = MagmaLexer()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        val key = ATTR_MAP[tokenType] ?: return TextAttributesKey.EMPTY_ARRAY
        return arrayOf(key)
    }

    private companion object {
        val ATTR_MAP: Map<IElementType, TextAttributesKey> = mapOf(
            MagmaTokenTypes.LINE_COMMENT to MagmaColors.LINE_COMMENT,
            MagmaTokenTypes.BLOCK_COMMENT to MagmaColors.BLOCK_COMMENT,
            MagmaTokenTypes.STRING_DOUBLE to MagmaColors.STRING,
            MagmaTokenTypes.STRING_SINGLE to MagmaColors.STRING,
            MagmaTokenTypes.NUMBER to MagmaColors.NUMBER,
            MagmaTokenTypes.IDENTIFIER to MagmaColors.IDENTIFIER,
            MagmaTokenTypes.KEYWORD_CONTROL to MagmaColors.KEYWORD_CONTROL,
            MagmaTokenTypes.KEYWORD_FUNCTION_DECL to MagmaColors.KEYWORD_FUNCTION_DECL,
            MagmaTokenTypes.KEYWORD_STATEMENT to MagmaColors.KEYWORD_STATEMENT,
            MagmaTokenTypes.KEYWORD_OPERATOR_WORD to MagmaColors.KEYWORD_OPERATOR_WORD,
            MagmaTokenTypes.CONSTANT to MagmaColors.CONSTANT,
            MagmaTokenTypes.TYPE_CATEGORY to MagmaColors.TYPE_CATEGORY,
            MagmaTokenTypes.BUILTIN_FUNCTION to MagmaColors.BUILTIN_FUNCTION,
            MagmaTokenTypes.TILDE_VARIABLE to MagmaColors.TILDE_VARIABLE,
            MagmaTokenTypes.BACKTICK_VARIABLE to MagmaColors.BACKTICK_VARIABLE,
            MagmaTokenTypes.OPERATOR to MagmaColors.OPERATOR,
            MagmaTokenTypes.ASSIGN to MagmaColors.OPERATOR,
            MagmaTokenTypes.LPAREN to MagmaColors.PARENTHESES,
            MagmaTokenTypes.RPAREN to MagmaColors.PARENTHESES,
            MagmaTokenTypes.LBRACK to MagmaColors.BRACKETS,
            MagmaTokenTypes.RBRACK to MagmaColors.BRACKETS,
            MagmaTokenTypes.LBRACE to MagmaColors.BRACES,
            MagmaTokenTypes.RBRACE to MagmaColors.BRACES,
            MagmaTokenTypes.SEMICOLON to MagmaColors.SEMICOLON,
            MagmaTokenTypes.COMMA to MagmaColors.COMMA,
            MagmaTokenTypes.DOT to MagmaColors.DOT,
            MagmaTokenTypes.BAD_CHARACTER to MagmaColors.BAD_CHARACTER,
        )
    }
}
