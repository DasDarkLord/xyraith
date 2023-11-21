import runtime.*
import runtime.server.startServer
import config.parseToml
import docs.dumpCommands
import docs.generateDocumentation
import lang.lexer.Lexer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import parser.Parser
import error.ParserError
import instructions.visitables
import lang.emitter.IREmitter
import lang.ir.transformAst
import registry.validateRegistry
import typechecker.ArgumentType
import typechecker.Typechecker
import java.io.File
import java.nio.ByteBuffer

val debug = 1
var constants: Map<Int, Value> = mapOf()
var blockMap: MutableMap<Int, InterpreterData.BasicBlock> = mutableMapOf()

val miniMessage = MiniMessage.miniMessage()
fun mm(str: String): Component = miniMessage.deserialize(str)

val globalVariables: MutableMap<String, Value> = mutableMapOf()

val configInstance = parseToml()

val structs: MutableMap<String, MutableMap<String, ArgumentType>> = mutableMapOf()
val types: MutableList<String> = mutableListOf(
    "number",
    "string",
    "bool",
    "symbol",
    "list",
    "any",
    "itemStack"
)

val functions: MutableMap<String, Pair<MutableList<ArgumentType>, ArgumentType>> = mutableMapOf()

fun main(args: Array<String>) {
    println(visitables)
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
Version v0.3 (??/??/23)

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

    for(key in docgen.keys) {
        val value = docgen[key]
        val key2 = if(key == "") "" else "$key/"
        val cmds = File(".\\docs\\commands")
        cmds.mkdir()
        val file = File(".\\docs\\commands\\${key2}commands.md")
        println("path: " + file.path)
        if(!file.path.contains("docs\\commands\\commands.md")) {
            file.mkdirs()
            file.delete()
        }
        file.createNewFile()
        file.writeText(value!!)
    }

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
    println("tokens:\n$tokens")
    val parser = Parser(tokens)
    try {
        // val time1 = LocalDate.now()
        val ast = parser.parseAll()
        val typeChecker = Typechecker()
        for(event in ast) {
            typeChecker.typecheckEvent(event)
        }
        println("ast:\n${ast}")
        val module = transformAst(ast)
        println(module)
        val irEmitter = IREmitter(module)
        val bytes = irEmitter.emit()
        println(bytes)

        val parsed = parseBytecode(ByteBuffer.wrap(bytes.toByteArray()))

        constants = parsed.second
        blockMap = parsed.first.associateBy { it.id }.toMutableMap()

        println("Here's the apps:")
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

fun initProject() {
    File("./src").mkdirs()
    File("./src/main.xr").createNewFile()
    File("./src/main.xr").writeText("""
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
