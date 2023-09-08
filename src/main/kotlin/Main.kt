import lexer.Lexer
import parser.Parser
import parser.ParserError
import parser.Value
import server.interpreter.Interpreter
import java.io.File
import java.nio.ByteBuffer
import java.time.LocalDate

var globalInterpreter = Interpreter(ByteBuffer.allocate(0))
val debug = 5
val blockMap: MutableMap<Int, ByteBuffer> = mutableMapOf()
val constants: MutableMap<Int, Value> = mutableMapOf()
val functions: MutableMap<Value, Int> = mutableMapOf()

fun getResourceAsText(path: String): String? =
    object {}.javaClass.getResource(path)?.readText()

fun main(args: Array<String>) {
    val text = File("main.xyr").readText()
    val lexer = Lexer(text)
    val tokens = lexer.transform()
    Logger.trace("[")
    tokens.forEach {
        Logger.trace("\t$it,")
    }
    Logger.trace("]")
    val parser = Parser(tokens)
    try {
        val time1 = LocalDate.now()
        val ast = parser.parseAll()
        Logger.trace(ast)
    } catch(e: ParserError) {
        println(e.emit())
    }
}

