import code.*
import code.server.startServer
import docs.dumpCommands
import docs.generateDocumentation
import lexer.Lexer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minestom.server.entity.Player
import parser.Parser
import error.ParserError
import registry.validateRegistry
import typechecker.Typechecker
import java.io.File
import java.nio.ByteBuffer
import java.time.LocalDate

val debug = 1
var constants: Map<Int, parser.Value> = mapOf()
var blockMap: MutableMap<Int, ByteBuffer> = mutableMapOf()

val miniMessage = MiniMessage.miniMessage()
fun mm(str: String): Component = miniMessage.deserialize(str)

var playerList = mutableListOf<Player>()

val globalVariables: MutableMap<String, parser.Value> = mutableMapOf()

fun main(args: Array<String>) {
    validateRegistry()
    when(args.getOrNull(0)) {
        "run" -> {
            println("Running server.. you may see some debug output.")
            runServer(true)
        }
        "docs" -> {
            println("Generating documentation.")
            generateDocs()
        }
        "dumpcommandinfo" -> {
            println("Generating command dump.")
            generateCommandDump()
        }
        "serverless" -> {
            println("Starting serverlessly...")
            runServer(false)
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
Build version v0.1-RC1 (10/08/23)

Example of using a subcommand:
java -jar Xyraith.jar run
java -jar Xyraith.jar docs
etc. etc.

Subcommands:
run - Run the server. Currently grabs code from file at: ./src/main/xyraith/main.xr
docs - Generate documentation. This will open your web browser.
serverless - Run the Xyraith directory without a server.

Advanced Subcommands:
dumpcommandinfo - Dump a JSON of command info to the file at `docs/commanddump.json`.

    """.trimIndent())
}

fun generateDocs() {
    val docgen = generateDocumentation()
    val file = File("./docs/commandDocs.md")
    file.createNewFile()
    file.writeText(docgen)
}

fun generateCommandDump() {
    val docgen = dumpCommands()
    val file = File("docs/commandDump.jsonc")
    file.createNewFile()
    file.writeText(docgen)
}

fun runServer(withServer: Boolean) {
    val text = File("src/main.xr").readText()
    val lexer = Lexer(text, "src/main.xr")
    val tokens = lexer.transform()
    val parser = Parser(tokens)
    try {
        val time1 = LocalDate.now()
        val ast = parser.parseAll()
        Logger.trace(ast)
        val typeChecker = Typechecker()
        for(event in ast) {
            typeChecker.typecheckEvent(event)
        }
        val emitter = Emitter(ast)
        emitter.emit()
        Logger.trace(emitter)

        constants = emitter.constants.map { pair -> pair.value to pair.key }.toMap()
        blockMap = emitter.blockMap
        Disassembler.dissasemble(emitter)
        saveBinary(emitter)
        println("Executing...")
        if(!withServer) {
            runEvent(1)
            return
        }
        startServer()
    } catch(e: ParserError) {
        println(e.emit())
        e.printStackTrace()
    }
}

fun saveBinary(emitter: Emitter) {
    val file = File("cached.xrv")
    val listOfBytes = mutableListOf<Byte>()
    var index = 0
    println(emitter.constantsBytes.position())
    listOfBytes.add(1)
    for(byte in emitter.constantsBytes.array()) {
        index++
        if(index > emitter.constantsBytes.position()) {
            continue
        }
        listOfBytes.add(byte)
    }
    listOfBytes.add(2)
    for(entry in blockMap) {
        var index2 = 0
        for(byte in entry.value.array()) {
            index2++
            if(index2 > entry.value.position()) {
                continue
            }
            listOfBytes.add(byte)
        }
    }

    println("lob: ${listOfBytes.joinToString(",") }")
    file.writeBytes(listOfBytes.toByteArray())
}
