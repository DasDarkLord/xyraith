package instructions.minecraft.item

import code.Interpreter
import instructions.Visitable
import miniMessage
import mm
import net.kyori.adventure.text.Component
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag
import parser.Value
import typechecker.ArgumentList
import typechecker.ArgumentType
import typechecker.NodeBuilder


object SetItemLore : instructions.Visitable {
    override val code: Int get() = 6000
    override val isExtension: Boolean get() = true
    override val command: String get() = "item.setLore"
    override val returnType: ArgumentType
        get() = ArgumentType.ITEM
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.ITEM, "Item to manipulate")
            .addSingleArgument(ArgumentType.STRING_LIST, "Lore to set")
            .build()

    override val description: String
        get() = "Set an item's lore."

    override suspend fun visit(visitor: Interpreter) {
        val lore = visitor.environment.stack.popValue() as Value.StringList
        val newLore = mutableListOf<Component>()
        for(line in lore.value) {
            newLore.add(mm("<!italic><gray>" + line.value))
        }
        val item = visitor.environment.stack.popValue() as? Value.Item ?: Value.Item(ItemStack.of(Material.STONE))
        visitor.environment.stack.pushValue(
            Value.Item(item.itemStack.withLore(newLore))
        )
    }
}

object SetItemName : instructions.Visitable {
    override val code: Int get() = 6001
    override val isExtension: Boolean get() = true
    override val command: String get() = "item.setName"
    override val returnType: ArgumentType
        get() = ArgumentType.ITEM
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.ITEM, "Item to manipulate")
            .addSingleArgument(ArgumentType.STRING, "Name to set")
            .build()

    override val description: String
        get() = "Set an item's name."

    override suspend fun visit(visitor: Interpreter) {
        val name = visitor.environment.stack.popValue().castToString()
        val item = visitor.environment.stack.popValue() as Value.Item
        visitor.environment.stack.pushValue(
            Value.Item(item.itemStack.withDisplayName(mm("<!italic>$name")))

        )
    }
}

object SetItemStackSize : instructions.Visitable {
    override val code: Int get() = 6004
    override val isExtension: Boolean get() = true
    override val command: String get() = "item.setStackSize"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.ITEM, "Item to manipulate")
            .addSingleArgument(ArgumentType.NUMBER, "Stack size")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.ITEM
    override val description: String
        get() = "Set an item's stack size."

    override suspend fun visit(visitor: Interpreter) {
        val size = visitor.environment.stack.popValue() as Value.Number
        val item = (visitor.environment.stack.popValue() as Value.Item).itemStack
        visitor.environment.stack.pushValue(Value.Item(item.withAmount(size.value.toInt())))
    }
}

object GetItemStackSize : instructions.Visitable {
    override val code: Int get() = 6005
    override val isExtension: Boolean get() = true
    override val command: String get() = "item.getStackSize"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.ITEM, "Item to check")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.ITEM
    override val description: String
        get() = "Get an item's stack size."

    override suspend fun visit(visitor: Interpreter) {
        val item = (visitor.environment.stack.popValue() as Value.Item).itemStack
        visitor.environment.stack.pushValue(Value.Number(item.amount().toDouble()))
    }
}

object GetItemStackName : instructions.Visitable {
    override val code: Int get() = 6006
    override val isExtension: Boolean get() = true
    override val command: String get() = "item.getName"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.ITEM, "Item to check")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.STRING
    override val description: String
        get() = "Get an item's name."

    override suspend fun visit(visitor: Interpreter) {
        val item = (visitor.environment.stack.popValue() as Value.Item).itemStack
        visitor.environment.stack.pushValue(Value.String(miniMessage.serialize(item.displayName ?: Component.text(item.material().name()))))
    }
}

object GetItemLore : instructions.Visitable {
    override val code: Int
        get() = 6007
    override val isExtension: Boolean
        get() = true
    override val command: String
        get() = "item.getLore"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.ITEM, "Item to get the lore of")
            .build()
    override val description: String
        get() = "Gets the lore of an item"
    override val returnType: ArgumentType
        get() = ArgumentType.STRING_LIST

    override suspend fun visit(visitor: Interpreter) {
        val item = ((visitor.environment.stack.popValue()) as Value.Item).itemStack
        val lore = mutableListOf<String>()
        for (loreLine in item.lore) {
            lore.add(miniMessage.serialize(loreLine))
        }
        visitor.environment.stack.pushValue(Value.StringList(lore.map { Value.String(it) }))
    }

}

object GetItemDamage : instructions.Visitable {
    override val code: Int
        get() = 6008
    override val isExtension: Boolean
        get() = true
    override val command: String
        get() = "item.getDamage"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.ITEM, "The item to get the damage of")
            .build()
    override val description: String
        get() = "Gets the damage of an item"
    override val returnType: ArgumentType
        get() = ArgumentType.NUMBER

    override suspend fun visit(visitor: Interpreter) {
        val item = (visitor.environment.stack.popValue() as Value.Item).itemStack
        val damage = Value.Number(item.meta().damage.toDouble())
        visitor.environment.stack.pushValue(damage)
    }

}

object SetItemDamage : instructions.Visitable {
    override val code: Int
        get() = 6009
    override val isExtension: Boolean
        get() = true
    override val command: String
        get() = "item.setDamage"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.ITEM, "Item to set damage of")
            .addSingleArgument(ArgumentType.NUMBER, "Damage to set")
            .build()
    override val description: String
        get() = "Sets an items damage"
    override val returnType: ArgumentType
        get() = ArgumentType.ITEM

    override suspend fun visit(visitor: Interpreter) {
        val durability = visitor.environment.stack.popValue().castToNumber()
        val item = (visitor.environment.stack.popValue() as Value.Item).itemStack
        visitor.environment.stack.pushValue(Value.Item(
            item.withTag(Tag.Integer("Damage").defaultValue(0), durability.toInt())
        ))
    }

}
