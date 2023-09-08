package code.instructions

import code.Interpreter
import code.Visitable
import parser.ArgumentList
import parser.ArgumentType
import parser.NodeBuilder
import parser.Value

object Add : Visitable {
    override val code: Int
        get() = 3
    override val isExtension: Boolean
        get() = false
    override val command: String
        get() = "add"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER)
            .addSingleArgument(ArgumentType.NUMBER)
            .build()

    override fun visit(visitor: Interpreter) {
        val lhs = visitor.environment.stack.removeLast()
        val rhs = visitor.environment.stack.removeLast()
        if(lhs is Value.Number && rhs is Value.Number) {
            visitor.environment.stack.add(Value.Number(lhs.value + rhs.value))
        } else {
            visitor.environment.stack.add(Value.Number(0.0))
        }
    }
}