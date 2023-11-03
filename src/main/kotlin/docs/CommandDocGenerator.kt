package docs

import instructions.visitables
import typechecker.*

fun generateDocumentation(): Map<String, String> {
    val output = mutableMapOf<String, String>()
    for(obj in visitables.sortedBy { it.command }) {
        val command = obj.command
        val split = command.split(".").toMutableList()
        val finalName = split.removeLast()
        val startingName = split.joinToString("/")
        println("split: $split | finalName: $finalName | startingName: $startingName")
        val documentation = CommandDocument(
            obj.command,
            decomposeList(obj.arguments),
            obj.description,
            obj.returnType
        )
        output[startingName] = (output[startingName] ?: "") + documentation.toHtml()
    }
    return output
}

fun decomposeList(list: ArgumentList): List<Pair<String, String>> {
    val output = mutableListOf<Pair<String, String>>()
    for(arg in list.list) {
        output.add(when(arg) {
            is ArgumentNode.SingleArgumentNode -> Pair(arg.type.toString(), arg.desc)
            is ArgumentNode.OptionalArgumentNode -> Pair("*${arg.type}", arg.desc)
            is ArgumentNode.PluralArgumentNode -> Pair("${arg.type}(s)", arg.desc)
            else -> Pair("Unknown", "If you see this, please report this as an issue!")
        })
    }
    return output
}
