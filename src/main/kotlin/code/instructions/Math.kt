package code.instructions

import code.Interpreter
import code.Visitable
import parser.ArgumentList
import parser.ArgumentType
import parser.NodeBuilder
import parser.Value

object Add : Visitable {
    override val code: Int get() = 3
    override val isExtension: Boolean get() = false
    override val command: String get() = "add"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addPluralArgument(ArgumentType.NUMBER, "Numbers to add")
            .build()
    override val description: String
        get() = "Sum a series of numbers."

    override fun visit(visitor: Interpreter) {
        var md = 1.0
        for(x in 1..visitor.environment.argumentCount) {
            md += visitor.environment.stack.removeLast().castToNumber()
        }
        visitor.environment.stack.add(Value.Number(md))
    }
}

object Sub : Visitable {
    override val code: Int get() = 4
    override val isExtension: Boolean get() = false
    override val command: String get() = "sub"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "Left hand side")
            .addSingleArgument(ArgumentType.NUMBER, "Right hand side")
            .build()
    override val description: String
        get() = "Subtract two numbers from eachother."

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
    override val code: Int get() = 5
    override val isExtension: Boolean get() = false
    override val command: String get() = "mul"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addPluralArgument(ArgumentType.NUMBER, "Numbers to multiply")
            .build()
    override val description: String
        get() = "Multiply a series of numbers"

    override fun visit(visitor: Interpreter) {
        var md = 1.0
        for(x in 1..visitor.environment.argumentCount) {
            md *= visitor.environment.stack.removeLast().castToNumber()
        }
        visitor.environment.stack.add(Value.Number(md))
    }
}

object Div : Visitable {
    override val code: Int get() = 6
    override val isExtension: Boolean get() = false
    override val command: String get() = "div"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "Dividend")
            .addSingleArgument(ArgumentType.NUMBER, "Divisor")
            .build()
    override val description: String
        get() = "Divide two numbers"

    override fun visit(visitor: Interpreter) {
        val rhs = visitor.environment.stack.removeLast()
        val lhs = visitor.environment.stack.removeLast()

        if (lhs is Value.Number && rhs is Value.Number) {
            visitor.environment.stack.add(Value.Number(lhs.value / rhs.value))
        } else {
            visitor.environment.stack.add(Value.Number(0.0))
        }
    }
}

object Mod : Visitable {
    override val code: Int get() = 6
    override val isExtension: Boolean get() = false
    override val command: String get() = "mod"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "Number to get modulo of")
            .addSingleArgument(ArgumentType.NUMBER, "Number to modulo by")
            .build()
    override val description: String
        get() = "Get the modulo of two numbers."

    override fun visit(visitor: Interpreter) {
        val lhs = visitor.environment.stack.removeLast()
        val rhs = visitor.environment.stack.removeLast()
        if(lhs is Value.Number && rhs is Value.Number) {
            visitor.environment.stack.add(Value.Number(lhs.value % rhs.value))
        } else {
            visitor.environment.stack.add(Value.Number(0.0))
        }
    }
}