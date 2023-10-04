package code.instructions

import code.Interpreter
import code.Visitable
import parser.ArgumentList
import parser.ArgumentType
import parser.NodeBuilder
import parser.Value

object ForEach : Visitable {
    override val code: Int get() = 20
    override val isExtension: Boolean get() = false
    override val command: String get() = "foreach"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.SYMBOL, "Variable to store value in")
            .addSingleArgument(ArgumentType.LIST, "List to loop through")
            .addSingleArgument(ArgumentType.BLOCK, "Code to run on each iteration")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Loop through a series of items in a list."

    override fun visit(visitor: Interpreter) {
        val block = visitor.environment.stack.removeLast()
        val list = visitor.environment.stack.removeLast()
        val symbol = visitor.environment.stack.removeLast()

        if(block is Value.BasicBlockRef && list is Value.Array && symbol is Value.Symbol) {
            for(subValue in list.value) {
                visitor.environment.functionLocalVariables[symbol.value] = subValue
                visitor.runBlock(block.value)
            }
        }
    }
}

object If : Visitable {
    override val code: Int get() = 21
    override val isExtension: Boolean get() = false
    override val command: String get() = "if"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.BOOL, "Condition to check")
            .addSingleArgument(ArgumentType.BLOCK, "Code to run on each iteration")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Run a block if a condition is true"

    override fun visit(visitor: Interpreter) {
        val block = visitor.environment.stack.removeLast()
        val condition = visitor.environment.stack.removeLast()

        if(block is Value.BasicBlockRef && condition is Value.Bool && condition.value) {
            visitor.runBlock(block.value)
        }
    }
}