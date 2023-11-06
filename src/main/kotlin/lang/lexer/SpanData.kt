package lang.lexer

data class SpanData(val spanStart: Int, val spanEnd: Int, val file: String = "unknownfile")