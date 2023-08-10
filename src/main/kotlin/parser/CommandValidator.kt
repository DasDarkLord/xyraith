package parser

import getResourceAsText

fun verifyBuiltinCommand(command: String): Boolean {
    val commands = getResourceAsText("commands.txt")!!.split('\n')
    return !commands.contains(command)
}