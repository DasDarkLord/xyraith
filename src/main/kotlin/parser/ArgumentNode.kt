package parser

interface ArgumentNode

enum class ArgumentType {
    NUMBER,
    STRING,
    COMMAND,
    LOCATION,
    SYMBOL,
    ANY,
    BLOCK,
    LIST,
    SELECTOR,
    NONE,
    ITEM,
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
            LIST -> "List"
            SELECTOR -> "Selector"
            NONE -> "Null"
            ITEM -> "Item Stack"
        }
    }
}

class NodeBuilder {
    private val nodes: MutableList<ArgumentNode> = mutableListOf()

    fun addSingleArgument(type: ArgumentType, description: String): NodeBuilder {
        this.nodes.add(SingleArgumentNode(type, description))
        return this
    }

    fun addOptionalArgument(type: ArgumentType, defaultValue: Value, description: String): NodeBuilder {
        this.nodes.add(OptionalArgumentNode(type, defaultValue, description))
        return this
    }

    fun addPluralArgument(type: ArgumentType, description: String): NodeBuilder {
        this.nodes.add(PluralArgumentNode(type, description))
        return this
    }

    fun build(): ArgumentList = ArgumentList(this.nodes)
}

data class ArgumentList(val list: List<ArgumentNode>) : ArgumentNode
data class SingleArgumentNode(val type: ArgumentType, val desc: String) : ArgumentNode
data class OptionalArgumentNode(val type: ArgumentType, val defaultValue: Value, val desc: String) : ArgumentNode
data class PluralArgumentNode(val type: ArgumentType, val desc: String) : ArgumentNode