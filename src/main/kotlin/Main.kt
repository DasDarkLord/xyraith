import bytecode.Emitter
import interpreter.Interpreter
import ir.Translation
import ir.optimizations.applyAllTransformations
import lexer.Lexer
import parser.Parser
import parser.ParserError
import java.io.File
import java.lang.IndexOutOfBoundsException

fun getResourceAsText(path: String): String? =
    object {}.javaClass.getResource(path)?.readText()

fun main(args: Array<String>) {
    val text = File("main.lisp").readText()
    val lexer = Lexer(text)
    val tokens = lexer.transform()
    println("[")
    tokens.forEach {
        println("\t$it,")
    }
    println("]")
    val parser = Parser(tokens)
    try {
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
        emitter.startEmitting()
        var bytes = emitter.getBytes()
        bytes = bytes.position(0)
        print("[")
        bytes.array().forEach {
            print("$it, ")
        }
        println("]")
        bytes = bytes.position(0)
        val interpreter = Interpreter(bytes)
        interpreter.transform()
        println("blockmap:")
        println(interpreter.blockMap)
    } catch(e: ParserError) {
        println(e.emit())
    } catch(e: IndexOutOfBoundsException) {
        println("silly bounds error?!")
    }
}