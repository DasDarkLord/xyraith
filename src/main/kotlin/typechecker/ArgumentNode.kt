package typechecker

import parser.Value

data class ArgumentList(val list: List<ArgumentNode>) : ArgumentNode

sealed interface ArgumentNode {
    data class SingleArgumentNode(val type: ArgumentType, val desc: String) : ArgumentNode
    data class OptionalArgumentNode(val type: ArgumentType, val defaultValue: Value, val desc: String) : ArgumentNode
    data class PluralArgumentNode(val type: ArgumentType, val desc: String) : ArgumentNode
}

class ArgumentType(private val typeName: String) {
    companion object {
        val NUMBER = ArgumentType("number")
        val STRING = ArgumentType("string")
        val COMMAND = ArgumentType("command")
        val LOCATION = ArgumentType("location")
        val SYMBOL = ArgumentType("symbol")
        val ANY = ArgumentType("any")
        val BLOCK = ArgumentType("block")
        val GENERIC_LIST = ArgumentType("list[any]")
        val NUMBER_LIST = ArgumentType("list[number]")
        val STRING_LIST = ArgumentType("list[string]")
        val SELECTOR = ArgumentType("selector")
        val NONE = ArgumentType("null")
        val ITEM = ArgumentType("itemStack")
        val BOOL = ArgumentType("boolean")
        val BLOCK_REFERENCE = ArgumentType("block_reference")
        val NULL = ArgumentType("null")
    }

    override fun toString(): String {
        return this.typeName
    }

    fun isEqualTypeTo(other: ArgumentType): Boolean {
        val out = when(true) {
            (this == GENERIC_LIST && other.typeName.startsWith("list")) -> true
            (other == GENERIC_LIST && this.typeName.startsWith("list")) -> true
            (other == ANY) -> true
            (this == ANY) -> true
            (this == other) -> true
            else -> false
        }
        return out
    }

    override fun equals(other: Any?): Boolean {
        if(other !is ArgumentType) return false
        return this.typeName == other.typeName
    }

    override fun hashCode(): Int {
        return typeName.hashCode()
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

