package parser

import lexer.Token

fun verifyBuiltinCommand(nameToken: Token.Identifier, arguments: List<Value>) {
    val command = nameToken.value
    val commandMap = mapOf(
        "add" to listOf("number", "number"),
        "sub" to listOf("number", "number"),
        "mul" to listOf("number", "number"),
        "div" to listOf("number", "number"),
        "mod" to listOf("number", "number"),
        "shl" to listOf("number", "number"),
        "shr" to listOf("number", "number"),

        "addAndLoad" to listOf("symbol", "number"),
        "subAndLoad" to listOf("symbol", "number"),
        "mulAndLoad" to listOf("symbol", "number"),
        "divAndLoad" to listOf("symbol", "number"),
        "modAndLoad" to listOf("symbol", "number"),
        "shlAndLoad" to listOf("symbol", "number"),
        "shrAndLoad" to listOf("symbol", "number"),

        "load" to listOf("symbol"),
        "store" to listOf("symbol"),
        "globals.load" to listOf("symbol", "any"),
        "globals.store" to listOf("symbol"),

        "player.sendMessage" to listOf("string"),
    )

    if(!commandMap.containsKey(command)) {
        throw InvalidCommand(command, nameToken.spanStart, nameToken.spanEnd)
    }
    val typeCheckList = commandMap[command]!!
    val iterator = arguments.iterator()
    typeCheckList.forEach { type ->
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
        if(type == "loc" || type == "list") {
            if(next !is Value.Command && next !is Value.Symbol) {
                throw IncorrectArgument(type, "another type", command, nameToken.spanStart, nameToken.spanEnd)
            }
        }
    }

}

