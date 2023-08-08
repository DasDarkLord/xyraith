package parser

sealed class Ast {
    class Event(val name: String, val code: Block) : Ast() {
        override fun toString(): String {
            return """
"event": {
    "name": "$name",
    "code": $code,
}
            """.trimIndent()
        }
    }
    class Block(val nodes: List<Ast.Command>) : Ast() {
        override fun toString(): String {
            return """
[
    // todo
]
            """.trimIndent()
        }
    }
    class Command(val name: String, val arguments: List<Value>) : Ast()
}

sealed class Value {
    class Number(value: Double) : Value()
    class String(value: kotlin.String) : Value()
    class Symbol(value: kotlin.String) : Value()
    class Command(value: Ast.Command) : Value()
}