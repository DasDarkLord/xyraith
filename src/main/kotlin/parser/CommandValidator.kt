package parser

import lexer.Token

fun verifyBuiltinCommand(nameToken: Token.Identifier, arguments: List<Value>) {
    val command = nameToken.value

    if(!commandRegistry.containsKey(command)) {
        throw InvalidCommand(command, nameToken.spanStart, nameToken.spanEnd)
    }
    val typeCheckList: List<*> = (commandRegistry[command]!!["arguments"] as List<*>?)!!
    val iterator = arguments.iterator()
    typeCheckList.forEach { type ->
        val type = type as String
        if(!iterator.hasNext()) {
            throw IncorrectArgument(type, "end of command", command, nameToken.spanStart, nameToken.spanEnd)
        }
        val next = iterator.next()
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
    }

}

