package ir

import parser.Ast
import parser.Value

class BasicBlock(val id: Int, val code: MutableList<Node>) {
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
class Node(val id: Int, val name: String, val arguments: List<Argument>) {
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
}
sealed class Argument {
    abstract fun display(): kotlin.String
    class Number(val value: Double) : Argument() {
        override fun toString(): kotlin.String {
            return """{"type":"number","value":$value}"""
        }
        override fun display(): kotlin.String {
            return "$value"
        }

    }
    class String(val value: kotlin.String) : Argument() {
        override fun toString(): kotlin.String {
            return """{"type":"string","value":"$value"}"""
        }
        override fun display(): kotlin.String {
            return "\"$value\""
        }
    }
    class Symbol(val value: kotlin.String) : Argument() {
        override fun toString(): kotlin.String {
            return """{"type":"symbol","value":"$value"}"""
        }
        override fun display(): kotlin.String {
            return "$value"
        }
    }

    class SSARef(val value: Int) : Argument() {
        override fun toString(): kotlin.String {
            return """{"type":"ssaRef","value":$value}"""
        }
        override fun display(): kotlin.String {
            return "%$value"
        }
    }
    class BasicBlockRef(val value: Int) : Argument() {
        override fun toString(): kotlin.String {
            return """{"type":"basicBlockRef","value":$value}"""
        }
        override fun display(): kotlin.String {
            return "@$value"
        }
    }
}