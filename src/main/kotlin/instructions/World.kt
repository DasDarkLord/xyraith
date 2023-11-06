package instructions

import runtime.Interpreter
import net.minestom.server.coordinate.Pos
import net.minestom.server.instance.AnvilLoader
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.instance.block.Block
import runtime.Value
import typechecker.ArgumentList
import typechecker.ArgumentType
import typechecker.NodeBuilder

object SetBlock : instructions.Visitable {
    override val code: Int get() = 2000
    override val isExtension: Boolean get() = true
    override val command: String get() = "world.setBlock"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.LOCATION, "Location to set the block at")
            .addSingleArgument(ArgumentType.STRING, "Block")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Set a block at a location."
    override val pure: Boolean
        get() = false

    override suspend fun visit(visitor: Interpreter) {
        val mat = visitor.environment.stack.popValue().castToString()
        val loc = visitor.environment.stack.popValue() as Value.Struct
        val pos = Pos(
            loc.fields[":x"]!!.castToNumber(),
            loc.fields[":y"]!!.castToNumber(),
            loc.fields[":z"]!!.castToNumber(),
            loc.fields[":pitch"]!!.castToNumber().toFloat(),
            loc.fields[":yaw"]!!.castToNumber().toFloat(),
        )
        Block.fromNamespaceId(mat)?.let {
            visitor.environment.instance?.setBlock(pos, it)
        }
    }
}

object LoadAnvilWorld : instructions.Visitable {
    override val code: Int get() = 2001
    override val isExtension: Boolean get() = true
    override val command: String get() = "world.loadAnvilWorld"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.STRING, "World to load")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Make a world load based on region files."
    override val pure: Boolean
        get() = false

    override suspend fun visit(visitor: Interpreter) {
        val dir = visitor.environment.stack.popValue().castToString()
        val ic = visitor.environment.instance as InstanceContainer
        ic.chunkLoader = AnvilLoader(dir)
    }
}


