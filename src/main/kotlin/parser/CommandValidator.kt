package parser

import code.Visitable
import lexer.SpanData
import registry.commandRegistry
import lexer.Token

data class NodeValidatorData<T>(
    val command: String,
    val arguments: Iterator<Value>,
    val spans: Iterator<SpanData>,
    val node: T,
    val nameSpan: SpanData,
)

fun verifyBuiltinCommand(nameToken: Token.Identifier, arguments: List<Value>, spans: List<SpanData>) {
    val command = nameToken.value

    if(!commandRegistry.containsKey(command)) {
        throw InvalidCommand(command, nameToken.span)
    }

    val obj: Visitable = commandRegistry[command]!!["object"]!! as Visitable
    val argIter = arguments.iterator()
    val spanIter = spans.iterator()
    obj.arguments.list.forEachIndexed { _, node ->
        when(node) {
            is SingleArgumentNode -> verifySingleNode(NodeValidatorData(command, argIter, spanIter, node, nameToken.span))
            is OptionalArgumentNode -> verifyOptionalNode(NodeValidatorData(command, argIter, spanIter, node, nameToken.span))
            is PluralArgumentNode -> verifyPluralNode(NodeValidatorData(command, argIter, spanIter, node, nameToken.span))
        }
    }
    // if(arguments.size > obj.arguments.list.size) throw IncorrectArgument("no type", "too many arguments", command, nameToken.span)

}

private fun verifySingleNode(data: NodeValidatorData<SingleArgumentNode>) {
    val (command, arguments, spans, node, nameSpan) = data
    var nextSpan: SpanData = nameSpan
    if(!arguments.hasNext()) {
        throw IncorrectArgument("No Type", node.type.toString(), command, nextSpan)
    }
    val nextArgument = arguments.next()
    var nextType = nextArgument.castToArgumentType()
    if(nextArgument.castToArgumentType() == ArgumentType.COMMAND) {
        val visitable = commandRegistry[nextArgument.castToCommandName()]?.get("object")!! as Visitable
        nextType = visitable.returnType
    }
    nextSpan = spans.next()
    if(node.type != nextType && node.type != ArgumentType.ANY && nextType != ArgumentType.ANY) {
        throw IncorrectArgument(node.type.toString(), nextType.toString(), command, nextSpan)
    }
}

private fun verifyOptionalNode(data: NodeValidatorData<OptionalArgumentNode>) {
    val (command, arguments, spans, node, nameSpan) = data
    var nextSpan: SpanData = nameSpan
    if(!arguments.hasNext()) {
        return
    }
    val nextArgument = arguments.next()
    var nextType = nextArgument.castToArgumentType()
    if(nextArgument.castToArgumentType() == ArgumentType.COMMAND) {
        val visitable = commandRegistry[nextArgument.castToCommandName()]?.get("object")!! as Visitable
        nextType = visitable.returnType
    }
    nextSpan = spans.next()
    if(node.type != nextType && node.type != ArgumentType.ANY && nextType != ArgumentType.ANY) {
        throw IncorrectArgument(node.type.toString(), nextType.toString(), command, nextSpan)
    }
}

private fun verifyPluralNode(data: NodeValidatorData<PluralArgumentNode>) {
    val (command, arguments, spans, node, nameSpan) = data
    if(!arguments.hasNext()) {
        return
    }
    var argCount = 0
    var nextSpan: SpanData = nameSpan
    while(true) {
        if(!arguments.hasNext()) {
            if(argCount == 0) {
                throw IncorrectArgument(node.type.toString(), "No Type", command, nextSpan)
            }
            return
        }
        val nextArgument = arguments.next()
        var nextType = nextArgument.castToArgumentType()
        if(nextArgument.castToArgumentType() == ArgumentType.COMMAND) {
            val visitable = commandRegistry[nextArgument.castToCommandName()]?.get("object")!! as Visitable
            nextType = visitable.returnType
        }
        nextSpan = spans.next()
        if(node.type != nextType && node.type != ArgumentType.ANY && nextType != ArgumentType.ANY) {
            throw IncorrectArgument(node.type.toString(), nextType.toString(), command, nextSpan)
        }
        argCount++
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