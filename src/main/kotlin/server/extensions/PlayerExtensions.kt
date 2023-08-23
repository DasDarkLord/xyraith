package server.extensions

import registry.Extensions
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import server.Value
import server.interpreter.Interpreter

fun playerExtension(interpreter: Interpreter, player: Player) {
    /*
    Extensions 1-1000 handle the player
     */
    interpreter.addExtensionInstruction(Extensions.Player.SENDMESSAGE) {
        val target = it.getShort()
        val reg = it.getShort().toInt()
        val message = MiniMessage.miniMessage().deserialize(interpreter.registers[reg].toDisplay())
        player.sendMessage(message)
    }
    interpreter.addExtensionInstruction(Extensions.Player.SETSPAWNPOINT) {
        val target = it.getShort()
        val x = interpreter.registers[it.getShort().toInt()].toNumber()
        val y = interpreter.registers[it.getShort().toInt()].toNumber()
        val z = interpreter.registers[it.getShort().toInt()].toNumber()
        player.respawnPoint = Pos(x, y, z)
    }
    interpreter.addExtensionInstruction(Extensions.Player.GETLOCATION) {
        val target = it.getShort().toInt()
        interpreter.registers[target] = Value.Position(
            player.position.x,
            player.position.y,
            player.position.z,
            player.position.pitch.toDouble(),
            player.position.yaw.toDouble()
        )
    }
    interpreter.addExtensionInstruction(Extensions.Player.TELEPORT) {
        val target = it.getShort().toInt()
        val loc = it.getShort().toInt()
        val value = interpreter.registers[loc]
        if(value is Value.Position) {
            player.teleport(Pos(
                value.x,
                value.y,
                value.z,
                value.pitch.toFloat(),
                value.yaw.toFloat(),
            ))
        }
    }
}

