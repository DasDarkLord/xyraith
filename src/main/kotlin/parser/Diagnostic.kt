package parser

import lexer.SpanData

class Diagnostic(val errorCode: Int, val problem: String, val span: SpanData) {
    override fun toString(): String {
        val (spanStart, spanEnd, file) = span
        return """
[E$errorCode] $problem
| Span: $spanStart..$spanEnd ($file.xyr)
        """.trimIndent()
    }
}