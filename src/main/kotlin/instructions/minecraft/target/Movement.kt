package instructions.minecraft.target

import runtime.Interpreter
import net.minestom.server.coordinate.Pos
import runtime.Value
import typechecker.ArgumentList
import typechecker.ArgumentType
import typechecker.NodeBuilder


object Teleport : instructions.Visitable {
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
    override val pure: Boolean
        get() = false
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