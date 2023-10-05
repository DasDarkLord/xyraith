package code.instructions

import code.Interpreter
import code.Visitable
import parser.ArgumentList
import parser.ArgumentType
import parser.NodeBuilder

object Log : Visitable {
    override val code: Int get() = 126
    override val isExtension: Boolean get() = false
    override val command: String get() = "console.log"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.STRING, "String to print")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Log a message to the console."

    override fun visit(visitor: Interpreter) {
        println(visitor.environment.stack.popValue().toDisplay())
    }
}
