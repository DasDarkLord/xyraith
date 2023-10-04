package code.instructions

import code.Interpreter
import code.Visitable
import parser.ArgumentList
import parser.ArgumentType
import parser.NodeBuilder
import parser.Value
import kotlin.random.Random

object Random : Visitable {
    override val code: Int get() = 30
    override val isExtension: Boolean get() = false
    override val command: String get() = "random"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "Minimum number")
            .addSingleArgument(ArgumentType.NUMBER, "Maximum number")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NUMBER
    override val description: String
        get() = "Generate a random number."

    override fun visit(visitor: Interpreter) {
        var max = visitor.environment.stack.removeLast().castToNumber()
        var min = visitor.environment.stack.removeLast().castToNumber()
        if(min > max) {
            val temp = min
            max = min
            min = temp
        }
        val rng = Random.Default.nextDouble(min, max)
        visitor.environment.stack.add(Value.Number(rng))
    }
}

object Range : Visitable {
    override val code: Int get() = 31
    override val isExtension: Boolean get() = false
    override val command: String get() = "range"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "Minimum number")
            .addSingleArgument(ArgumentType.NUMBER, "Maximum number")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.LIST
    override val description: String
        get() = "Generate a series of numbers."

    override fun visit(visitor: Interpreter) {
        val max = visitor.environment.stack.removeLast().castToNumber()
        val min = visitor.environment.stack.removeLast().castToNumber()
        val list = (min.toInt()..max.toInt()).toList().map { Value.Number(it.toDouble()) }
        visitor.environment.stack.add(Value.Array(list))
    }
}

object Add : Visitable {
    override val code: Int get() = 32
    override val isExtension: Boolean get() = false
    override val command: String get() = "add"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addPluralArgument(ArgumentType.NUMBER, "Numbers to add")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NUMBER
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
    override val code: Int get() = 33
    override val isExtension: Boolean get() = false
    override val command: String get() = "sub"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "Left hand side")
            .addSingleArgument(ArgumentType.NUMBER, "Right hand side")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NUMBER
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
    override val code: Int get() = 34
    override val isExtension: Boolean get() = false
    override val command: String get() = "mul"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addPluralArgument(ArgumentType.NUMBER, "Numbers to multiply")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NUMBER
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
    override val code: Int get() = 35
    override val isExtension: Boolean get() = false
    override val command: String get() = "div"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "Dividend")
            .addSingleArgument(ArgumentType.NUMBER, "Divisor")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NUMBER
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
    override val code: Int get() = 36
    override val isExtension: Boolean get() = false
    override val command: String get() = "mod"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "Number to get modulo of")
            .addSingleArgument(ArgumentType.NUMBER, "Number to modulo by")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NUMBER
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


object Perlin : Visitable {
    override val code: Int get() = 37
    override val isExtension: Boolean get() = false
    override val command: String get() = "math.perlin"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.LOCATION, "Perlin location")
            .addSingleArgument(ArgumentType.NUMBER, "Seed")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NUMBER
    override val description: String
        get() = "Generate a random number 0.0-1.0 based on location and seed."

    override fun visit(visitor: Interpreter) {
        val pos = visitor.environment.stack.removeLast().castToPos()
        val seed = visitor.environment.stack.removeLast().castToNumber()
        visitor.environment.stack.add(Value.Number(0.0))
    }
}