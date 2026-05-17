package com.renpe.intellij.magma.lang

import com.intellij.psi.tree.IElementType
import com.intellij.psi.TokenType

class MagmaTokenType(debugName: String) : IElementType(debugName, MagmaLanguage)

object MagmaTokenTypes {
    val WHITE_SPACE: IElementType = TokenType.WHITE_SPACE
    val BAD_CHARACTER: IElementType = TokenType.BAD_CHARACTER

    val LINE_COMMENT = MagmaTokenType("LINE_COMMENT")
    val BLOCK_COMMENT = MagmaTokenType("BLOCK_COMMENT")

    val STRING_DOUBLE = MagmaTokenType("STRING_DOUBLE")
    val STRING_SINGLE = MagmaTokenType("STRING_SINGLE")
    val NUMBER = MagmaTokenType("NUMBER")

    val IDENTIFIER = MagmaTokenType("IDENTIFIER")
    val TILDE_VARIABLE = MagmaTokenType("TILDE_VARIABLE")
    val BACKTICK_VARIABLE = MagmaTokenType("BACKTICK_VARIABLE")

    val KEYWORD_CONTROL = MagmaTokenType("KEYWORD_CONTROL")
    val KEYWORD_FUNCTION_DECL = MagmaTokenType("KEYWORD_FUNCTION_DECL")
    val KEYWORD_STATEMENT = MagmaTokenType("KEYWORD_STATEMENT")
    val KEYWORD_OPERATOR_WORD = MagmaTokenType("KEYWORD_OPERATOR_WORD")
    val CONSTANT = MagmaTokenType("CONSTANT")
    val TYPE_CATEGORY = MagmaTokenType("TYPE_CATEGORY")
    val BUILTIN_FUNCTION = MagmaTokenType("BUILTIN_FUNCTION")

    val OPERATOR = MagmaTokenType("OPERATOR")
    val ASSIGN = MagmaTokenType("ASSIGN")

    val LPAREN = MagmaTokenType("LPAREN")
    val RPAREN = MagmaTokenType("RPAREN")
    val LBRACK = MagmaTokenType("LBRACK")
    val RBRACK = MagmaTokenType("RBRACK")
    val LBRACE = MagmaTokenType("LBRACE")
    val RBRACE = MagmaTokenType("RBRACE")
    val SEMICOLON = MagmaTokenType("SEMICOLON")
    val COMMA = MagmaTokenType("COMMA")
    val DOT = MagmaTokenType("DOT")
    val COLON = MagmaTokenType("COLON")

    val INCLUDE_DIRECTIVE = MagmaTokenType("INCLUDE_DIRECTIVE")
}
