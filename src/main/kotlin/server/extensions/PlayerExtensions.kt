package server.extensions

import parser.Value
import registry.Extensions
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minestom.server.coordinate.Pos
import server.interpreter.Interpreter

fun playerExtension(interpreter: Interpreter) {
    /*
    Extensions 1-1000 handle the player
     */
    interpreter.addExtensionInstruction(Extensions.Player.SENDMESSAGE) {
        val target = it.getShort()
        val reg = it.getShort().toInt()
        val message = MiniMessage.miniMessage().deserialize(interpreter.registers[reg].toDisplay())
        interpreter.player?.sendMessage(message)
    }
    interpreter.addExtensionInstruction(Extensions.Player.SETSPAWNPOINT) {
        val target = it.getShort()
        val x = interpreter.registers[it.getShort().toInt()].toNumber()
        val y = interpreter.registers[it.getShort().toInt()].toNumber()
        val z = interpreter.registers[it.getShort().toInt()].toNumber()
        interpreter.player?.respawnPoint = Pos(x, y, z)
    }
    interpreter.addExtensionInstruction(Extensions.Player.GETLOCATION) {
        val target = it.getShort().toInt()
        if(interpreter.player != null) {
            interpreter.registers[target] = Value.Position(
                interpreter.player!!.position.x,
                interpreter.player!!.position.y,
                interpreter.player!!.position.z,
                interpreter.player!!.position.pitch.toDouble(),
                interpreter.player!!.position.yaw.toDouble()
            )
        }
    }
    interpreter.addExtensionInstruction(Extensions.Player.TELEPORT) {
        val target = it.getShort().toInt()
        val loc = it.getShort().toInt()
        val value = interpreter.registers[loc]
        if(value is Value.Position) {
            interpreter.player?.teleport(Pos(
                value.x,
                value.y,
                value.z,
                value.pitch.toFloat(),
                value.yaw.toFloat(),
            ))
        }
    }
}

