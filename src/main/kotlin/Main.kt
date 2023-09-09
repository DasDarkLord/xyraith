import code.Emitter
import code.Interpreter
import code.Visitable
import code.server.startServer
import code.visitables
import lexer.Lexer
import parser.Parser
import parser.ParserError
import java.io.File
import java.nio.ByteBuffer
import java.time.LocalDate

val debug = 5
var constants: Map<Int, parser.Value> = mapOf()
var blockMap: MutableMap<Int, ByteBuffer> = mutableMapOf()

fun main(args: Array<String>) {

    val text = File("src/main/xyraith/main.xr").readText()
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
        constants = emitter.constants.map { pair -> pair.value to pair.key }.toMap()
        blockMap = emitter.blockMap

        startServer()
    } catch(e: ParserError) {
        e.printStackTrace()
        println(e.emit())
    }
}
