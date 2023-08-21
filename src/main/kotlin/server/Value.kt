package server

open class Value {
    class Number(val value: kotlin.Double) : Value()
    class String(val value: kotlin.String) : Value()
    class Symbol(val value: kotlin.String) : Value()
    class Null : Value()

    fun toNumber(): Double {
        return when(this) {
            is Number -> return this.value
            else -> 0.0
        }
    }

    override fun toString(): kotlin.String {
        return when(this) {
            is Number -> "$value"
            is Null -> "null"
            is String -> "\"$value\""
            is Symbol -> ":$value"
            else -> "unknown"
        }
    }

    fun toDisplay(): kotlin.String {
        return when(this) {
            is Number -> java.text.DecimalFormat("#.##").format(value)
            is Null -> "null"
            is String -> value
            is Symbol -> ":$value"
            else -> "unknown"
        }
    }
}