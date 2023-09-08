package code.instructions

import code.Visitable
import code.Visitor

object Log : Visitable {
    override val code: Int
        get() = 2
    override val isExtension: Boolean
        get() = false

    override fun visit(visitor: Visitor) {
        println(visitor.environment.stack.removeLast().toDisplay())
    }

}
