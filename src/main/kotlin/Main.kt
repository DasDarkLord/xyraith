import ir.Translation
import ir.optimizations.applyAllTransformations
import lexer.Lexer
import parser.Parser
import parser.ParserError
import java.io.File
import java.lang.IndexOutOfBoundsException

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
    } finally {

    }
//    } catch(e: ParserError) {
//        println(e.emit())
//    } catch(e: IndexOutOfBoundsException) {
//        println("silly bounds error?!")
//    }
}