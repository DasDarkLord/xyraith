package code.instructions.minecraft

import code.Interpreter
import code.instructions.Visitable
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.Player
import net.minestom.server.tag.Tag
import parser.Value
import typechecker.ArgumentList
import typechecker.ArgumentType
import typechecker.NodeBuilder

object TargetUUID : Visitable {
    override val code: Int get() = 5000
    override val isExtension: Boolean get() = true
    override val command: String get() = "target.uuid"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.STRING
    override val description: String
        get() = "Get the UUID of a target"

    override suspend fun visit(visitor: Interpreter) {
        val target = visitor.environment.targets.firstOrNull()
        if(target as? Player != null) {
            visitor.environment.stack.pushValue(Value.String(target.uuid.toString()))
        } else {
            visitor.environment.stack.pushValue(Value.Null)
        }
    }
}

object Teleport : Visitable {
    override val code: Int get() = 5001
    override val isExtension: Boolean get() = true
    override val command: String get() = "target.teleport"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.LOCATION, "Location to teleport")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Teleport the targets to a location."

    override suspend fun visit(visitor: Interpreter) {
        val pos = visitor.environment.stack.popValue() as Value.Struct
        for(entity in visitor.environment.targets) {
            entity.teleport(
                Pos(
                    pos.fields[":x"]!!.castToNumber(),
                    pos.fields[":y"]!!.castToNumber(),
                    pos.fields[":z"]!!.castToNumber(),
                    pos.fields[":pitch"]!!.castToNumber().toFloat(),
                    pos.fields[":yaw"]!!.castToNumber().toFloat(),
                )
            )

        }
    }
}

object Damage : Visitable {
    override val code: Int get() = 5002
    override val isExtension: Boolean get() = true
    override val command: String get() = "target.damage"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "Damage to deal")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Deals damage to the targets"

    override suspend fun visit(visitor: Interpreter) {
        val num = visitor.environment.stack.popValue().castToNumber()
        for(entity in visitor.environment.targets) {
            if(entity is LivingEntity) {
                entity.health -= num.toFloat()
            }
        }
    }
}

object Heal : Visitable {
    override val code: Int get() = 5002
    override val isExtension: Boolean get() = true
    override val command: String get() = "target.heal"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "Health to heal")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Heals targets' health"

    override suspend fun visit(visitor: Interpreter) {
        val num = visitor.environment.stack.popValue().castToNumber()
        for(entity in visitor.environment.targets) {
            if(entity is LivingEntity) {
                entity.health += num.toFloat()
            }
        }
    }
}

object SetHealth : Visitable {
    override val code: Int get() = 5003
    override val isExtension: Boolean get() = true
    override val command: String get() = "target.setHealth"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "Value to set health to")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Sets the targets' health to given value"

    override suspend fun visit(visitor: Interpreter) {
        val num = visitor.environment.stack.popValue().castToNumber()
        for(entity in visitor.environment.targets) {
            if(entity is LivingEntity) {
                entity.health = num.toFloat()
            }
        }
    }
}

object GetHealth : Visitable {
    override val code: Int get() = 5004
    override val isExtension: Boolean get() = true
    override val command: String get() = "target.getHealth"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NUMBER
    override val description: String
        get() = "Gets the target's health"

    override suspend fun visit(visitor: Interpreter) {
        val entity = visitor.environment.targets.firstOrNull() as? LivingEntity
        visitor.environment.stack.pushValue(Value.Number(entity?.health?.toDouble() ?: 0.0))
    }
}

object TargetStore : Visitable {
    override val code: Int get() = 5005
    override val isExtension: Boolean get() = true
    override val command: String get() = "target.store"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.SYMBOL, "Symbol to set value of")
            .addSingleArgument(ArgumentType.ANY, "Value to set")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Set a value of a symbol to the targets."

    override suspend fun visit(visitor: Interpreter) {
        val value = visitor.environment.stack.popValue()
        val symbol = visitor.environment.stack.popValue()
        if(symbol is Value.Symbol) {
            val name = symbol.value
            for(target in visitor.environment.targets) {
                when(value) {
                    is Value.Number -> target.setTag(Tag.Double(name), value.value)
                    is Value.String -> target.setTag(Tag.String(name), value.value)
                    is Value.Bool -> target.setTag(Tag.Boolean(name), value.value)
                    else -> target.setTag(Tag.Double(name), 0.0)
                }
            }
        }
    }
}

object TargetLoad : Visitable {
    override val code: Int get() = 5006
    override val isExtension: Boolean get() = true
    override val command: String get() = "target.load"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.SYMBOL, "Symbol to get value of")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.ANY
    override val description: String
        get() = "Get a value of a symbol from the target."

    override suspend fun visit(visitor: Interpreter) {
        val symbol = visitor.environment.stack.popValue()
        if(symbol is Value.Symbol) {
            val name = symbol.value
            val target = visitor.environment.targets.firstOrNull()

            if(target != null) {
                if(target.getTag(Tag.String(name)) != null) {
                    visitor.environment.stack.pushValue(Value.String(target.getTag(Tag.String(name))))
                    return
                }
                if(target.getTag(Tag.Double(name)) != null) {
                    visitor.environment.stack.pushValue(Value.Number(target.getTag(Tag.Double(name))))
                    return
                }
                if(target.getTag(Tag.Boolean(name)) != null) {
                    visitor.environment.stack.pushValue(Value.Bool(target.getTag(Tag.Boolean(name))))
                    return
                }
                visitor.environment.stack.pushValue(Value.Null)
            }
        }
    }
}