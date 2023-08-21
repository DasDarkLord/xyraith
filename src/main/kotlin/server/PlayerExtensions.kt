package server

import net.minestom.server.entity.Player

fun playerExtension(player: Player, interpreter: Interpreter) {
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