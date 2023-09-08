import code.Emitter
import code.Interpreter
import code.Visitable
import code.visitables
import lexer.Lexer
import parser.Parser
import parser.ParserError
import java.io.File
import java.time.LocalDate

val debug = 5



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
        val emitter = Emitter(ast)
        emitter.emit()
        Logger.trace(emitter)
        val constants = emitter.constants.map { pair -> pair.value to pair.key }.toMap()
        val blockMap = emitter.blockMap
        val interpreter = Interpreter(constants, blockMap)
        interpreter.runBlock(1)
    } catch(e: ParserError) {
        println(e.emit())
    }
}
