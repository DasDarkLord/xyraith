package parser

sealed class Ast {
    class Event(val name: String, val code: Block, val type: String) : Ast() {
        override fun toString(): String {
            return """{"name": "$name","type":"$type","code": $code}""".trimIndent()
        }
    }
    class Block(val nodes: List<Ast.Command>, val eventName: String) : Ast() {
        override fun toString(): String {
            return """{"name":"$eventName","nodes":$nodes}""".trimIndent()
        }
    }
    class Command(val name: String, val arguments: MutableList<Value>) : Ast() {
        override fun toString(): String {
            return """{"name":"$name","arguments":$arguments}"""
        }
    }
}

