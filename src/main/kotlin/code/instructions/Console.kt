package code.instructions

import code.Interpreter
import code.Visitable
import parser.ArgumentList
import parser.ArgumentType
import parser.NodeBuilder

object Log : Visitable {
    override val code: Int
        get() = 2
    override val isExtension: Boolean
        get() = false
    override val command: String
        get() = "console.log"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.STRING)
            .build()

    override fun visit(visitor: Interpreter) {
        println(visitor.environment.stack.removeLast().toDisplay())
    }
}
