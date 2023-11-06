package instructions.minecraft.target

import runtime.Interpreter
import net.minestom.server.tag.Tag
import runtime.Value
import typechecker.ArgumentList
import typechecker.ArgumentType
import typechecker.NodeBuilder


object TargetStore : instructions.Visitable {
    override val code: Int get() = 5006
    override val isExtension: Boolean get() = true
    override val command: String get() = "target.store"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.SYMBOL, "Symbol to set value of")
            .addSingleArgument(ArgumentType.ANY, "Value to set")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Set a value of a symbol to the targets."
    override val pure: Boolean
        get() = false
    override suspend fun visit(visitor: Interpreter) {
        val value = visitor.environment.stack.popValue()
        val symbol = visitor.environment.stack.popValue()
        if(symbol is Value.Symbol) {
            val name = symbol.value
            for(target in visitor.environment.targets) {
                when(value) {
                    is Value.Number -> target.setTag(Tag.Double(name), value.value)
                    is Value.String -> target.setTag(Tag.String(name), value.value)
                    is Value.Bool -> target.setTag(Tag.Boolean(name), value.value)
                    is Value.Item -> target.setTag(Tag.ItemStack(name), value.itemStack)
                    else -> target.setTag(Tag.Double(name), 0.0)
                }
            }
        }
    }
}

object TargetLoad : instructions.Visitable {
    override val code: Int get() = 5007
    override val isExtension: Boolean get() = true
    override val command: String get() = "target.load"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.SYMBOL, "Symbol to get value of")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.ANY
    override val description: String
        get() = "Get a value of a symbol from the target."
    override val pure: Boolean
        get() = true
    override suspend fun visit(visitor: Interpreter) {
        val symbol = visitor.environment.stack.popValue()
        if(symbol is Value.Symbol) {
            val name = symbol.value
            val target = visitor.environment.targets.firstOrNull()

            if(target != null) {
                if(target.getTag(Tag.String(name)) != null) {
                    visitor.environment.stack.pushValue(Value.String(target.getTag(Tag.String(name))))
                    return
                }
                if(target.getTag(Tag.Double(name)) != null) {
                    visitor.environment.stack.pushValue(Value.Number(target.getTag(Tag.Double(name))))
                    return
                }
                if(target.getTag(Tag.Boolean(name)) != null) {
                    visitor.environment.stack.pushValue(Value.Bool(target.getTag(Tag.Boolean(name))))
                    return
                }
                if (target.getTag(Tag.ItemStack(name)) != null) {
                    visitor.environment.stack.pushValue(Value.Item(target.getTag(Tag.ItemStack(name))))
                    return
                }
                visitor.environment.stack.pushValue(Value.Null)
            }
        }
    }
}