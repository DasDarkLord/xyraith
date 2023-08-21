package server

sealed class Value {
    data class Number(val value: kotlin.Double) : Value()
    data class String(val value: kotlin.String) : Value()
    data class Symbol(val value: kotlin.String) : Value()
    object Null : Value()

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
        }
    }

    fun toDisplay(): kotlin.String {
        return when(this) {
            is Number -> java.text.DecimalFormat("#.##").format(value)
            is Null -> "null"
            is String -> value
            is Symbol -> ":$value"
        }
    }
}