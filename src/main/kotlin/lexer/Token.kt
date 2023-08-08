package lexer

sealed class Token() {

    abstract val spanStart: Int
    abstract val spanEnd: Int

    class LeftParen(override val spanStart: Int = 0, override val spanEnd: Int = 0) : Token()
    class RightParen(override val spanStart: Int = 0, override val spanEnd: Int = 0) : Token()
    class Identifier(val value: String, override val spanStart: Int = 0, override val spanEnd: Int = 0) : Token()
    class StringText(val value: String, override val spanStart: Int = 0, override val spanEnd: Int = 0) : Token()
    class Number(val value: Double, override val spanStart: Int = 0, override val spanEnd: Int = 0) : Token()
    class Symbol(val value: String, override val spanStart: Int = 0, override val spanEnd: Int = 0) : Token()

    override fun toString(): String {
        return when(this) {
            is LeftParen -> """{"type":"leftParen","spanStart":$spanStart,"spanEnd":$spanEnd}"""
            is RightParen -> """{"type":"rightParen","spanStart":$spanStart,"spanEnd":$spanEnd}"""
            is Identifier -> """{"type":"ident","value":"${this.value}","spanStart":$spanStart,"spanEnd":$spanEnd}"""
            is StringText -> """{"type":"string","value":"${this.value}","spanStart":$spanStart,"spanEnd":$spanEnd}"""
            is Number -> """{"type":"number","value":${this.value},"spanStart":$spanStart,"spanEnd":$spanEnd}"""
            is Symbol -> """{"type":"symbol","value":"${this.value}","spanStart":$spanStart,"spanEnd":$spanEnd}"""
        }
    }

    fun toType(): TokenType {
        return when(this) {
            is LeftParen -> TokenType.LeftParen
            is RightParen -> TokenType.RightParen
            is Identifier -> TokenType.Identifier()
            is StringText -> TokenType.StringText()
            is Number -> TokenType.Number()
            is Symbol -> TokenType.Symbol()
        }
    }
}