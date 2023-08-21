import bytecode.Emitter
import server.Interpreter
import ir.Translation
import ir.optimizations.applyAllTransformations
import lexer.Lexer
import parser.Parser
import parser.ParserError
import server.disassemble
import server.transform
import java.io.File
import java.lang.IndexOutOfBoundsException
import java.nio.ByteBuffer
import java.time.LocalDate

var globalInterpreter = Interpreter(ByteBuffer.allocate(0))
fun getResourceAsText(path: String): String? =
    object {}.javaClass.getResource(path)?.readText()

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
        val time1 = LocalDate.now()
        val ast = parser.parseAll()
        println(ast)
        val translator = Translation()
        val blocks = translator.translateAST(ast)
        val optimizedBlocks = applyAllTransformations(blocks)
        println("$optimizedBlocks")
        optimizedBlocks.forEach {
            println(it.display())
        }
        val emitter = Emitter(optimizedBlocks)
        var bytes = emitter.startEmitting()
        bytes = bytes.position(0)
        print("[")
        bytes.array().forEach {
            print("$it, ")
        }
        println("]")
        bytes = bytes.position(0)
        val interpreter = Interpreter(bytes)
        interpreter.transform()
        globalInterpreter = interpreter
        println("blockmap:")
        globalInterpreter.printBlockMap()
        globalInterpreter.disassemble()
        globalInterpreter.bytes.position(0)
        globalInterpreter.interpretEvent(1)
    } catch(e: ParserError) {
        println(e.emit())
    }
}