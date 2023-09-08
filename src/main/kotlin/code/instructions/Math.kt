package code.instructions

import code.Interpreter
import code.Visitable
import parser.Value

object Add : Visitable {
    override val code: Int
        get() = 3
    override val isExtension: Boolean
        get() = false
    override val command: String
        get() = "add"
    override val arguments: List<String>
        get() = listOf("number", "number")

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

object Sub : Visitable {
    override val code: Int
        get() = 4
    override val isExtension: Boolean
        get() = false
    override val command: String
        get() = "sub"
    override val arguments: List<String>
        get() = listOf("number", "number")

    override fun visit(visitor: Interpreter) {
        val lhs = visitor.environment.stack.removeLast()
        val rhs = visitor.environment.stack.removeLast()
        if(lhs is Value.Number && rhs is Value.Number) {
            visitor.environment.stack.add(Value.Number(lhs.value - rhs.value))
        } else {
            visitor.environment.stack.add(Value.Number(0.0))
        }
    }
}

object Mul : Visitable {
    override val code: Int
        get() = 5
    override val isExtension: Boolean
        get() = false
    override val command: String
        get() = "mul"
    override val arguments: List<String>
        get() = listOf("number", "number")

    override fun visit(visitor: Interpreter) {
        val lhs = visitor.environment.stack.removeLast()
        val rhs = visitor.environment.stack.removeLast()
        if(lhs is Value.Number && rhs is Value.Number) {
            visitor.environment.stack.add(Value.Number(lhs.value * rhs.value))
        } else {
            visitor.environment.stack.add(Value.Number(0.0))
        }
    }
}

object Div : Visitable {
    override val code: Int
        get() = 6
    override val isExtension: Boolean
        get() = false
    override val command: String
        get() = "div"
    override val arguments: List<String>
        get() = listOf("number", "number")

    override fun visit(visitor: Interpreter) {
        val lhs = visitor.environment.stack.removeLast()
        val rhs = visitor.environment.stack.removeLast()
        if(lhs is Value.Number && rhs is Value.Number) {
            visitor.environment.stack.add(Value.Number(lhs.value / rhs.value))
        } else {
            visitor.environment.stack.add(Value.Number(0.0))
        }
    }
}