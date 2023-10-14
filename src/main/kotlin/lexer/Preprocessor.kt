package lexer

import error.UnexpectedEOF
import error.UnexpectedToken
import java.io.File

fun preprocessMain(mainTokens: MutableList<Token>, directory: String): MutableList<Token> {
    val outputTokens = mutableListOf<Token>()

    var hitCode = false

    for((index, token) in mainTokens.withIndex()) {
        if(!hitCode) {
            if(token is Token.Identifier && token.value == "import") {
                if(mainTokens.size < index) {
                    throw UnexpectedEOF(token.span)
                }
                val next = mainTokens[index+1]
                if(next !is Token.StringText) {
                    throw UnexpectedToken(TokenType.StringText, next.toType(), next.span)
                }
                val lexer = Lexer(
                    File(directory + next.value + ".xr").readText(),
                    directory + next.value + ".xr"
                )
                outputTokens.addAll(lexer.transform())
            } else if (token is Token.Identifier) {
                hitCode = true
                outputTokens.add(token)
            }
        } else {
            outputTokens.add(token)
        }
    }
    return outputTokens
}