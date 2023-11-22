package lang.lexer

sealed class Token {

    abstract val span: SpanData

    class LeftParen(override val span: SpanData) : Token()
    class RightParen(override val span: SpanData) : Token()
    class Identifier(val value: String, override val span: SpanData) : Token()
    class StringText(val value: String, override val span: SpanData) : Token()
    class Number(val value: Double, override val span: SpanData) : Token()
    class Colon(override val span: SpanData) : Token()
    class EOF(override val span: SpanData) : Token()
    class At(override val span: SpanData) : Token()
    class NewLine(override val span: SpanData) : Token()
    class Equals(override val span: SpanData) : Token()
    class Bang(override val span: SpanData) : Token()
    class ForEachKeyword(override val span: SpanData) : Token()
    class IfKeyword(override val span: SpanData) : Token()
    override fun toString(): String {
        return when(this) {
            is LeftParen -> """{"type":"leftParen","span":$span}"""
            is RightParen -> """{"type":"rightParen","span":$span}"""
            is Identifier -> """{"type":"identifier","value":"${this.value}","span":$span}"""
            is StringText -> """{"type":"string","value":"${this.value}","span":$span}"""
            is Number -> """{"type":"number","value":${this.value},"span":$span}"""

            is EOF -> """{"type":"eof","span":$span}"""
            is NewLine -> """{"type":"newLine","span":$span}"""

            is ForEachKeyword -> """{"type":"foreach","span":$span}"""
            is IfKeyword -> """{"type":"if","span":$span}"""

            is Colon -> """{"type":"colon","span":$span}"""
            is Equals -> """{"type":"equals","span":$span}"""
            is Bang -> """{"type":"bang","span":$span}"""
            is At -> """{"type":"at","span":$span}"""
        }
    }
}