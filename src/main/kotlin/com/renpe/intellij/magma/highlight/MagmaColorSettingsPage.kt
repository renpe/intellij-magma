package com.renpe.intellij.magma.highlight

import com.renpe.intellij.magma.lang.MagmaIcons
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import javax.swing.Icon

class MagmaColorSettingsPage : ColorSettingsPage {
    override fun getIcon(): Icon = MagmaIcons.FILE
    override fun getHighlighter(): SyntaxHighlighter = MagmaSyntaxHighlighter()
    override fun getDisplayName(): String = "Magma"
    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = DESCRIPTORS
    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY
    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey> = emptyMap()

    override fun getDemoText(): String = """
        // Magma sample
        /* Compute the order of a permutation group */
        intrinsic Demo(G :: GrpPerm) -> RngIntElt
        { Returns the order of G. }
            n := #G;
            assert n gt 0;
            return n;
        end intrinsic;

        f := function(x, y)
            if x eq 0 or y in {1, 2, 3} then
                return true;
            end if;
            return false;
        end function;

        S := Sylow(G, 2);
        printf "Order: %o\n", #S;

        // Built-ins like Sylow, AbelianGroup, IsPrime, Factorization are
        // highlighted as predefined symbols.
        p := IsPrime(2017);
        F := Factorization(2024);
    """.trimIndent()

    private companion object {
        val DESCRIPTORS: Array<AttributesDescriptor> = arrayOf(
            AttributesDescriptor("Comments//Line comment", MagmaColors.LINE_COMMENT),
            AttributesDescriptor("Comments//Block comment", MagmaColors.BLOCK_COMMENT),
            AttributesDescriptor("Strings//String", MagmaColors.STRING),
            AttributesDescriptor("Numbers", MagmaColors.NUMBER),
            AttributesDescriptor("Identifiers//Identifier", MagmaColors.IDENTIFIER),
            AttributesDescriptor("Keywords//Control flow (if, for, while, ...)", MagmaColors.KEYWORD_CONTROL),
            AttributesDescriptor("Keywords//Function declaration (function, procedure, intrinsic)", MagmaColors.KEYWORD_FUNCTION_DECL),
            AttributesDescriptor("Keywords//Statement (print, assert, import, ...)", MagmaColors.KEYWORD_STATEMENT),
            AttributesDescriptor("Keywords//Operator word (and, or, eq, ...)", MagmaColors.KEYWORD_OPERATOR_WORD),
            AttributesDescriptor("Constants (true, false, NULL)", MagmaColors.CONSTANT),
            AttributesDescriptor("Type category", MagmaColors.TYPE_CATEGORY),
            AttributesDescriptor("Built-in function", MagmaColors.BUILTIN_FUNCTION),
            AttributesDescriptor("Variables//Tilde reference (~x)", MagmaColors.TILDE_VARIABLE),
            AttributesDescriptor("Variables//Backtick attribute (`x)", MagmaColors.BACKTICK_VARIABLE),
            AttributesDescriptor("Operators//Operator sign", MagmaColors.OPERATOR),
            AttributesDescriptor("Operators//Parentheses", MagmaColors.PARENTHESES),
            AttributesDescriptor("Operators//Brackets", MagmaColors.BRACKETS),
            AttributesDescriptor("Operators//Braces", MagmaColors.BRACES),
            AttributesDescriptor("Operators//Semicolon", MagmaColors.SEMICOLON),
            AttributesDescriptor("Operators//Comma", MagmaColors.COMMA),
            AttributesDescriptor("Operators//Dot", MagmaColors.DOT),
            AttributesDescriptor("Bad character", MagmaColors.BAD_CHARACTER),
        )
    }
}
