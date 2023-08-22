package server.extensions

import net.minestom.server.entity.Player
import server.interpreter.Interpreter

fun playerExtension(interpreter: Interpreter, player: Player) {
    interpreter.addExtensionInstruction(0) {
        val target = it.getShort()
        val reg = it.getShort().toInt()
        println("Interpreter | Sending message of a funny value ")
        player.sendMessage(interpreter.registers[reg].toDisplay())
    }
    interpreter.addExtensionInstruction(0) {
        val target = it.getShort()
        val reg = it.getShort().toInt()
        println("Interpreter | Sending message of a funny value ")
        player.sendMessage(interpreter.registers[reg].toDisplay())
    }
}

