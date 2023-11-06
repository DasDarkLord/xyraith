package instructions.primitives

import runtime.Interpreter
import globalVariables
import typechecker.ArgumentList
import typechecker.ArgumentType
import typechecker.NodeBuilder
import runtime.Value

object FLocalStore : instructions.Visitable {
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
    override val pure: Boolean
        get() = false

    override suspend fun visit(visitor: Interpreter) {
        val value = visitor.environment.stack.popValue()
        val symbol = visitor.environment.stack.popValue()
        if(symbol is Value.Symbol) {
            visitor.environment.localVariables[symbol.value] = value
        }
    }
}

object FLocalLoad : instructions.Visitable {
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
    override val pure: Boolean
        get() = true

    override suspend fun visit(visitor: Interpreter) {
        val symbol = visitor.environment.stack.popValue()
        if(symbol is Value.Symbol) {
            val push = visitor.environment.localVariables[symbol.value] ?: Value.Null
            visitor.environment.stack.pushValue(push)
        }
    }
}

object GlobalStore : instructions.Visitable {
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
    override val pure: Boolean
        get() = false

    override suspend fun visit(visitor: Interpreter) {
        val value = visitor.environment.stack.popValue()
        val symbol = visitor.environment.stack.popValue()
        if(symbol is Value.Symbol) {
            globalVariables[symbol.value] = value
        }
    }
}

object GlobalLoad : instructions.Visitable {
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
    override val pure: Boolean
        get() = true

    override suspend fun visit(visitor: Interpreter) {
        val symbol = visitor.environment.stack.popValue()
        if(symbol is Value.Symbol) {
            val push = globalVariables[symbol.value] ?: Value.Null
            visitor.environment.stack.pushValue(push)
        }
    }
}
