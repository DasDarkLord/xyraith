package code.instructions.minecraft.item

import code.Interpreter
import code.instructions.Visitable
import net.minestom.server.tag.Tag
import parser.Value
import typechecker.ArgumentList
import typechecker.ArgumentType
import typechecker.NodeBuilder

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

        visitor.environment.stack.pushValue(
            Value.Item(when(value) {
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