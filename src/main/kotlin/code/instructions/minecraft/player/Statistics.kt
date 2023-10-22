package code.instructions.minecraft.player

import code.Interpreter
import code.instructions.Visitable
import net.minestom.server.entity.Player
import net.minestom.server.network.packet.server.play.ChangeGameStatePacket
import parser.Value
import typechecker.ArgumentList
import typechecker.ArgumentType
import typechecker.NodeBuilder


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

object PlayerUuid : Visitable {
    override val code: Int get() = 1107
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.uuid"
    override val arguments: ArgumentList get() = NodeBuilder().build()
    override val description: String get() = "Get the uuid of a player"
    override val returnType: ArgumentType get() = ArgumentType.STRING

    override suspend fun visit(visitor: Interpreter) {
        val target = visitor.environment.targets.firstOrNull()
        if (target as? Player != null) {
            visitor.environment.stack.pushValue(Value.String(target.uuid.toString()))
        } else visitor.environment.stack.pushValue(Value.Null)
    }
}

object UnsafeSetGamemode : Visitable {
    override val code: Int get() = 1300
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.unsafe.setGamemode"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "Game mode to change to.")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Change a player's game mode."

    override suspend fun visit(visitor: Interpreter) {
        val mode = visitor.environment.stack.popValue().castToNumber().toFloat()
        for(target in visitor.environment.targets) {
            if(target is Player) {
                target.sendPacket(ChangeGameStatePacket(ChangeGameStatePacket.Reason.CHANGE_GAMEMODE, mode))
            }
        }
    }
}

object SetGamemode : Visitable {
    override val code: Int get() = 1301
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.setGamemode"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType(":gamemode", listOf()), "Game mode to change to.")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Change a player's game mode."

    override suspend fun visit(visitor: Interpreter) {
        val mode = (visitor.environment.stack.popValue() as Value.Struct).fields[":id"]!!.castToNumber().toFloat()
        for(target in visitor.environment.targets) {
            if(target is Player) {
                target.sendPacket(ChangeGameStatePacket(ChangeGameStatePacket.Reason.CHANGE_GAMEMODE, mode))
            }
        }
    }
}

object SetExpProgress : Visitable {
    override val code: Int get() = 1302
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.setExpProgress"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "XP progress to set (in %)")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Change a player's XP progress."

    override suspend fun visit(visitor: Interpreter) {
        val exp = visitor.environment.stack.popValue().castToNumber().toFloat()
        for(target in visitor.environment.targets) {
            if(target is Player) {
                target.exp = (exp/100)
            }
        }
    }
}

object SetExpLevel : Visitable {
    override val code: Int get() = 1303
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.setExpLevel"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "XP level to set")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Change a player's XP level."

    override suspend fun visit(visitor: Interpreter) {
        val level = visitor.environment.stack.popValue().castToNumber().toFloat()
        for(target in visitor.environment.targets) {
            if(target is Player) {
                target.level = level.toInt()
            }
        }
    }
}

object GetExpProgress : Visitable {
    override val code: Int get() = 1304
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.getExpProgress"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "XP progress to set (in %)")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Change a player's XP progress."

    override suspend fun visit(visitor: Interpreter) {
        val exp = visitor.environment.stack.popValue().castToNumber().toFloat()
        for(target in visitor.environment.targets) {
            if(target is Player) {
                target.exp = (exp/100)
            }
        }
    }
}

object GetExpLevel : Visitable {
    override val code: Int get() = 1305
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.getExpLevel"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "XP level to set")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Change a player's XP level."

    override suspend fun visit(visitor: Interpreter) {
        val level = visitor.environment.stack.popValue().castToNumber().toFloat()
        for(target in visitor.environment.targets) {
            if(target is Player) {
                target.level = level.toInt()
            }
        }
    }
}