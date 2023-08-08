import lexer.Lexer
import parser.Parser
import parser.ParserError
import java.io.File

fun main(args: Array<String>) {
    val text = File("main.xyr").readText()
    val lexer = Lexer(text)
    val tokens = lexer.transform()
    println("[")
    tokens.forEach {
        println("\t$it,")
    }
    println("]")
    val parser = Parser(tokens)
    try {
        val ast = parser.parseEvent()
        println(ast)
    } catch(e: ParserError) {
        println(e.emit())
    }
}
// t