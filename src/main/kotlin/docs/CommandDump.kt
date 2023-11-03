package docs

import instructions.visitables

fun dumpCommands(): String {
    var output = "["
    for(obj in instructions.visitables) {
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