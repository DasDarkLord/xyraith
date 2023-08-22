package server

sealed class Value {
    data class Number(val value: kotlin.Double) : Value()
    data class String(val value: kotlin.String) : Value()
    data class Symbol(val value: kotlin.String) : Value()
    data class Position(val x: Double, val y: Double, val z: Double, val pitch: Double = 0.0, val yaw: Double = 0.0) : Value()
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
            is Position -> "<$x, $y, $z, $pitch, $yaw>"
        }
    }

    fun toDisplay(): kotlin.String {
        return when(this) {
            is Number -> java.text.DecimalFormat("#.##").format(value)
            is Null -> "null"
            is String -> value
            is Symbol -> ":$value"
            is Position -> "<$x, $y, $z, $pitch, $yaw>"
        }
    }
}