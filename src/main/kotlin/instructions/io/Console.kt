package instructions.io

import runtime.Interpreter
import typechecker.ArgumentList
import typechecker.ArgumentType
import typechecker.NodeBuilder

object Log : instructions.Visitable {
    override val code: Int get() = 70
    override val isExtension: Boolean get() = false
    override val command: String get() = "console.log"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.ANY, "Value to print")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Log a message to the console."
    override val pure: Boolean
        get() = false

    override suspend fun visit(visitor: Interpreter) {
        println(visitor.environment.stack.popValue().toDisplay())
    }
}
