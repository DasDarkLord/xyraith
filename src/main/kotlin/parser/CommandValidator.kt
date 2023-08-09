package parser

import getResourceAsText

fun verifyBuiltinCommand(command: String): Boolean {
    val commands = getResourceAsText("commands.txt")!!.split('\n')
    println("cmds")
    commands.forEach {
        println("> $it")
    }
    return !commands.contains(command)
}