package typechecker

import parser.Value
import java.lang.Exception

data class ArgumentList(val list: List<ArgumentNode>) : ArgumentNode

sealed interface ArgumentNode {
    data class SingleArgumentNode(val type: ArgumentType, val desc: String) : ArgumentNode
    data class OptionalArgumentNode(val type: ArgumentType, val defaultValue: Value, val desc: String) : ArgumentNode
    data class PluralArgumentNode(val type: ArgumentType, val desc: String) : ArgumentNode
}

enum class ArgumentType {
    NUMBER,
    STRING,
    COMMAND,
    LOCATION,
    SYMBOL,
    ANY,
    BLOCK,
    GENERIC_LIST,
    NUMBER_LIST,
    STRING_LIST,
    SELECTOR,
    NONE,
    ITEM,
    BOOL,

    BLOCK_REFERENCE,
    NULL,
    ;

    override fun toString(): String {
        return when(this) {
            NUMBER -> "Number"
            STRING -> "String"
            COMMAND -> "Command"
            LOCATION -> "Location"
            SYMBOL -> "Symbol"
            ANY -> "Any Value"
            BLOCK -> "Block"
            BLOCK_REFERENCE -> "Basic Block Reference"
            NULL -> "Null"
            GENERIC_LIST -> "Any List"
            NUMBER_LIST -> "Number List"
            STRING_LIST -> "String List"
            SELECTOR -> "Selector"
            NONE -> "Null"
            ITEM -> "Item Stack"
            BOOL -> "Boolean"
        }
    }

    fun isEqualTo(other: ArgumentType): Boolean {
        println("comparing $this == $other")
        val out = when(true) {
            (this == GENERIC_LIST && (other == NUMBER_LIST || other == STRING_LIST)) -> true
            (other == GENERIC_LIST && (this == NUMBER_LIST || this == STRING_LIST)) -> true
            (other == ANY) -> true
            (this == ANY) -> true
            (this == other) -> true
            else -> false
        }
        println("result: $out")
        try {
            throw KotlinNullPointerException()
        } catch(e: Exception) {
            e.printStackTrace()
        }
        return out

    }
}

class NodeBuilder {
    private val nodes: MutableList<ArgumentNode> = mutableListOf()

    fun addSingleArgument(type: ArgumentType, description: String): NodeBuilder {
        this.nodes.add(ArgumentNode.SingleArgumentNode(type, description))
        return this
    }

    fun addOptionalArgument(type: ArgumentType, defaultValue: Value, description: String): NodeBuilder {
        this.nodes.add(ArgumentNode.OptionalArgumentNode(type, defaultValue, description))
        return this
    }

    fun addPluralArgument(type: ArgumentType, description: String): NodeBuilder {
        this.nodes.add(ArgumentNode.PluralArgumentNode(type, description))
        return this
    }

    fun build(): ArgumentList = ArgumentList(this.nodes)
}

