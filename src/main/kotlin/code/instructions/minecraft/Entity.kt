package code.instructions.minecraft

import code.Interpreter
import code.instructions.Visitable
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import parser.Value
import typechecker.ArgumentList
import typechecker.ArgumentType
import typechecker.NodeBuilder

object SpawnEntity : Visitable {
    override val code: Int get() = 3000
    override val isExtension: Boolean get() = true
    override val command: String get() = "entity.summon"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.STRING, "Type of entity to spawn")
            .addSingleArgument(ArgumentType.LOCATION, "Location to spawn the entity")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.STRING
    override val description: String
        get() = "Spawn an entity in the world. Returns it's UUID."

    override suspend fun visit(visitor: Interpreter) {
        val pos = visitor.environment.stack.popValue().castToPos()
        val id = visitor.environment.stack.popValue().castToString()

        println("id: $id, pos: $pos")
        val entity = Entity(EntityType.fromNamespaceId(id))
        entity.setInstance(visitor.environment.instance!!)
        entity.teleport(pos)
        visitor.environment.stack.pushValue(Value.String(entity.uuid.toString()))
    }
}