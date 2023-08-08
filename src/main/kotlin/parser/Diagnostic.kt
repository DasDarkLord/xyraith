package parser

class Diagnostic(val errorCode: Int, val problem: String, val spanStart: Int, val spanEnd: Int) {
    override fun toString(): String {
        return """
[E$errorCode] $problem
| Span: $spanStart..$spanEnd
        """.trimIndent()
    }
}