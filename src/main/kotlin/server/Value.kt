package server

open class Value {
    class Number(val value: kotlin.Double) : Value()
    class Null : Value()

    fun toNumber(): Double {
        return when(this) {
            is Number -> return this.value
            else -> 0.0
        }
    }

    override fun toString(): String {
        return when(this) {
            is Number -> return "${this.value}"
            is Null -> "null"
            else -> "unknown"
        }
    }
}