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
        get() = "Set a value of a symbol in function-local scope."

    override fun visit(visitor: Interpreter) {
        val value = visitor.environment.stack.removeLast()
        val symbol = visitor.environment.stack.removeLast()
        if(symbol is Value.Symbol) {
            visitor.environment.functionLocalVariables[symbol.value] = value
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
        get() = "Get a value of a symbol in function-local scope."

    override fun visit(visitor: Interpreter) {
        val symbol = visitor.environment.stack.removeLast()
        if(symbol is Value.Symbol) {
            val push = visitor.environment.functionLocalVariables[symbol.value]
            if(push != null) {
                visitor.environment.stack.add(push)
            } else {
                println("symbol ${symbol.value} has a problem uhohohohohohohohohkjsahjhdsdfjkndsfnmfnmds")
                println("vars: ${visitor.environment.functionLocalVariables}")
                // TODO: make error report good
                visitor.environment.stack.add(Value.Number(0.0))
            }
        }
    }
}