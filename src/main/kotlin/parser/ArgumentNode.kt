package parser

interface ArgumentNode

enum class ArgumentType {
    NUMBER,
    STRING;

    override fun toString(): String {
        return when(this) {
            NUMBER -> "Number"
            STRING -> "String"
        }
    }
}

class NodeBuilder {
    val nodes: MutableList<ArgumentNode> = mutableListOf()

    fun addSingleArgument(type: ArgumentType): NodeBuilder {
        this.nodes.add(SingleArgumentNode(type))
        return this
    }

    fun build(): ArgumentList = ArgumentList(this.nodes)
}

data class ArgumentList(val list: List<ArgumentNode>) : ArgumentNode
data class SingleArgumentNode(val type: ArgumentType) : ArgumentNode