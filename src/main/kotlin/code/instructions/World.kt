package code.instructions

import code.Interpreter
import code.Visitable
import net.minestom.server.instance.block.Block
import parser.ArgumentList
import parser.ArgumentType
import parser.NodeBuilder

object SetBlock : Visitable {
    override val code: Int get() = 2000
    override val isExtension: Boolean get() = true
    override val command: String get() = "world.setBlock"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.COMMAND, "Location to set the block at")
            .addSingleArgument(ArgumentType.STRING, "Block")
            .build()
    override val description: String
        get() = "Set a block at a location."

    override fun visit(visitor: Interpreter) {
        val mat = visitor.environment.stack.removeLast().castToString()
        val loc = visitor.environment.stack.removeLast().castToPos()
        Block.fromNamespaceId(mat)?.let { visitor.environment.instance?.setBlock(loc, it) }
    }
}