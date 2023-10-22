package code.instructions.minecraft.item

import code.Interpreter
import code.instructions.Visitable
import miniMessage
import mm
import net.kyori.adventure.text.Component
import parser.Value
import typechecker.ArgumentList
import typechecker.ArgumentType
import typechecker.NodeBuilder


object SetItemLore : Visitable {
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
        val item = visitor.environment.stack.popValue() as Value.Item
        visitor.environment.stack.pushValue(
            Value.Item(item.itemStack.withLore(newLore))
        )
    }
}

object SetItemName : Visitable {
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

object SetItemStackSize : Visitable {
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

object GetItemStackSize : Visitable {
    override val code: Int get() = 6005
    override val isExtension: Boolean get() = true
    override val command: String get() = "item.setStackSize"
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

object GetItemStackName : Visitable {
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