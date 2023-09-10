import code.Emitter
import code.Interpreter
import code.Visitable
import code.server.startServer
import code.visitables
import docs.dumpCommands
import docs.generateDocumentation
import docs.wrapDocumentation
import lexer.Lexer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import parser.Parser
import parser.ParserError
import registry.commandRegistry
import java.awt.Desktop
import java.io.File
import java.net.URI
import java.nio.ByteBuffer
import java.time.LocalDate

val debug = 5
var constants: Map<Int, parser.Value> = mapOf()
var blockMap: MutableMap<Int, ByteBuffer> = mutableMapOf()

val miniMessage = MiniMessage.miniMessage()
fun mm(str: String): Component = miniMessage.deserialize(str)

fun main(args: Array<String>) {
    when(args.getOrNull(0)) {
        "run" -> {
            println("Running server.. you may see some debug output.")
            runServer()
        }
        "docs" -> {
            println("Generating documentation.")
            generateDocs()
        }
        "dumpcommandinfo" -> {
            println("Generating command dump.")
            generateCommandDump()
        }
        else -> {
            println("Unknown subcommand.\n")
            helpCommand()
        }
    }
}

fun helpCommand() {
    println("""
Xyraith's official compiler & tooling

Example of using a subcommand:
java -jar Xyraith.jar run
java -jar Xyraith.jar docs
etc. etc.

Subcommands:
run - Run the server. Currently grabs code from file at: ./src/main/xyraith/main.xr
docs - Generate documentation. This will open your web browser.

Advanced Subcommands:
dumpcommandinfo - Dump a JSON of command info to the file at `docs/commanddump.json`.

    """.trimIndent())
}

fun generateDocs() {
    val docgen = generateDocumentation()
    val file = File("docs/commandDocs.html")
    file.createNewFile()
    file.writeText(wrapDocumentation(docgen))
    Desktop.getDesktop().browse(URI("docs/commandDocs.html"))
}

fun generateCommandDump() {
    val docgen = dumpCommands()
    val file = File("docs/commandDump.jsonc")
    file.createNewFile()
    file.writeText(docgen)
}

fun runServer() {
    val text = File("src/main/xyraith/main.xr").readText()
    val lexer = Lexer(text)
    val tokens = lexer.transform()
    Logger.trace("[")
    tokens.forEach {
        Logger.trace("\t$it,")
    }
    Logger.trace("]")
    val parser = Parser(tokens)
    println("registry: $commandRegistry")
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
