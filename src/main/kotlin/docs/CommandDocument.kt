package docs

import typechecker.ArgumentType

data class CommandDocument(
    val commandName: String,
    val arguments: List<Pair<String, String>>,
    val commandDescription: String,
    val returnType: ArgumentType,
) {
    fun toHtml(): String {
        var preproc = ""
        for(arg in arguments) {
            preproc += "- **${arg.first}** ${arg.second}\n"
        }
        if(arguments.isEmpty()) {
            preproc = "None"
        }
        preproc = preproc.removeSuffix("\n")
        return """
## $commandName
$commandDescription
### Command Parameters
$preproc
### Return Type
Returns $returnType.%%spec_nl%%%%spec_nl%%
        """.trimIndent().replace("%%spec_nl%%", "\n")
    }

    fun toJson(): String {
        var preproc = ""
        for(arg in arguments) {
            preproc += "{\"type\":\"${arg.first}\",\"description\":\"${arg.second}\"},"
        }
        preproc = preproc.removeSuffix(",")
        return """
{
    "name": "$commandName",
    "description": "$commandDescription",
    "arguments": [
        $preproc
    ],
    "return_type": "$returnType",
},
        """
    }
}