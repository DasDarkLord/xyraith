package instructions.minecraft.target

import ai.WalkToPositionGoal
import code.Interpreter
import instructions.Visitable
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.ai.goal.RandomStrollGoal
import parser.Value
import typechecker.ArgumentList
import typechecker.ArgumentType
import typechecker.NodeBuilder


object TargetWalkTo : instructions.Visitable {
    override val code: Int get() = 5100
    override val isExtension: Boolean get() = true
    override val command: String get() = "target.walkTo"
    override val arguments: ArgumentList get() = NodeBuilder().addSingleArgument(ArgumentType.LOCATION, "Location to walk to").build()
    override val description: String get() = "Tells a mob's AI to pathfind to a certain location"
    override val returnType: ArgumentType get() = ArgumentType.NONE

    override suspend fun visit(visitor: Interpreter) {
        val posStruct = visitor.environment.stack.popValue() as Value.Struct
        val pos = Pos(
            posStruct.fields[":x"]!!.castToNumber(),
            posStruct.fields[":y"]!!.castToNumber(),
            posStruct.fields[":z"]!!.castToNumber(),
            posStruct.fields[":pitch"]!!.castToNumber().toFloat(),
            posStruct.fields[":yaw"]!!.castToNumber().toFloat(),
        )

        for (target in visitor.environment.targets) {
            if (target as? EntityCreature != null) {
                target.aiGroups.clear()
                target.addAIGroup(
                    listOf(WalkToPositionGoal(target, pos)),
                    emptyList()
                )
            }
        }
    }

}

object TargetAIStroll : instructions.Visitable {
    override val code: Int get() = 5101
    override val isExtension: Boolean get() = true
    override val command: String get() = "target.stroll"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addOptionalArgument(ArgumentType.NUMBER, Value.Number(16.0), "The radius it should walk around in")
            .build()

    override val description: String get() = "Makes the target randomly walk/stroll around"
    override val returnType: ArgumentType get() = ArgumentType.NONE

    override suspend fun visit(visitor: Interpreter) {
        val radius = visitor.environment.stack.popValue().castToNumber().toInt()

        for (target in visitor.environment.targets) {
            if (target as? EntityCreature != null) {
                target.aiGroups.clear()
                target.addAIGroup(
                    listOf(RandomStrollGoal(target, radius)),
                    emptyList()
                )
            }
        }
    }

}

object TargetAIDisable : instructions.Visitable {
    override val code: Int
        get() = 5102
    override val isExtension: Boolean
        get() = true
    override val command: String
        get() = "target.noai"
    override val arguments: ArgumentList
        get() = NodeBuilder().build()
    override val description: String
        get() = "Disables the targets AI"
    override val returnType: ArgumentType
        get() = ArgumentType.NONE

    override suspend fun visit(visitor: Interpreter) {
        for (target in visitor.environment.targets) {
            if (target as? EntityCreature != null) {
                target.aiGroups.clear()
            }
        }
    }

}