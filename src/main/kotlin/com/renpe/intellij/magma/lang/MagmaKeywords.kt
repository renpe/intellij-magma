package com.renpe.intellij.magma.lang

/**
 * Keyword sets derived from the original VS Code TextMate grammar
 * (syntaxes/magma.tmLanguage). The lexer looks identifiers up here to
 * decide which token type to emit.
 */
internal object MagmaKeywords {

    val CONTROL: Set<String> = setOf(
        "if", "then", "elif", "else", "while", "for", "return", "where",
        "do", "select", "when", "repeat", "until", "break", "continue",
        "try", "catch", "case", "end",
    )

    val FUNCTION_DECL: Set<String> = setOf(
        "function", "procedure", "intrinsic",
    )

    val STATEMENT: Set<String> = setOf(
        "print", "hom", "quo", "map", "pmap", "iso", "sub", "freeze",
        "rec", "recformat", "car", "assert", "assert2", "assert3",
        "local", "exists", "forall", "require", "requirerange",
        "requirege", "import", "assigned", "save", "restore", "quit",
        "iload", "printf", "fprintf", "exit", "read", "readi", "time",
        "forward", "error", "clear", "vprint", "vprintf", "vtime",
        "func", "declare", "attributes", "verbose", "eval", "load",
    )

    val OPERATOR_WORD: Set<String> = setOf(
        "by", "to", "eq", "not", "lt", "gt", "le", "ge", "cmpeq",
        "cmpne", "mod", "is", "or", "xor", "and", "in", "notin",
        "delete", "div", "cat", "join", "subset", "notsubset", "meet",
        "diff", "sdiff", "ne",
    )

    val CONSTANT: Set<String> = setOf(
        "NULL", "true", "false", "TRUE", "FALSE",
    )

    /** ~720 Magma category/type identifiers — see [MagmaTypes]. */
    val TYPE_CATEGORY: Set<String> get() = MagmaTypes.NAMES

    /** ~4600 Magma built-in function names — see [MagmaBuiltins]. */
    val BUILTIN_FUNCTION: Set<String> get() = MagmaBuiltins.NAMES
}
