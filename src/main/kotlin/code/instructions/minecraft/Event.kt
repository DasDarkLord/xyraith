package code.instructions.minecraft

import code.Interpreter
import code.instructions.Visitable
import mm
import net.minestom.server.event.player.PlayerBlockBreakEvent
import net.minestom.server.event.player.PlayerBlockInteractEvent
import net.minestom.server.event.player.PlayerBlockPlaceEvent
import net.minestom.server.event.player.PlayerChatEvent
import net.minestom.server.event.trait.BlockEvent
import net.minestom.server.event.trait.CancellableEvent
import typechecker.ArgumentList
import typechecker.ArgumentType
import typechecker.NodeBuilder
import parser.Value

object EventChatSetFormat : Visitable {
    override val code: Int get() = 3000
    override val isExtension: Boolean get() = true
    override val command: String get() = "event.chat.setChatFormat"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.STRING, "Final outgoing message.")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Requires `chat` event.\nSet a the format of the outgoing chat message."

    override suspend fun visit(visitor: Interpreter) {
        val message = visitor.environment.stack.popValue().castToString()
        val event = visitor.environment.event
        if(event is PlayerChatEvent) {
            event.setChatFormat { mm(message) }
        }
    }
}

object EventChatGetMessage : Visitable {
    override val code: Int get() = 3001
    override val isExtension: Boolean get() = true
    override val command: String get() = "event.chat.getMessage"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.STRING
    override val description: String
        get() = "Requires `chat` event.\nGets the chat message of the event."

    override suspend fun visit(visitor: Interpreter) {
        val format = visitor.environment.stack.popValue().castToString()
        val event = visitor.environment.event
        if(event is PlayerChatEvent) {
            visitor.environment.stack.pushValue(Value.String(event.message))
        }
    }
}

object EventSetCancelled : Visitable {
    override val code: Int get() = 3002
    override val isExtension: Boolean get() = true
    override val command: String get() = "event.setCancelled"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.BOOL, "Sets the event as cancelled or not.")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Requires a cancellable event. Sets whether an event is cancelled or not."

    override suspend fun visit(visitor: Interpreter) {
        val event = visitor.environment.event
        if(event is CancellableEvent) {
            event.isCancelled = true
        }
    }
}



object EventBlockGetBlock : Visitable {
    override val code: Int get() = 3003
    override val isExtension: Boolean get() = true
    override val command: String get() = "event.block.getBlock"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.STRING
    override val description: String
        get() = "Requires a block event.\nGets the block material affected."

    override suspend fun visit(visitor: Interpreter) {
        val event = visitor.environment.event
        if(event is BlockEvent) {
            visitor.environment.stack.pushValue(Value.String(
                event.block.namespace().toString()
            ))
        }
    }
}

object EventGetLocation : Visitable {
    override val code: Int get() = 3004
    override val isExtension: Boolean get() = true
    override val command: String get() = "event.getLocation"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.LOCATION
    override val description: String
        get() = "Requires an event involving a location.\nGets the location of the event."

    override suspend fun visit(visitor: Interpreter) {
        val event = visitor.environment.event
        if(event is PlayerBlockBreakEvent) {
            visitor.environment.stack.pushValue(Value.Struct(
                ArgumentType.LOCATION,
                mutableMapOf(
                    ":x" to Value.Number(event.blockPosition.x()),
                    ":y" to Value.Number(event.blockPosition.y()),
                    ":z" to Value.Number(event.blockPosition.z()),
                    ":pitch" to Value.Number(0.0),
                    ":yaw" to Value.Number(0.0),
                )
            ))
        }
        if(event is PlayerBlockPlaceEvent) {
            visitor.environment.stack.pushValue(Value.Struct(
                ArgumentType.LOCATION,
                mutableMapOf(
                    ":x" to Value.Number(event.blockPosition.x()),
                    ":y" to Value.Number(event.blockPosition.y()),
                    ":z" to Value.Number(event.blockPosition.z()),
                    ":pitch" to Value.Number(0.0),
                    ":yaw" to Value.Number(0.0),
                )
            ))
        }
        if(event is PlayerBlockInteractEvent) {
            visitor.environment.stack.pushValue(Value.Struct(
                ArgumentType.LOCATION,
                mutableMapOf(
                    ":x" to Value.Number(event.blockPosition.x()),
                    ":y" to Value.Number(event.blockPosition.y()),
                    ":z" to Value.Number(event.blockPosition.z()),
                    ":pitch" to Value.Number(0.0),
                    ":yaw" to Value.Number(0.0),
                )
            ))
        }
    }
}