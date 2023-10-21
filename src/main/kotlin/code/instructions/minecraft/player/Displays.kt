package code.instructions.minecraft.player

import code.Interpreter
import code.instructions.Visitable
import mm
import net.minestom.server.entity.Player
import net.minestom.server.network.packet.server.play.ActionBarPacket
import net.minestom.server.network.packet.server.play.SetTitleSubTitlePacket
import net.minestom.server.network.packet.server.play.SetTitleTextPacket
import net.minestom.server.network.packet.server.play.SetTitleTimePacket
import typechecker.ArgumentList
import typechecker.ArgumentType
import typechecker.NodeBuilder

object SendMessage : Visitable {
    override val code: Int get() = 1000
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.sendMessage"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.STRING, "Message to send")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Send the player a message in chat."

    override suspend fun visit(visitor: Interpreter) {
        val display = visitor.environment.stack.popValue().toDisplay()
        val component = mm(display)
        for(target in visitor.environment.targets) {
            if(target as? Player != null) {
                target.sendMessage(component)
            }
        }
    }
}

object SendActionBar : Visitable {
    override val code: Int get() = 1001
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.sendActionBar"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.STRING, "Actionbar to send")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Send a player a message in the actionbar."

    override suspend fun visit(visitor: Interpreter) {
        val display = visitor.environment.stack.popValue().toDisplay()
        val comp = mm(display)
        for(target in visitor.environment.targets) {
            if(target as? Player != null) {
                target.sendPacket(ActionBarPacket(comp))
            }
        }
    }
}

object SendTitle : Visitable {
    override val code: Int get() = 1002
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.sendTitleText"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.STRING, "Title to send")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Send a player a message through a title. Sends the main title"

    override suspend fun visit(visitor: Interpreter) {
        val title = visitor.environment.stack.popValue().toDisplay()
        val comp = mm(title)
        visitor.environment.targets.forEach { if(it is Player) it.sendPacket(SetTitleTextPacket(comp)) }
    }
}

object SendSubtitle : Visitable {
    override val code: Int get() = 1003
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.sendSubtitleText"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.STRING, "Subtitle to send")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Send a player a message through a title. Sends the main subtitle."

    override suspend fun visit(visitor: Interpreter) {
        val title = visitor.environment.stack.popValue().toDisplay()
        val comp = mm(title)
        visitor.environment.targets.forEach { if(it is Player) it.sendPacket(SetTitleSubTitlePacket(comp)) }
    }
}

object SendTitleTimes : Visitable {
    override val code: Int get() = 1003
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.sendTitleTimes"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "Fade in (in ticks)")
            .addSingleArgument(ArgumentType.NUMBER, "Stay time (in ticks)")
            .addSingleArgument(ArgumentType.NUMBER, "Fade out (in ticks)")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Sets a players' title times."

    override suspend fun visit(visitor: Interpreter) {
        val fadeStay = visitor.environment.stack.popValue().castToNumber()
        val fadeIn = visitor.environment.stack.popValue().castToNumber()
        val fadeOut = visitor.environment.stack.popValue().castToNumber()
        visitor.environment.targets.forEach { if(it is Player) it.sendPacket(
            SetTitleTimePacket(fadeIn.toInt(), fadeStay.toInt(), fadeOut.toInt())
        ) }
    }
}