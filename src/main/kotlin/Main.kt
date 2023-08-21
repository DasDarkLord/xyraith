import bytecode.Emitter
import server.Interpreter
import ir.Translation
import ir.optimizations.applyAllTransformations
import lexer.Lexer
import parser.Parser
import parser.ParserError
import server.disassemble
import server.startupServer
import server.transform
import java.io.File
import java.lang.IndexOutOfBoundsException
import java.nio.ByteBuffer
import java.time.LocalDate

var globalInterpreter = Interpreter(ByteBuffer.allocate(0))
val debug = 5
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
        val translator = Translation()
        val blocks = translator.translateAST(ast)
        val optimizedBlocks = applyAllTransformations(blocks)
        Logger.trace("$optimizedBlocks")
        optimizedBlocks.forEach {
            Logger.trace(it.display())
        }
        val emitter = Emitter(optimizedBlocks)
        var bytes = emitter.startEmitting()
        bytes = bytes.position(0)
        val interpreter = Interpreter(bytes)
        interpreter.transform()
        globalInterpreter = interpreter
        Logger.trace("blockmap:")
        globalInterpreter.printBlockMap()
        startupServer()
    } catch(e: ParserError) {
        println(e.emit())
    }
}

