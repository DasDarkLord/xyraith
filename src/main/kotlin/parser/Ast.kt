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
    class Command(val name: String, val arguments: List<Value>) : Ast() {
        override fun toString(): String {
            return """{"name":"$name","arguments":$arguments}"""
        }
    }
}

sealed class Value {
    class Number(val value: Double) : Value() {
        override fun toString(): kotlin.String {
            return """{"type":"number","value":$value}"""
        }
    }
    class String(val value: kotlin.String) : Value() {
        override fun toString(): kotlin.String {
            return """{"type":"string","value":"$value"}"""
        }
    }
    class Selector(val value: kotlin.String) : Value() {
        override fun toString(): kotlin.String {
            return """{"type":"selector","value":"$value"}"""
        }
    }
    class Symbol(val value: kotlin.String) : Value() {
        override fun toString(): kotlin.String {
            return """{"type":"symbol","value":"$value"}"""
        }
    }
    class Command(val value: Ast.Command) : Value() {
        override fun toString(): kotlin.String {
            return """{"type":"command","value":$value}"""
        }
    }
    class Block(val value: Ast.Block) : Value() {
        override fun toString(): kotlin.String {
            return """{"type":"block","value":$value}"""
        }
    }
}