package typechecker

import parser.Value

data class ArgumentList(val list: List<ArgumentNode>) : ArgumentNode

sealed interface ArgumentNode {
    data class SingleArgumentNode(val type: ArgumentType, val desc: String) : ArgumentNode
    data class OptionalArgumentNode(val type: ArgumentType, val defaultValue: Value, val desc: String) : ArgumentNode
    data class OptionalPluralArgumentNode(val type: ArgumentType, val desc: String) : ArgumentNode
    data class PluralArgumentNode(val type: ArgumentType, val desc: String) : ArgumentNode
}

class ArgumentType(private val typeName: String, val genericTypes: List<ArgumentType>) {
    companion object {
        val NUMBER = ArgumentType("number", listOf())
        val STRING = ArgumentType("string", listOf())
        val COMMAND = ArgumentType("command", listOf())
        val LOCATION = ArgumentType("location", listOf())
        val SYMBOL = ArgumentType("symbol", listOf())
        val ANY = ArgumentType("any", listOf())
        val BLOCK = ArgumentType("block", listOf())
        val GENERIC_LIST = ArgumentType("list", listOf(ANY))
        val NUMBER_LIST = ArgumentType("list", listOf(NUMBER))
        val STRING_LIST = ArgumentType("list", listOf(STRING))
        val SELECTOR = ArgumentType("selector", listOf())
        val NONE = ArgumentType("nothing", listOf())
        val ITEM = ArgumentType("itemStack", listOf())
        val BOOL = ArgumentType("boolean", listOf())
        val BLOCK_REFERENCE = ArgumentType("block_reference", listOf())
        val NULL = ArgumentType("null", listOf())
    }

    override fun toString(): String {
        if(this.genericTypes.isEmpty()) return this.typeName
        return this.typeName + this.genericTypes
    }

    fun toTypeName(): String {
        return this.typeName
    }

    fun isEqualTypeTo(other: ArgumentType): Boolean {
        val out = when(true) {
            (this == GENERIC_LIST && other.typeName == "list") -> true
            (other == GENERIC_LIST && this.typeName == "list") -> true
            (other == ANY) -> true
            (this == ANY) -> true
            (this == other) -> true
            else -> false
        }
        return out
    }

    override fun equals(other: Any?): Boolean {
        if(other !is ArgumentType) return false
        return this.typeName == other.typeName && this.genericTypes == other.genericTypes
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

    fun addOptionalPluralArgument(type: ArgumentType, description: String): NodeBuilder {
        this.nodes.add(ArgumentNode.OptionalPluralArgumentNode(type, description))
        return this
    }

    fun addPluralArgument(type: ArgumentType, description: String): NodeBuilder {
        this.nodes.add(ArgumentNode.PluralArgumentNode(type, description))
        return this
    }

    fun build(): ArgumentList = ArgumentList(this.nodes)
}

