package code.instructions

import code.Interpreter
import mm
import net.kyori.adventure.title.Title
import net.kyori.adventure.title.TitlePart
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.entity.damage.DamageType
import typechecker.ArgumentList
import typechecker.ArgumentType
import typechecker.NodeBuilder
import parser.Value
import java.time.Duration

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
        for(target in visitor.environment.targets) {
            if(target as? Player != null) {
                target.sendMessage(mm(display))
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
        for(target in visitor.environment.targets) {
            if(target as? Player != null) {
                target.sendActionBar(mm(display))
            }
        }
    }
}

object SendTitle : Visitable {
    override val code: Int get() = 1002
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.sendTitle"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.STRING, "Title to send")
            .addSingleArgument(ArgumentType.STRING, "Subtitle to send")
            .addOptionalArgument(ArgumentType.NUMBER, Value.Number(3000.0), "Duration")
            .addOptionalArgument(ArgumentType.NUMBER, Value.Number(500.0),"Fade in time")
            .addOptionalArgument(ArgumentType.NUMBER, Value.Number(500.0),"Fade out time")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Send a player a message through a title."

    override suspend fun visit(visitor: Interpreter) {
        var fadeOut = 500.0
        var fadeIn = 500.0
        var duration = 3000.0

        if(visitor.environment.argumentCount >= 3.toByte()) {
            if(visitor.environment.argumentCount >= 4.toByte()) {
                if(visitor.environment.argumentCount >= 5.toByte()) {
                    fadeOut = visitor.environment.stack.popValue().castToNumber()
                }
                fadeIn = visitor.environment.stack.popValue().castToNumber()
            }
            duration = visitor.environment.stack.popValue().castToNumber()
        }

        val subtitle = visitor.environment.stack.popValue().toDisplay()
        val title = visitor.environment.stack.popValue().toDisplay()

        for(target in visitor.environment.targets) {
            if(target as? Player != null) {
                target.sendTitlePart(TitlePart.TIMES, Title.Times.times(
                    Duration.ofMillis(fadeIn.toLong()),
                    Duration.ofMillis(duration.toLong()),
                    Duration.ofMillis(fadeOut.toLong())))
                target.sendTitlePart(TitlePart.TITLE, mm(title))
                target.sendTitlePart(TitlePart.SUBTITLE, mm(subtitle))
            }
        }
    }
}

object SetHunger : Visitable {
    override val code: Int get() = 1102
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.setHunger"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "Points to set hunger to")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Set a player's hunger points."

    override suspend fun visit(visitor: Interpreter) {
        val food = visitor.environment.stack.popValue().castToNumber()
        for(target in visitor.environment.targets) {
            if(target as? Player != null) {
                target.food = food.toInt()
            }
        }
    }
}

object GetHunger : Visitable {
    override val code: Int get() = 1105
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.getSaturation"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NUMBER
    override val description: String
        get() = "Get a player's hunger points."

    override suspend fun visit(visitor: Interpreter) {
        for(target in visitor.environment.targets) {
            if(target as? Player != null) {
                visitor.environment.stack.pushValue(Value.Number(target.food.toDouble()))
            }
        }
    }
}

object SetSaturation : Visitable {
    override val code: Int get() = 1104
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.setSaturation"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "Points to set saturation to")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Set a player's saturation points."

    override suspend fun visit(visitor: Interpreter) {
        val food = visitor.environment.stack.popValue().castToNumber()
        for(target in visitor.environment.targets) {
            if(target as? Player != null) {
                target.foodSaturation = food.toFloat()
            }
        }
    }
}

object GetSaturation : Visitable {
    override val code: Int get() = 1106
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.getSaturation"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NUMBER
    override val description: String
        get() = "Get a player's saturation points."

    override suspend fun visit(visitor: Interpreter) {
        for(target in visitor.environment.targets) {
            if(target as? Player != null) {
                visitor.environment.stack.pushValue(Value.Number(target.foodSaturation.toDouble()))
            }
        }
    }
}

object PlayerUsername : Visitable {
    override val code: Int get() = 1109
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.username"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.STRING
    override val description: String
        get() = "Get the username of a player"

    override suspend fun visit(visitor: Interpreter) {
        val target = visitor.environment.targets.firstOrNull()
        if(target as? Player != null) {
            visitor.environment.stack.pushValue(Value.String(target.username))
        } else {
            visitor.environment.stack.pushValue(Value.Null)
        }
    }
}

object SetGamemode : Visitable {
    override val code: Int get() = 1300
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.gamemode"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.STRING, "Game mode to change to.")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Change a player's game mode."

    override suspend fun visit(visitor: Interpreter) {
        val mode = visitor.environment.stack.popValue().castToString()
        if(mode == "gmc" || mode == "c" || mode == "creative") {
            for(target in visitor.environment.targets) {
                if(target is Player) {
                    target.gameMode = GameMode.CREATIVE
                }
            }
        }
        if(mode == "gms" || mode == "s" || mode == "survival") {
            for(target in visitor.environment.targets) {
                if(target is Player) {
                    target.gameMode = GameMode.SURVIVAL
                }
            }
        }
        if(mode == "gma" || mode == "a" || mode == "adventure") {
            for(target in visitor.environment.targets) {
                if(target is Player) {
                    target.gameMode = GameMode.ADVENTURE
                }
            }
        }
        if(mode == "gmsp" || mode == "sp" || mode == "spectator") {
            for(target in visitor.environment.targets) {
                if(target is Player) {
                    target.gameMode = GameMode.SPECTATOR
                }
            }
        }
    }
}