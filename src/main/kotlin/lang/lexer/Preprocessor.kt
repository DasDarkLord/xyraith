package lang.lexer

import error.NotAValidImport
import error.UnexpectedEOF
import error.UnexpectedToken
import stdlib.stdlibFiles
import java.io.File
import java.io.FileNotFoundException

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
                if(stdlibFiles.containsKey(next.value)) {
                    val lexer = Lexer(
                        stdlibFiles[next.value]!!,
                        directory + next.value + ".xr"
                    )
                    outputTokens.addAll(lexer.transform())
                } else {
                    try {
                        val lexer = Lexer(
                            File(directory + next.value + ".xr").readText(),
                            directory + next.value + ".xr"
                        )
                        outputTokens.addAll(lexer.transform())
                    } catch(e: FileNotFoundException) {
                        throw NotAValidImport(next.span)
                    }
                }


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