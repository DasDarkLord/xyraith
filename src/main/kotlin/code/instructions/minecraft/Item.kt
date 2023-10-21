package code.instructions.minecraft

import code.Interpreter
import code.instructions.Visitable
import code.instructions.visitables
import mm
import net.kyori.adventure.text.Component
import net.minestom.server.item.ItemMeta
import net.minestom.server.tag.Tag
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


object ItemStore : Visitable {
    override val code: Int get() = 6002
    override val isExtension: Boolean get() = true
    override val command: String get() = "item.store"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.ITEM, "Item to manipulate")
            .addSingleArgument(ArgumentType.SYMBOL, "Symbol to set value of")
            .addSingleArgument(ArgumentType.ANY, "Value to set")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.ITEM
    override val description: String
        get() = "Set a value of a symbol to the item."

    override suspend fun visit(visitor: Interpreter) {
        val value = visitor.environment.stack.popValue()
        val symbol = visitor.environment.stack.popValue() as Value.Symbol
        val itemTmp = visitor.environment.stack.popValue() as Value.Item

        val item = itemTmp.itemStack
        val name = symbol.value

        visitor.environment.stack.pushValue(Value.Item(when(value) {
            is Value.Number -> item.withTag(Tag.Double(name), value.value)
            is Value.String -> item.withTag(Tag.String(name), value.value)
            is Value.Bool -> item.withTag(Tag.Boolean(name), value.value)
            else -> item.withTag(Tag.Double(name), 0.0)
        }))
    }
}

object ItemLoad : Visitable {
    override val code: Int get() = 6003
    override val isExtension: Boolean get() = true
    override val command: String get() = "item.load"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.ITEM, "Item to manipulate")
            .addSingleArgument(ArgumentType.SYMBOL, "Symbol to get value of")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.ANY
    override val description: String
        get() = "Get a value of a symbol from the item."

    override suspend fun visit(visitor: Interpreter) {
        val symbol = visitor.environment.stack.popValue() as Value.Symbol
        val itemTmp = visitor.environment.stack.popValue() as Value.Item

        val name = symbol.value
        val item = itemTmp.itemStack

        when {
            item.getTag(Tag.String("name")) != null ->
                visitor.environment.stack.pushValue(Value.String(item.getTag(Tag.String(name))))
            item.getTag(Tag.Double("name")) != null ->
                visitor.environment.stack.pushValue(Value.Number(item.getTag(Tag.Double(name))))
            item.getTag(Tag.Boolean("name")) != null ->
                visitor.environment.stack.pushValue(Value.Bool(item.getTag(Tag.Boolean(name))))
            else -> visitor.environment.stack.pushValue(Value.Null)
        }
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
        get() = ArgumentType.ITEM
    override val description: String
        get() = "Get an item's name."

    override suspend fun visit(visitor: Interpreter) {
        val item = (visitor.environment.stack.popValue() as Value.Item).itemStack
        visitor.environment.stack.pushValue(Value.String(item.displayName?.examinableName() ?: item.material().toString()))
    }
}