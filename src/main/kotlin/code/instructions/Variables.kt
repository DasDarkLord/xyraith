package code.instructions

import code.Interpreter
import code.Visitable
import parser.ArgumentList
import parser.ArgumentType
import parser.NodeBuilder
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
            val push = visitor.environment.localVariables[symbol.value]
            if(push != null) {
                visitor.environment.stack.pushValue(push)
            } else {
                println("ERROR: Tried to load from local variable `${symbol.value}` when it doesn't exist.")
                println("Defaulting to loading the number 0.")
                visitor.environment.stack.pushValue(Value.Number(0.0))
            }
        }
    }
}