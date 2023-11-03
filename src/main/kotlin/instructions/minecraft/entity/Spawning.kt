package instructions.minecraft.entity

import code.Interpreter
import instructions.Visitable
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.item.ItemEntityMeta
import parser.Value
import typechecker.ArgumentList
import typechecker.ArgumentType
import typechecker.NodeBuilder

object SpawnEntity : instructions.Visitable {
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
        val pos = visitor.environment.stack.popValue() as Value.Struct
        val id = visitor.environment.stack.popValue().castToString()

        println("id: $id, pos: $pos")
        val entity = EntityCreature(EntityType.fromNamespaceId(id))
        entity.setInstance(visitor.environment.instance!!, Pos(
            pos.fields[":x"]!!.castToNumber(),
            pos.fields[":y"]!!.castToNumber(),
            pos.fields[":z"]!!.castToNumber(),
            pos.fields[":pitch"]!!.castToNumber().toFloat(),
            pos.fields[":yaw"]!!.castToNumber().toFloat(),
        )
        )

        visitor.environment.stack.pushValue(Value.String(entity.uuid.toString()))
    }
}

object SpawnItem : instructions.Visitable {
    override val code: Int get() = 3001
    override val isExtension: Boolean get() = true
    override val command: String get() = "entity.summonItem"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.ITEM, "Item to spawn")
            .addSingleArgument(ArgumentType.LOCATION, "Location to spawn the entity")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.STRING
    override val description: String
        get() = "Spawn a dropped item in the world. Returns it's UUID."
    override suspend fun visit(visitor: Interpreter) {
        val pos = visitor.environment.stack.popValue() as Value.Struct
        val item = visitor.environment.stack.popValue() as Value.Item

        val entity = Entity(EntityType.ITEM)
        entity.setInstance(visitor.environment.instance!!)

        entity.teleport(
            Pos(
            pos.fields[":x"]!!.castToNumber(),
            pos.fields[":y"]!!.castToNumber(),
            pos.fields[":z"]!!.castToNumber(),
            pos.fields[":pitch"]!!.castToNumber().toFloat(),
            pos.fields[":yaw"]!!.castToNumber().toFloat(),
        )
        )

        val meta = entity.entityMeta as ItemEntityMeta
        meta.item = item.itemStack

        visitor.environment.stack.pushValue(Value.String(entity.uuid.toString()))
    }
}