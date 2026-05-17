package com.renpe.intellij.magma.lang

import com.intellij.lexer.LexerBase
import com.intellij.psi.tree.IElementType

class MagmaLexer : LexerBase() {

    private var buffer: CharSequence = ""
    private var startOffset = 0
    private var endOffset = 0
    private var tokenStart = 0
    private var tokenEnd = 0
    private var tokenType: IElementType? = null
    private var state = 0

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        this.startOffset = startOffset
        this.endOffset = endOffset
        this.tokenStart = startOffset
        this.tokenEnd = startOffset
        this.tokenType = null
        this.state = initialState
        advance()
    }

    override fun getState(): Int = state
    override fun getTokenType(): IElementType? = tokenType
    override fun getTokenStart(): Int = tokenStart
    override fun getTokenEnd(): Int = tokenEnd
    override fun getBufferSequence(): CharSequence = buffer
    override fun getBufferEnd(): Int = endOffset

    override fun advance() {
        tokenStart = tokenEnd
        if (tokenStart >= endOffset) {
            tokenType = null
            return
        }
        val c = buffer[tokenStart]
        tokenType = when {
            c.isWhitespace() -> readWhitespace()
            c == '/' && peek(1) == '/' -> readLineComment()
            c == '/' && peek(1) == '*' -> readBlockComment()
            c == '"' -> readString('"', MagmaTokenTypes.STRING_DOUBLE)
            c == '\'' -> readString('\'', MagmaTokenTypes.STRING_SINGLE)
            c == '~' && isIdentStart(peek(1)) -> readTildeOrBacktick(MagmaTokenTypes.TILDE_VARIABLE)
            c == '`' && isIdentStart(peek(1)) -> readTildeOrBacktick(MagmaTokenTypes.BACKTICK_VARIABLE)
            c.isDigit() -> readNumber()
            c == '.' && peek(1)?.isDigit() == true -> readNumber()
            isIdentStart(c) -> readIdentifierOrKeyword()
            else -> readPunctuationOrOperator()
        }
    }

    private fun peek(offset: Int): Char? {
        val p = tokenEnd + offset
        return if (p in 0 until endOffset) buffer[p] else null
    }

    private fun isIdentStart(c: Char?): Boolean =
        c != null && (c.isLetter() || c == '_')

    private fun isIdentPart(c: Char?): Boolean =
        c != null && (c.isLetterOrDigit() || c == '_')

    private fun readWhitespace(): IElementType {
        while (tokenEnd < endOffset && buffer[tokenEnd].isWhitespace()) tokenEnd++
        return MagmaTokenTypes.WHITE_SPACE
    }

    private fun readLineComment(): IElementType {
        tokenEnd += 2
        while (tokenEnd < endOffset && buffer[tokenEnd] != '\n') tokenEnd++
        return MagmaTokenTypes.LINE_COMMENT
    }

    private fun readBlockComment(): IElementType {
        tokenEnd += 2
        while (tokenEnd < endOffset) {
            if (buffer[tokenEnd] == '*' && tokenEnd + 1 < endOffset && buffer[tokenEnd + 1] == '/') {
                tokenEnd += 2
                return MagmaTokenTypes.BLOCK_COMMENT
            }
            tokenEnd++
        }
        return MagmaTokenTypes.BLOCK_COMMENT
    }

    private fun readString(quote: Char, type: IElementType): IElementType {
        tokenEnd++ // opening quote
        while (tokenEnd < endOffset) {
            val ch = buffer[tokenEnd]
            if (ch == '\\' && tokenEnd + 1 < endOffset) {
                tokenEnd += 2
                continue
            }
            if (ch == quote) {
                tokenEnd++
                return type
            }
            if (ch == '\n') {
                // Unterminated string — stop at line end.
                return type
            }
            tokenEnd++
        }
        return type
    }

    private fun readTildeOrBacktick(type: IElementType): IElementType {
        tokenEnd++ // the ~ or `
        while (tokenEnd < endOffset && isIdentPart(buffer[tokenEnd])) tokenEnd++
        return type
    }

    private fun readNumber(): IElementType {
        // Hex literal
        if (buffer[tokenEnd] == '0' && tokenEnd + 1 < endOffset &&
            (buffer[tokenEnd + 1] == 'x' || buffer[tokenEnd + 1] == 'X')
        ) {
            tokenEnd += 2
            while (tokenEnd < endOffset && (buffer[tokenEnd].isDigit() ||
                        buffer[tokenEnd] in 'a'..'f' || buffer[tokenEnd] in 'A'..'F')
            ) tokenEnd++
            consumeNumberSuffix()
            return MagmaTokenTypes.NUMBER
        }
        // Decimal/float
        while (tokenEnd < endOffset && buffer[tokenEnd].isDigit()) tokenEnd++
        if (tokenEnd < endOffset && buffer[tokenEnd] == '.' &&
            (tokenEnd + 1 >= endOffset || buffer[tokenEnd + 1] != '.')
        ) {
            tokenEnd++
            while (tokenEnd < endOffset && buffer[tokenEnd].isDigit()) tokenEnd++
        }
        if (tokenEnd < endOffset && (buffer[tokenEnd] == 'e' || buffer[tokenEnd] == 'E')) {
            tokenEnd++
            if (tokenEnd < endOffset && (buffer[tokenEnd] == '+' || buffer[tokenEnd] == '-')) tokenEnd++
            while (tokenEnd < endOffset && buffer[tokenEnd].isDigit()) tokenEnd++
        }
        consumeNumberSuffix()
        return MagmaTokenTypes.NUMBER
    }

    private fun consumeNumberSuffix() {
        // L, l, UL, ul, u, U, F, f, ll, LL, ull, ULL — kept simple
        while (tokenEnd < endOffset && buffer[tokenEnd] in "LlUuFf") tokenEnd++
    }

    private fun readIdentifierOrKeyword(): IElementType {
        val start = tokenEnd
        while (tokenEnd < endOffset && isIdentPart(buffer[tokenEnd])) tokenEnd++
        val word = buffer.subSequence(start, tokenEnd).toString()

        return when (word) {
            in MagmaKeywords.CONTROL -> MagmaTokenTypes.KEYWORD_CONTROL
            in MagmaKeywords.FUNCTION_DECL -> MagmaTokenTypes.KEYWORD_FUNCTION_DECL
            in MagmaKeywords.STATEMENT -> MagmaTokenTypes.KEYWORD_STATEMENT
            in MagmaKeywords.OPERATOR_WORD -> MagmaTokenTypes.KEYWORD_OPERATOR_WORD
            in MagmaKeywords.CONSTANT -> MagmaTokenTypes.CONSTANT
            in MagmaKeywords.TYPE_CATEGORY -> MagmaTokenTypes.TYPE_CATEGORY
            in MagmaKeywords.BUILTIN_FUNCTION -> MagmaTokenTypes.BUILTIN_FUNCTION
            else -> MagmaTokenTypes.IDENTIFIER
        }
    }

    private fun readPunctuationOrOperator(): IElementType {
        val c = buffer[tokenEnd]
        // Multi-char operators first
        val two = if (tokenEnd + 1 < endOffset) "$c${buffer[tokenEnd + 1]}" else null
        when (two) {
            ":=" -> { tokenEnd += 2; return MagmaTokenTypes.ASSIGN }
            "..", "::", "->", "<=", ">=", "==", "!=", "//" -> { tokenEnd += 2; return MagmaTokenTypes.OPERATOR }
            "@@", "!!" -> { tokenEnd += 2; return MagmaTokenTypes.OPERATOR }
        }
        tokenEnd++
        return when (c) {
            '(' -> MagmaTokenTypes.LPAREN
            ')' -> MagmaTokenTypes.RPAREN
            '[' -> MagmaTokenTypes.LBRACK
            ']' -> MagmaTokenTypes.RBRACK
            '{' -> MagmaTokenTypes.LBRACE
            '}' -> MagmaTokenTypes.RBRACE
            ';' -> MagmaTokenTypes.SEMICOLON
            ',' -> MagmaTokenTypes.COMMA
            '.' -> MagmaTokenTypes.DOT
            ':' -> MagmaTokenTypes.COLON
            '+', '-', '*', '/', '%', '&', '|', '^', '<', '>', '=',
            '!', '#', '@', '?' -> MagmaTokenTypes.OPERATOR
            else -> MagmaTokenTypes.BAD_CHARACTER
        }
    }
}
