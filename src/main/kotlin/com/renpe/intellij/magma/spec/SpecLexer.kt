package com.renpe.intellij.magma.spec

import com.renpe.intellij.magma.lang.MagmaTokenTypes
import com.intellij.lexer.LexerBase
import com.intellij.psi.tree.IElementType

class SpecLexer : LexerBase() {

    private var buffer: CharSequence = ""
    private var startOffset = 0
    private var endOffset = 0
    private var tokenStart = 0
    private var tokenEnd = 0
    private var tokenType: IElementType? = null

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        this.startOffset = startOffset
        this.endOffset = endOffset
        this.tokenStart = startOffset
        this.tokenEnd = startOffset
        this.tokenType = null
        advance()
    }

    override fun getState(): Int = 0
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
            c.isWhitespace() -> {
                while (tokenEnd < endOffset && buffer[tokenEnd].isWhitespace()) tokenEnd++
                MagmaTokenTypes.WHITE_SPACE
            }
            c == '+' && tokenEnd + 1 < endOffset &&
                (buffer[tokenEnd + 1].isLetter() || buffer[tokenEnd + 1] == '_') -> {
                tokenEnd++ // the +
                while (tokenEnd < endOffset &&
                    (buffer[tokenEnd].isLetterOrDigit() || buffer[tokenEnd] == '_')) tokenEnd++
                MagmaTokenTypes.INCLUDE_DIRECTIVE
            }
            c.isLetter() || c == '_' -> {
                while (tokenEnd < endOffset &&
                    (buffer[tokenEnd].isLetterOrDigit() || buffer[tokenEnd] == '_')) tokenEnd++
                MagmaTokenTypes.IDENTIFIER
            }
            else -> {
                tokenEnd++
                MagmaTokenTypes.BAD_CHARACTER
            }
        }
    }
}
