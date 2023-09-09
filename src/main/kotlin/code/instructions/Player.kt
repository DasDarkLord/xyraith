package code.instructions

import code.Interpreter
import code.Visitable
import mm
import net.kyori.adventure.title.Title
import net.kyori.adventure.title.TitlePart
import net.minestom.server.entity.Player
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
            .addSingleArgument(ArgumentType.STRING)
            .build()

    override fun visit(visitor: Interpreter) {
        val disp = visitor.environment.stack.removeLast().toDisplay()
        for(target in visitor.environment.targets) {
            if(target as? Player != null) {
                target.sendMessage(mm(disp))
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
            .addSingleArgument(ArgumentType.STRING)
            .build()

    override fun visit(visitor: Interpreter) {
        val disp = visitor.environment.stack.removeLast().toDisplay()
        for(target in visitor.environment.targets) {
            if(target as? Player != null) {
                target.sendActionBar(mm(disp))
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
            .addSingleArgument(ArgumentType.STRING)
            .addSingleArgument(ArgumentType.STRING)
            .build()

    override fun visit(visitor: Interpreter) {
        val title = visitor.environment.stack.removeLast().toDisplay()
        val subtitle = visitor.environment.stack.removeLast().toDisplay()
        for(target in visitor.environment.targets) {
            if(target as? Player != null) {
                target.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ofMillis(50L), Duration.ofMillis(3000L), Duration.ofMillis(50L)))
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
            .addSingleArgument(ArgumentType.NUMBER)
            .build()

    override fun visit(visitor: Interpreter) {
        val health = visitor.environment.stack.removeLast().toNumber()
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
            .addSingleArgument(ArgumentType.NUMBER)
            .build()

    override fun visit(visitor: Interpreter) {
        val food = visitor.environment.stack.removeLast().toNumber()
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
            .addSingleArgument(ArgumentType.NUMBER)
            .build()

    override fun visit(visitor: Interpreter) {
        val food = visitor.environment.stack.removeLast().toNumber()
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

    override fun visit(visitor: Interpreter) {
        for(target in visitor.environment.targets) {
            if(target as? Player != null) {
                visitor.environment.stack.add(Value.Number(target.foodSaturation.toDouble()))
            }
        }
    }
}