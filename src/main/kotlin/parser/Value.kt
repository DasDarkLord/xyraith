package parser

import net.minestom.server.coordinate.Pos
import typechecker.ArgumentType

sealed class Value {
    /*
    These values are both used during runtime & compilation
     */
    data class Number(val value: Double) : Value() {
        override fun toString(): kotlin.String {
            return """{"type":"number","value":$value}"""
        }
    }
    data class String(val value: kotlin.String) : Value() {
        override fun toString(): kotlin.String {
            return """{"type":"string","value":"$value"}"""
        }
    }
    data class Selector(val value: kotlin.String) : Value() {
        override fun toString(): kotlin.String {
            return """{"type":"selector","value":"$value"}"""
        }
    }
    data class Symbol(val value: kotlin.String) : Value() {
        override fun toString(): kotlin.String {
            return """{"type":"symbol","value":"$value"}"""
        }
    }
    data class Command(val value: Ast.Command) : Value() {
        override fun toString(): kotlin.String {
            return """{"type":"command","value":$value}"""
        }
    }
    data class Block(val value: Ast.Block) : Value() {
        override fun toString(): kotlin.String {
            return """{"type":"block","value":$value}"""
        }
    }

    /*
    These values are only used during runtime
     */
    data class BasicBlockRef(val value: Int) : Value() {
        override fun toString(): kotlin.String {
            return """{"type":"basicBlockRef","value":$value}"""
        }
    }

    data class NumberList(val value: List<Number>) : Value() {
        override fun toString(): kotlin.String {
            return """{"type":"string_list","value":$value}"""
        }
    }

    data class StringList(val value: List<String>) : Value() {
        override fun toString(): kotlin.String {
            return """{"type":"string_list","value":$value}"""
        }
    }

    object Null : Value() {
        override fun toString(): kotlin.String {
            return """{"type":"null","value":"null"}"""
        }
    }

    data class Bool(val value: kotlin.Boolean) : Value() {
        override fun toString(): kotlin.String {
            return """{"type":"bool","value":$value}"""
        }
    }

    data class Position(
        val x: Double,
        val y: Double,
        val z: Double,
        val pitch: Double = 0.0,
        val yaw: Double = 0.0
    ) : Value() {
        override fun toString(): kotlin.String {
            return """{"type":"position","value":{"x":$x,"y":$y,"z":$z,"pitch":$pitch,"yaw":$yaw}}"""
        }
    }

    fun toDisplay(): kotlin.String {
        return when(this) {
            is Number -> "$value"
            is Position -> "<$x, $y, $z, $pitch, $yaw>"
            is BasicBlockRef -> "bb{${this.value}}"
            is Null -> "NUL"
            is Block -> "UNAVAILABLE_DURING_RUNTIME_BLOCK"
            is Command -> "UNAVAILABLE_DURING_RUNTIME_COMMAND"
            is Selector -> value
            is String -> value
            is Symbol -> value
            is NumberList -> value.toString()
            is StringList -> value.toString()
            is Bool -> value.toString()
        }
    }

    fun castToNumber(): Double =
        if(this is Number)
            value
        else
            0.0
    fun castToString(): kotlin.String =
        if(this is String)
            value
        else
            toDisplay()
    fun castToPos(): Pos =
        if(this is Position)
            Pos(this.x, this.y, this.z, this.pitch.toFloat(), this.yaw.toFloat())
        else
            Pos(0.0, 0.0, 0.0, 0.0f, 0.0f)

    fun castToArgumentType(): ArgumentType {
        return when(this) {
            is BasicBlockRef -> ArgumentType.BLOCK_REFERENCE
            is Block -> ArgumentType.BLOCK
            is Command -> ArgumentType.COMMAND
            Null -> ArgumentType.NULL
            is Number -> ArgumentType.NUMBER
            is Position -> ArgumentType.COMMAND
            is Selector -> ArgumentType.SELECTOR
            is String -> ArgumentType.STRING
            is Symbol -> ArgumentType.SYMBOL
            is NumberList -> ArgumentType.NUMBER_LIST
            is StringList -> ArgumentType.STRING_LIST
            is Bool -> ArgumentType.BOOL
        }
    }

    fun castToCommandName(): kotlin.String {
        if(this is Value.Command) {
            return this.value.name
        }
        return ""
    }
}