package docs

import parser.ArgumentType

data class CommandDocument(
    val commandName: String,
    val arguments: List<Pair<String, String>>,
    val commandDescription: String
) {
    fun toHtml(): String {
        var preproc = ""
        for(arg in arguments) {
            preproc += "<b>${arg.first}</b> - ${arg.second}<br>"
        }
        return """
<h2>$commandName</h2>
$commandDescription

<h3>Command Parameters</h3>
$preproc
<br>
<br>
<br>
        """.trimIndent()
    }
}