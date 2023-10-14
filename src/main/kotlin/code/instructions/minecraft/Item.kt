package code.instructions.minecraft

import code.Interpreter
import code.instructions.Visitable
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
        get() = "Set an item's lore"

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
        get() = "Set an item's name"

    override suspend fun visit(visitor: Interpreter) {
        val name = visitor.environment.stack.popValue().castToString()
        val item = visitor.environment.stack.popValue() as Value.Item
        visitor.environment.stack.pushValue(
            Value.Item(item.itemStack.withDisplayName(mm("<!italic>$name")))

        )
    }
}