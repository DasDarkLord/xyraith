package parser

import code.Visitable
import registry.commandRegistry
import lexer.Token

data class NodeValidatorData<T>(
    val command: String,
    val arguments: Iterator<Value>,
    val node: T,
    val spanStart: Int,
    val spanEnd: Int,
)

fun verifyBuiltinCommand(nameToken: Token.Identifier, arguments: List<Value>) {
    val command = nameToken.value

    if(!commandRegistry.containsKey(command)) {
        throw InvalidCommand(command, nameToken.spanStart, nameToken.spanEnd)
    }

    val obj: Visitable = commandRegistry[command]!!["object"]!! as Visitable
    val argIter = arguments.iterator()
    for(node in obj.arguments.list) {
        if(node is SingleArgumentNode) {
            verifyNode(NodeValidatorData(command, argIter, node, nameToken.spanStart, nameToken.spanEnd))
        }
    }
}

private fun verifyNode(data: NodeValidatorData<SingleArgumentNode>) {
    val (command, arguments, node, spanStart, spanEnd) = data
    if(!arguments.hasNext()) {
        throw IncorrectArgument("No Type", node.type.toString(), command, spanStart, spanEnd)
    }
    val nextArgument = arguments.next()
    when(node.type) {
        ArgumentType.NUMBER -> {
            if(nextArgument is Value.Number) {
                throw IncorrectArgument(ArgumentType.NUMBER.toString(), node.type.toString(), command, spanStart, spanEnd)
            }
        }
        ArgumentType.STRING -> {
            if(nextArgument is Value.String) {
                throw IncorrectArgument(ArgumentType.STRING.toString(), node.type.toString(), command, spanStart, spanEnd)
            }
        }
    }
}


/*
val typeCheckList: List<*> = (commandRegistry[command]!!["arguments"] as List<*>?)!!
    var argumentPointer = 0
    typeCheckList.forEach { type ->
        val type = type as String
        if(argumentPointer >= arguments.size) {
            throw IncorrectArgument(type, "end of command", command, nameToken.spanStart, nameToken.spanEnd)
        }
        println("$argumentPointer $typeCheckList $nameToken")
        val next = arguments[argumentPointer++]
        if(type == "number") {
            if(next !is Value.Number && next !is Value.Symbol && next !is Value.Command) {
                throw IncorrectArgument("number", "another type", command, nameToken.spanStart, nameToken.spanEnd)
            }
        }
        if(type == "string") {
            if(next !is Value.String && next !is Value.Symbol && next !is Value.Command) {
                throw IncorrectArgument("string", "another type", command, nameToken.spanStart, nameToken.spanEnd)
            }
        }
        if(type == "symbol") {
            if(next !is Value.Symbol) {
                throw IncorrectArgument("symbol", "another type", command, nameToken.spanStart, nameToken.spanEnd)
            }
        }
        if(type == "loc" || type == "list" || type == "item") {
            if(next !is Value.Command && next !is Value.Symbol) {
                throw IncorrectArgument(type, "another type", command, nameToken.spanStart, nameToken.spanEnd)
            }
        }
        if(type == "block") {
            if(next !is Value.Block) {
                throw IncorrectArgument("block", "another type", command, nameToken.spanStart, nameToken.spanEnd)
            }
        }
        if(type == "command") {
            if(next !is Value.Command) {
                throw IncorrectArgument("command", "another type", command, nameToken.spanStart, nameToken.spanEnd)
            }
        }
        if(type == "selector") {
            if(next !is Value.Selector) {
                throw IncorrectArgument("selector", "another type", command, nameToken.spanStart, nameToken.spanEnd)
            }
        }
    }
 */