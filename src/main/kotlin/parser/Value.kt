package parser

import code.instructions.Visitable
import net.minestom.server.coordinate.Pos
import net.minestom.server.item.ItemStack
import registry.commandRegistry
import typechecker.ArgumentType
import typechecker.Typechecker

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

    data class GenericList(val value: List<Value>) : Value() {
        override fun toString(): kotlin.String {
            return """{"type":"generic_list","value":$value}"""
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

    data class Item(
        val itemStack: ItemStack,
    ) : Value() {
        override fun toString(): kotlin.String {
            return """{"type":"item","itemStack":"$itemStack"}"""
        }
    }

    data class Struct(
        val type: ArgumentType,
        val fields: MutableMap<kotlin.String, Value>
    ) : Value() {
        override fun toString(): kotlin.String {
            return """{"type":"user_defined","type":"$type","fields":"$fields"}"""
        }
    }

    data class StructField(
        val type: ArgumentType,
        val name: kotlin.String,
        val value: Value
    ) : Value() {
        override fun toString(): kotlin.String {
            return """{"type":"user_defined","type":"$type","name":"$name","value":"$value"}"""
        }
    }

    fun toDisplay(): kotlin.String {
        return when(this) {
            is Number -> "$value"
            is BasicBlockRef -> "bb{${this.value}}"
            is Null -> "null"
            is Block -> "UNAVAILABLE_DURING_RUNTIME_BLOCK"
            is Command -> "UNAVAILABLE_DURING_RUNTIME_COMMAND"
            is Selector -> value
            is String -> value
            is Symbol -> value
            is NumberList -> value.toString()
            is StringList -> value.toString()
            is Bool -> value.toString()
            is Struct -> "${type}{${fields}}"
            is StructField -> "structField{$name,$type,$value}"
            is Item -> "item{$itemStack}"
            is GenericList -> value.toString()
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

    fun castToArgumentType(): ArgumentType {
        return when(this) {
            is BasicBlockRef -> ArgumentType.BLOCK_REFERENCE
            is Block -> ArgumentType.BLOCK
            is Command -> ArgumentType.COMMAND
            Null -> ArgumentType.NULL
            is Number -> ArgumentType.NUMBER
            is Selector -> ArgumentType.SELECTOR
            is String -> ArgumentType.STRING
            is Symbol -> ArgumentType.SYMBOL
            is NumberList -> ArgumentType.NUMBER_LIST
            is StringList -> ArgumentType.STRING_LIST
            is Bool -> ArgumentType.BOOL
            is Struct -> type
            is StructField -> ArgumentType("structField", listOf())
            is Item -> ArgumentType.ITEM
            is GenericList -> ArgumentType.GENERIC_LIST
        }
    }

    fun getFixedType(tc: Typechecker, functionName: kotlin.String?): ArgumentType {
        return when(this) {
            is Command -> {
                if(this.value.name == "list") {
                    val generic = this.value.arguments[0].getFixedType(tc, functionName)
                    return ArgumentType("list", listOf(generic))
                }
                return tc.getCommandReturnType(this.value, null, functionName)
            }
            else -> this.castToArgumentType()
        }
    }
}