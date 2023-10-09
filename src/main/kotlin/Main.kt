import code.*
import code.server.startServer
import config.parseToml
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

val configInstance = parseToml()

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
        "init" -> {
            println("Initializing project..")
            initProject()
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
Version v0.1 (10/09/23)

Usage: java -jar Xyraith.jar [subcommand]

Subcommands:
init - Initialize a Xyraith project in the current directory.
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

fun initProject() {
    File("./src").mkdirs()
    File("./src/main.xyr").createNewFile()
    File("./src/main.xyr").writeText("""
;; This is a simple server that sends "Hello world!" when you join the server.
;; Use the `run` subcommand to run it.
event join {
    player.sendMessage "Hello world!"    
}
    """)
    File("./xyraith.toml").createNewFile()
    File("./xyraith.toml").writeText("""
[server]
# 0.0.0.0 for local machine, your IP address for public access
host = "0.0.0.0"
# Port to host the server on.
# 25565 recommended & default.
port = 25565
# MOTD to display in serverlist.
# Supports MiniMessage format.
motd = ""
    """.trimIndent())
}
