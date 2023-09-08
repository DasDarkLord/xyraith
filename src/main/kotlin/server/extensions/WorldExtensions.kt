package server.extensions

import net.minestom.server.instance.block.Block
import parser.Value
import server.interpreter.Interpreter

fun worldExtensions(interpreter: Interpreter) {
    /*
    Extensions 1001-2000 handle the world
     */
    interpreter.addExtensionInstruction(1001) {
        val target = it.getShort()
        val pos = interpreter.registers[it.getShort().toInt()]
        val block = interpreter.registers[it.getShort().toInt()].toDisplay()
        if(pos is Value.Position) {
            if(Block.fromNamespaceId(block) != null) {
                interpreter.instance?.setBlock(pos.x.toInt(), pos.y.toInt(), pos.z.toInt(), Block.fromNamespaceId(block)!!)
            } else {
                println("Warning with world.setBlock: Invalid block `$block`")
            }
        }
    }
}