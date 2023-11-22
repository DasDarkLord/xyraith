package lang.lexer

sealed class Token {

    abstract val span: SpanData

    class LeftParen(override val span: SpanData) : Token()
    class RightParen(override val span: SpanData) : Token()
    class Identifier(val value: String, override val span: SpanData) : Token()
    class StringText(val value: String, override val span: SpanData) : Token()
    class Number(val value: Double, override val span: SpanData) : Token()
    class Symbol(val value: String, override val span: SpanData) : Token()
    class Code(val value: String, override val span: SpanData) : Token()
    class EOF(override val span: SpanData) : Token()
    class At(override val span: SpanData) : Token()
    class NewLine(override val span: SpanData) : Token()

    override fun toString(): String {
        return when(this) {
            is LeftParen -> """{"type":"leftParen","span":$span}"""
            is RightParen -> """{"type":"rightParen","span":$span}"""
            is Identifier -> """{"type":"identifier","value":"${this.value}","span":$span}"""
            is StringText -> """{"type":"string","value":"${this.value}","span":$span}"""
            is Number -> """{"type":"number","value":${this.value},"span":$span}"""
            is Symbol -> """{"type":"symbol","value":"${this.value}","span":$span}"""
            is Code -> """{"type":"code","value":"${this.value}","span":$span}"""
            is EOF -> """"""
            is At -> """{"type":"at","span":$span}"""
            is NewLine -> """{"type":"newLine","span":$span}"""
        }
    }
}