package code.instructions

import code.Interpreter
import code.Visitable
import mm
import net.kyori.adventure.title.Title
import net.kyori.adventure.title.TitlePart
import net.minestom.server.entity.Player
import net.minestom.server.entity.damage.DamageType
import parser.ArgumentList
import parser.ArgumentType
import parser.NodeBuilder
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
    override val description: String
        get() = "Send the player a message in chat."

    override fun visit(visitor: Interpreter) {
        val display = visitor.environment.stack.removeLast().toDisplay()
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
    override val description: String
        get() = "Send a player a message in the actionbar."

    override fun visit(visitor: Interpreter) {
        val display = visitor.environment.stack.removeLast().toDisplay()
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
    override val description: String
        get() = "Send a player a message through a title."

    override fun visit(visitor: Interpreter) {
        var fadeOut = 500.0
        var fadeIn = 500.0
        var duration = 3000.0

        if(visitor.environment.argumentCount >= 3.toByte()) {
            if(visitor.environment.argumentCount >= 4.toByte()) {
                if(visitor.environment.argumentCount >= 5.toByte()) {
                    fadeOut = visitor.environment.stack.removeLast().castToNumber()
                }
                fadeIn = visitor.environment.stack.removeLast().castToNumber()
            }
            duration = visitor.environment.stack.removeLast().castToNumber()
        }

        val subtitle = visitor.environment.stack.removeLast().toDisplay()
        val title = visitor.environment.stack.removeLast().toDisplay()

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

object SetHealth : Visitable {
    override val code: Int get() = 1100
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.setHealth"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "Health to set (2 HP = 1 heart)")
            .build()
    override val description: String
        get() = "Set a player's health."

    override fun visit(visitor: Interpreter) {
        val health = visitor.environment.stack.removeLast().castToNumber()
        for(target in visitor.environment.targets) {
            if(target as? Player != null) {
                target.health = health.toFloat()
            }
        }
    }
}

object GetHealth : Visitable {
    override val code: Int get() = 1101
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.getHealth"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .build()
    override val description: String
        get() = "Get a player's health."

    override fun visit(visitor: Interpreter) {
        for(target in visitor.environment.targets) {
            if(target as? Player != null) {
                visitor.environment.stack.add(Value.Number(target.health.toDouble()))
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
    override val description: String
        get() = "Set a player's hunger points."

    override fun visit(visitor: Interpreter) {
        val food = visitor.environment.stack.removeLast().castToNumber()
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
    override val description: String
        get() = "Get a player's hunger points."

    override fun visit(visitor: Interpreter) {
        for(target in visitor.environment.targets) {
            if(target as? Player != null) {
                visitor.environment.stack.add(Value.Number(target.food.toDouble()))
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
    override val description: String
        get() = "Set a player's saturation points."

    override fun visit(visitor: Interpreter) {
        val food = visitor.environment.stack.removeLast().castToNumber()
        for(target in visitor.environment.targets) {
            if(target as? Player != null) {
                target.foodSaturation = food.toFloat()
            }
        }
    }
}

object GetSaturation : Visitable {
    override val code: Int get() = 1105
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.getSaturation"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .build()
    override val description: String
        get() = "Get a player's saturation points."

    override fun visit(visitor: Interpreter) {
        for(target in visitor.environment.targets) {
            if(target as? Player != null) {
                visitor.environment.stack.add(Value.Number(target.foodSaturation.toDouble()))
            }
        }
    }
}

object Damage : Visitable {
    override val code: Int get() = 1106
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.damage"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .build()
    override val description: String
        get() = "Get a player's saturation points."

    override fun visit(visitor: Interpreter) {
        for(target in visitor.environment.targets) {
            if(target as? Player != null) {
                target.damage(DamageType.VOID, visitor.environment.stack.removeLast().castToNumber().toFloat())
            }
        }
    }
}

object Heal : Visitable {
    override val code: Int get() = 1107
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.heal"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .build()
    override val description: String
        get() = "Get a player's saturation points."

    override fun visit(visitor: Interpreter) {
        for(target in visitor.environment.targets) {
            if(target as? Player != null) {
                target.health += visitor.environment.stack.removeLast().castToNumber().toFloat()
            }
        }
    }
}