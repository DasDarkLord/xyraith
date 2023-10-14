package parser

import lexer.SpanData

sealed class Ast {
    class Event(val name: String, val code: Block, val eventType: EventType, val span: SpanData) : Ast() {
        override fun toString(): String {
            return """{"name": "$name","eventType":"$eventType","code": $code}""".trimIndent()
        }
    }
    class Block(val nodes: List<Ast.Command>, val eventName: String, val span: SpanData) : Ast() {
        override fun toString(): String {
            return """{"name":"$eventName","nodes":$nodes}""".trimIndent()
        }
    }
    class Command(val name: String, val arguments: MutableList<Value>, val span: SpanData) : Ast() {
        override fun toString(): String {
            return """{"name":"$name","arguments":$arguments}"""
        }
    }
}

enum class EventType {
    EVENT,
    FUNCTION,
    STRUCT;
}

