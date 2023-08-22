package ir

class BasicBlock(val id: Int, val code: MutableList<Node>, val eventId: String) {
    override fun toString(): String {
        return """{"type":"basicBlock","id":$id,"code":$code}"""
    }
    fun display(): String {
        var display = ""
        code.forEach {
            display = "$display${it.display()}"
        }
        return "  @$id:\n$display"
    }
}
class Node(val id: Int, var name: String, var arguments: List<Argument>) {
    override fun toString(): String {
        return """{"type":"node","id":$id,"name":"$name","arguments":$arguments}"""
    }
    fun display(): String {
        var display = ""
        arguments.forEach {
            display = "$display${it.display()}, "
        }
        display = display.removeSuffix(", ")
        return "    %$id = $name $display\n"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Node) return false

        return id == other.id &&
                name == other.name &&
                arguments == other.arguments
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + arguments.hashCode()
        return result
    }
}
sealed class Argument {
    abstract fun display(): kotlin.String
    data class Number(val value: Double) : Argument() {
        override fun toString(): kotlin.String {
            return """{"type":"number","value":$value}"""
        }
        override fun display(): kotlin.String {
            return "$value"
        }
    }
    data class String(val value: kotlin.String) : Argument() {
        override fun toString(): kotlin.String {
            return """{"type":"string","value":"$value"}"""
        }
        override fun display(): kotlin.String {
            return "\"$value\""
        }
    }
    data class Symbol(val value: kotlin.String) : Argument() {
        override fun toString(): kotlin.String {
            return """{"type":"symbol","value":"$value"}"""
        }
        override fun display(): kotlin.String {
            return value
        }
    }
    data class Selector(val value: kotlin.String) : Argument() {
        override fun toString(): kotlin.String {
            return """{"type":"selector","value":"$value"}"""
        }
        override fun display(): kotlin.String {
            return "@$value"
        }
    }

    data class SSARef(val value: Int) : Argument() {
        override fun toString(): kotlin.String {
            return """{"type":"ssaRef","value":$value}"""
        }
        override fun display(): kotlin.String {
            return "%$value"
        }
    }
    data class BasicBlockRef(val value: Int) : Argument() {
        override fun toString(): kotlin.String {
            return """{"type":"basicBlockRef","value":$value}"""
        }
        override fun display(): kotlin.String {
            return "@$value"
        }
    }
}