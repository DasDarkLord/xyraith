package server.extensions

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import server.interpreter.Interpreter

fun playerExtension(interpreter: Interpreter, player: Player) {
    /*
    Extensions 1-1000 handle the player
     */
    interpreter.addExtensionInstruction(1) {
        val target = it.getShort()
        val reg = it.getShort().toInt()
        player.sendMessage(interpreter.registers[reg].toDisplay())
    }
    interpreter.addExtensionInstruction(2) {
        val target = it.getShort()
        val x = interpreter.registers[it.getShort().toInt()].toNumber()
        val y = interpreter.registers[it.getShort().toInt()].toNumber()
        val z = interpreter.registers[it.getShort().toInt()].toNumber()
        player.respawnPoint = Pos(x, y, z)
    }
}

