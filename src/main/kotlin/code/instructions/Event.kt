package code.instructions

import code.Interpreter
import mm
import net.minestom.server.event.player.PlayerChatEvent
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

    override fun visit(visitor: Interpreter) {
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

    override fun visit(visitor: Interpreter) {
        val format = visitor.environment.stack.popValue().castToString()
        val event = visitor.environment.event
        if(event is PlayerChatEvent) {
            visitor.environment.stack.pushValue(Value.String(event.message))
        }
    }
}

object SetCancelled : Visitable {
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

    override fun visit(visitor: Interpreter) {
        val event = visitor.environment.event
        if(event is CancellableEvent) {
            event.isCancelled = true
        }
    }
}