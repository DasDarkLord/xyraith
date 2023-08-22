package server.extensions

import net.minestom.server.instance.InstanceContainer
import net.minestom.server.instance.block.Block
import server.Value
import server.interpreter.Interpreter

fun worldExtensions(interpreter: Interpreter, instanceContainer: InstanceContainer) {
    /*
    Extensions 1001-2000 handle the world
     */
    interpreter.addExtensionInstruction(1001) {
        val target = it.getShort()
        val pos = interpreter.registers[it.getShort().toInt()] as Value.Position
        val block = interpreter.registers[it.getShort().toInt()].toDisplay()
        if(Block.fromNamespaceId(block) != null) {
            println("no null :)")
            instanceContainer.setBlock(pos.x.toInt(), pos.y.toInt(), pos.z.toInt(), Block.fromNamespaceId(block)!!)
        } else {
            println("Warning with world.setBlock: Invalid block `$block`")
        }
    }
}