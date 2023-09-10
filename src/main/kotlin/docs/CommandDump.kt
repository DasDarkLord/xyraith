package docs

import code.visitables

fun dumpCommands(): String {
    var output = "["
    for(obj in visitables) {
        val documentation = CommandDocument(
            obj.command,
            decomposeList(obj.arguments),
            obj.description
        )
        output += documentation.toJson()
    }
    output = output.trim().removeSuffix(",")
    output += "]"
    return output
}