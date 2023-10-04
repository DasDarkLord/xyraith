package docs

import code.visitables
import parser.*

fun generateDocumentation(): String {
    var output = ""
    for(obj in visitables) {
        val documentation = CommandDocument(
            obj.command,
            decomposeList(obj.arguments),
            obj.description,
            obj.returnType
        )
        output += documentation.toHtml()
    }
    return output
}

fun decomposeList(list: ArgumentList): List<Pair<String, String>> {
    val output = mutableListOf<Pair<String, String>>()
    for(arg in list.list) {
        output.add(when(arg) {
            is SingleArgumentNode -> Pair(arg.type.toString(), arg.desc)
            is OptionalArgumentNode -> Pair("*${arg.type.toString()}", arg.desc)
            is PluralArgumentNode -> Pair("${arg.type.toString()}(s)", arg.desc)
            else -> Pair("Unknown", "If you see this, please report this as an issue!")
        })
    }
    return output
}
