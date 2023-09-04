package server.extensions

import net.minestom.server.entity.Player
import net.minestom.server.instance.InstanceContainer
import server.interpreter.Interpreter

fun allPossibleExtensions(interpreter: Interpreter, player: Player?, world: InstanceContainer?) {
    locExtensions(interpreter)
    numberExtension(interpreter)
    if(player != null) {
        interpreter.player = player
    }
    playerExtension(interpreter)
    if(world != null) {
        interpreter.instance = world

    }
    worldExtensions(interpreter)

}