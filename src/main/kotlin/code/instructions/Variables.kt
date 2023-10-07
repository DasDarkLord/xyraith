package code.instructions

import code.Interpreter
import globalVariables
import net.minestom.server.tag.Tag
import typechecker.ArgumentList
import typechecker.ArgumentType
import typechecker.NodeBuilder
import parser.Value

object FLocalStore : Visitable {
    override val code: Int get() = 50
    override val isExtension: Boolean get() = false
    override val command: String get() = "store"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.SYMBOL, "Symbol to set value of")
            .addSingleArgument(ArgumentType.ANY, "Value to set")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Set a value of a symbol in local scope."

    override fun visit(visitor: Interpreter) {
        val value = visitor.environment.stack.popValue()
        val symbol = visitor.environment.stack.popValue()
        if(symbol is Value.Symbol) {
            visitor.environment.localVariables[symbol.value] = value
        }
    }
}

object FLocalLoad : Visitable {
    override val code: Int get() = 51
    override val isExtension: Boolean get() = false
    override val command: String get() = "load"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.SYMBOL, "Symbol to get value of")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.ANY
    override val description: String
        get() = "Get a value of a symbol in local scope."

    override fun visit(visitor: Interpreter) {
        val symbol = visitor.environment.stack.popValue()
        if(symbol is Value.Symbol) {
            val push = visitor.environment.localVariables[symbol.value] ?: Value.Null
            visitor.environment.stack.pushValue(push)
        }
    }
}

object GlobalStore : Visitable {
    override val code: Int get() = 52
    override val isExtension: Boolean get() = false
    override val command: String get() = "global.store"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.SYMBOL, "Symbol to set value of")
            .addSingleArgument(ArgumentType.ANY, "Value to set")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Set a value of a symbol in global scope."

    override fun visit(visitor: Interpreter) {
        val value = visitor.environment.stack.popValue()
        val symbol = visitor.environment.stack.popValue()
        if(symbol is Value.Symbol) {
            globalVariables[symbol.value] = value
        }
    }
}

object GlobalLoad : Visitable {
    override val code: Int get() = 53
    override val isExtension: Boolean get() = false
    override val command: String get() = "global.load"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.SYMBOL, "Symbol to get value of")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.ANY
    override val description: String
        get() = "Get a value of a symbol in global scope."

    override fun visit(visitor: Interpreter) {
        val symbol = visitor.environment.stack.popValue()
        if(symbol is Value.Symbol) {
            val push = globalVariables[symbol.value] ?: Value.Null
            visitor.environment.stack.pushValue(push)
        }
    }
}

object EntityStore : Visitable {
    override val code: Int get() = 54
    override val isExtension: Boolean get() = false
    override val command: String get() = "entity.store"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.SYMBOL, "Symbol to set value of")
            .addSingleArgument(ArgumentType.ANY, "Value to set")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Set a value of a symbol to the player."

    override fun visit(visitor: Interpreter) {
        val value = visitor.environment.stack.popValue()
        val symbol = visitor.environment.stack.popValue()
        if(symbol is Value.Symbol) {
            var name = symbol.value
            if(name.startsWith("nbt:")) {
                name = name.removePrefix("nbt:")
            }
            for(target in visitor.environment.targets) {
                when(value) {
                    is Value.Number -> target.setTag(Tag.Double(name), value.value)
                    is Value.String -> target.setTag(Tag.String(name), value.value)
                    is Value.Bool -> target.setTag(Tag.Boolean(name), value.value)
                    else -> target.setTag(Tag.Double(name), 0.0)
                }

            }
        }
    }
}

object EntityLoad : Visitable {
    override val code: Int get() = 55
    override val isExtension: Boolean get() = false
    override val command: String get() = "entity.load"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.SYMBOL, "Symbol to set value of")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.ANY
    override val description: String
        get() = "Get a value of a symbol to the player."

    override fun visit(visitor: Interpreter) {
        val symbol = visitor.environment.stack.popValue()
        if(symbol is Value.Symbol) {
            var name = symbol.value
            if(name.startsWith("nbt:")) {
                name = name.removePrefix("nbt:")
            }
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
                visitor.environment.stack.pushValue(Value.Null)
            }
        }
    }
}