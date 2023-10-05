package code.instructions

import code.Interpreter
import code.Visitable
import mm
import net.minestom.server.event.player.PlayerChatEvent
import net.minestom.server.instance.AnvilLoader
import net.minestom.server.instance.InstanceContainer
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
            .addSingleArgument(ArgumentType.LOCATION, "Location to set the block at")
            .addSingleArgument(ArgumentType.STRING, "Block")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Set a block at a location."

    override fun visit(visitor: Interpreter) {
        val mat = visitor.environment.stack.popValue().castToString()
        val loc = visitor.environment.stack.popValue().castToPos()

        Block.fromNamespaceId(mat)?.let {
            visitor.environment.instance?.setBlock(loc, it)
        }
    }
}

object LoadAnvilWorld : Visitable {
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

    override fun visit(visitor: Interpreter) {
        val dir = visitor.environment.stack.popValue().castToString()
        val ic = visitor.environment.instance as InstanceContainer
        ic.chunkLoader = AnvilLoader(dir)
    }
}

object SetChatFormat : Visitable {
    override val code: Int get() = 2001
    override val isExtension: Boolean get() = true
    override val command: String get() = "world.setChatFormat"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.STRING, "Format of the message.")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Requires `chat` event.\nSet a the format of the outgoing chat message.\nUse {player} to auto-fill in the player, and {message} for the message."

    override fun visit(visitor: Interpreter) {
        val format = visitor.environment.stack.popValue().castToString()
        val event = visitor.environment.event
        if(event is PlayerChatEvent) {
            val replaced = format.replace("{player}", event.player.username)
            event.setChatFormat { mm(replaced) }
        }
    }
}