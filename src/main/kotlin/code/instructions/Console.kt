package code.instructions

import code.Visitable
import code.Interpreter

object Log : Visitable {
    override val code: Int
        get() = 2
    override val isExtension: Boolean
        get() = false
    override val command: String
        get() = "console.log"
    override val arguments: List<String>
        get() = listOf("any")

    override fun visit(visitor: Interpreter) {
        println(visitor.environment.stack.removeLast().toDisplay())
    }
}
