package server.extensions

import net.minestom.server.entity.Player
import net.minestom.server.instance.InstanceContainer
import server.interpreter.Interpreter

fun allPossibleExtensions(interpreter: Interpreter, player: Player?, world: InstanceContainer?) {
    locExtensions(interpreter)
    if(player != null) {
        playerExtension(interpreter, player)
    }
    if(world != null) {
        worldExtensions(interpreter, world)
    }

}