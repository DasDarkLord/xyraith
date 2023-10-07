package code.instructions

import code.Interpreter
import typechecker.ArgumentList
import typechecker.ArgumentType
import typechecker.NodeBuilder
import parser.Value

object ForEach : Visitable {
    override val code: Int get() = 20
    override val isExtension: Boolean get() = false
    override val command: String get() = "foreach"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.SYMBOL, "Variable to store value in")
            .addSingleArgument(ArgumentType.GENERIC_LIST, "List to loop through")
            .addSingleArgument(ArgumentType.BLOCK, "Code to run on each iteration")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Loop through a series of items in a list."

    override fun visit(visitor: Interpreter) {
        val block = visitor.environment.stack.popValue()
        val list = visitor.environment.stack.popValue()
        val symbol = visitor.environment.stack.popValue()

        if(block is Value.BasicBlockRef && list is Value.NumberList && symbol is Value.Symbol) {
            for(subValue in list.value) {
                visitor.environment.localVariables[symbol.value] = subValue
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
        val block = visitor.environment.stack.popValue()
        val condition = visitor.environment.stack.popValue()

        if(block is Value.BasicBlockRef && condition is Value.Bool && condition.value) {
            visitor.runBlock(block.value)
        }
    }
}

object Call : Visitable {
    override val code: Int get() = 22
    override val isExtension: Boolean get() = false
    override val command: String get() = "call"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.SYMBOL, "Function to call")
            .addPluralArgument(ArgumentType.ANY, "Arguments to pass")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.ANY
    override val description: String
        get() = "Call a function and pass it parameters if wanted. Functions do not share local variables."

    override fun visit(visitor: Interpreter) {
        val arguments = mutableListOf<Value>()
        for(index in 2..visitor.environment.argumentCount) {
            val argument = visitor.environment.stack.popValue()
            arguments.add(argument)
        }
        val symbol = visitor.environment.stack.popValue()
        if(symbol is Value.Symbol) {
            visitor.environment.stack.pushFrame()
            visitor.environment.localVariables.pushFrame()
            visitor.environment.functionParameters.pushFrame(arguments)
            val returnValue = visitor.runFunction(symbol.value)
            visitor.environment.stack.popFrame()
            visitor.environment.localVariables.popFrame()
            visitor.environment.functionParameters.popFrame()
            visitor.environment.stack.pushValue(returnValue)
        }

    }
}

object GetParam : Visitable {
    override val code: Int get() = 23
    override val isExtension: Boolean get() = false
    override val command: String get() = "parameter"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "Index of parameter to get")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.ANY
    override val description: String
        get() = "Get a parameter passed to this function by index"

    override fun visit(visitor: Interpreter) {
        val index = visitor.environment.stack.popValue().castToNumber()
        visitor.environment.stack.pushValue(visitor.environment.functionParameters[index.toInt()] ?: Value.Null)
    }
}

object Return : Visitable {
    override val code: Int get() = 24
    override val isExtension: Boolean get() = false
    override val command: String get() = "return"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addOptionalArgument(ArgumentType.ANY, Value.Null, "Value to return")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.ANY
    override val description: String
        get() = "Get a parameter passed to this function by index"

    override fun visit(visitor: Interpreter) {
        visitor.environment.returnValue = visitor.environment.stack.popValue()
        visitor.environment.endBlock = true
    }
}