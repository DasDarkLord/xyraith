package docs

import code.instructions.visitables

fun dumpCommands(): String {
    var output = "["
    for(obj in visitables) {
        val documentation = CommandDocument(
            obj.command,
            decomposeList(obj.arguments),
            obj.description,
            obj.returnType
        )
        output += documentation.toJson()
    }
    output = output.trim().removeSuffix(",")
    output += "]"
    return output
}