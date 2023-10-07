package code.instructions

import code.Interpreter
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.Player
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

    override fun visit(visitor: Interpreter) {
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

    override fun visit(visitor: Interpreter) {
        val loc = visitor.environment.stack.popValue().castToPos()
        for(entity in visitor.environment.targets) {
            entity.teleport(loc)
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

    override fun visit(visitor: Interpreter) {
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

    override fun visit(visitor: Interpreter) {
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

    override fun visit(visitor: Interpreter) {
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

    override fun visit(visitor: Interpreter) {
        val entity = visitor.environment.targets.firstOrNull() as? LivingEntity
        visitor.environment.stack.pushValue(Value.Number(entity?.health?.toDouble() ?: 0.0))
    }
}