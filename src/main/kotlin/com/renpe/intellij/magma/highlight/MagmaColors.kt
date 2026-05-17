package com.renpe.intellij.magma.highlight

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey

object MagmaColors {
    val LINE_COMMENT = key("MAGMA_LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
    val BLOCK_COMMENT = key("MAGMA_BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT)
    val STRING = key("MAGMA_STRING", DefaultLanguageHighlighterColors.STRING)
    val NUMBER = key("MAGMA_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
    val IDENTIFIER = key("MAGMA_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER)

    val KEYWORD_CONTROL = key("MAGMA_KEYWORD_CONTROL", DefaultLanguageHighlighterColors.KEYWORD)
    val KEYWORD_FUNCTION_DECL = key("MAGMA_KEYWORD_FUNCTION_DECL", DefaultLanguageHighlighterColors.KEYWORD)
    val KEYWORD_STATEMENT = key("MAGMA_KEYWORD_STATEMENT", DefaultLanguageHighlighterColors.KEYWORD)
    val KEYWORD_OPERATOR_WORD = key("MAGMA_KEYWORD_OPERATOR_WORD", DefaultLanguageHighlighterColors.KEYWORD)
    val CONSTANT = key("MAGMA_CONSTANT", DefaultLanguageHighlighterColors.CONSTANT)
    val TYPE_CATEGORY = key("MAGMA_TYPE_CATEGORY", DefaultLanguageHighlighterColors.CLASS_NAME)
    val BUILTIN_FUNCTION = key("MAGMA_BUILTIN_FUNCTION", DefaultLanguageHighlighterColors.PREDEFINED_SYMBOL)

    val TILDE_VARIABLE = key("MAGMA_TILDE_VARIABLE", DefaultLanguageHighlighterColors.LOCAL_VARIABLE)
    val BACKTICK_VARIABLE = key("MAGMA_BACKTICK_VARIABLE", DefaultLanguageHighlighterColors.INSTANCE_FIELD)

    val OPERATOR = key("MAGMA_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
    val PARENTHESES = key("MAGMA_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES)
    val BRACKETS = key("MAGMA_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS)
    val BRACES = key("MAGMA_BRACES", DefaultLanguageHighlighterColors.BRACES)
    val SEMICOLON = key("MAGMA_SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON)
    val COMMA = key("MAGMA_COMMA", DefaultLanguageHighlighterColors.COMMA)
    val DOT = key("MAGMA_DOT", DefaultLanguageHighlighterColors.DOT)

    val INCLUDE_DIRECTIVE = key("MAGMA_SPEC_INCLUDE", DefaultLanguageHighlighterColors.METADATA)

    val BAD_CHARACTER = TextAttributesKey.createTextAttributesKey(
        "MAGMA_BAD_CHARACTER",
        com.intellij.openapi.editor.HighlighterColors.BAD_CHARACTER,
    )

    private fun key(name: String, fallback: TextAttributesKey): TextAttributesKey =
        TextAttributesKey.createTextAttributesKey(name, fallback)
}
