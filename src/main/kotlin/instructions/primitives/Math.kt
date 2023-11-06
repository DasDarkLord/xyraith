package instructions.primitives

import runtime.Interpreter
import typechecker.ArgumentList
import typechecker.ArgumentType
import typechecker.NodeBuilder
import runtime.Value
import kotlin.math.pow
import kotlin.random.Random

object Random : instructions.Visitable {
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
    override val pure: Boolean
        get() = true

    override suspend fun visit(visitor: Interpreter) {
        var max = visitor.environment.stack.popValue().castToNumber()
        var min = visitor.environment.stack.popValue().castToNumber()
        if(min > max) {
            val temp = min
            max = min
            min = temp
        }
        val rng = Random.Default.nextDouble(min, max)
        visitor.environment.stack.pushValue(Value.Number(rng))
    }
}

object Range : instructions.Visitable {
    override val code: Int get() = 31
    override val isExtension: Boolean get() = false
    override val command: String get() = "range"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "Minimum number")
            .addSingleArgument(ArgumentType.NUMBER, "Maximum number")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NUMBER_LIST
    override val description: String
        get() = "Generate a series of numbers."
    override val pure: Boolean
        get() = true

    override suspend fun visit(visitor: Interpreter) {
        val max = visitor.environment.stack.popValue().castToNumber()
        val min = visitor.environment.stack.popValue().castToNumber()
        val list = (min.toInt()..max.toInt()).toList().map { Value.Number(it.toDouble()) }
        visitor.environment.stack.pushValue(Value.NumberList(list))
    }
}

object Add : instructions.Visitable {
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
    override val pure: Boolean
        get() = true

    override suspend fun visit(visitor: Interpreter) {
        var md = 0.0
        for(x in 1..visitor.environment.argumentCount) {
            md += visitor.environment.stack.popValue().castToNumber()
        }
        visitor.environment.stack.pushValue(Value.Number(md))
    }
}

object Sub : instructions.Visitable {
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
    override val pure: Boolean
        get() = true

    override suspend fun visit(visitor: Interpreter) {
        val rhs = visitor.environment.stack.popValue()
        val lhs = visitor.environment.stack.popValue()
        if(lhs is Value.Number && rhs is Value.Number) {
            visitor.environment.stack.pushValue(Value.Number(lhs.value - rhs.value))
        } else {
            visitor.environment.stack.pushValue(Value.Number(0.0))
        }
    }
}

object Mul : instructions.Visitable {
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
    override val pure: Boolean
        get() = true
    override suspend fun visit(visitor: Interpreter) {
        var md = 1.0
        for(x in 1..visitor.environment.argumentCount) {
            md *= visitor.environment.stack.popValue().castToNumber()
        }
        visitor.environment.stack.pushValue(Value.Number(md))
    }
}

object Div : instructions.Visitable {
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
    override val pure: Boolean
        get() = true

    override suspend fun visit(visitor: Interpreter) {
        val rhs = visitor.environment.stack.popValue()
        val lhs = visitor.environment.stack.popValue()

        if (lhs is Value.Number && rhs is Value.Number) {
            visitor.environment.stack.pushValue(Value.Number(lhs.value / rhs.value))
        } else {
            visitor.environment.stack.pushValue(Value.Number(0.0))
        }
    }
}

object Mod : instructions.Visitable {
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
    override val pure: Boolean
        get() = true

    override suspend fun visit(visitor: Interpreter) {
        val lhs = visitor.environment.stack.popValue()
        val rhs = visitor.environment.stack.popValue()
        if(lhs is Value.Number && rhs is Value.Number) {
            visitor.environment.stack.pushValue(Value.Number(lhs.value % rhs.value))
        } else {
            visitor.environment.stack.pushValue(Value.Number(0.0))
        }
    }
}

object Pow : instructions.Visitable {
    override val code: Int get() = 38
    override val isExtension: Boolean get() = false
    override val command: String get() = "pow"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "Base")
            .addSingleArgument(ArgumentType.NUMBER, "Exponent")
            .build()
    override val description: String
        get() = "Raises the given base to the given exponent"
    override val returnType: ArgumentType
        get() = ArgumentType.NUMBER
    override val pure: Boolean
        get() = true

    override suspend fun visit(visitor: Interpreter) {
        val exponent = visitor.environment.stack.popValue().castToNumber()
        val base = visitor.environment.stack.popValue().castToNumber()
        visitor.environment.stack.pushValue(Value.Number(base.pow(exponent)))
    }

}

object Perlin : instructions.Visitable {
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
    override val pure: Boolean
        get() = true

    override suspend fun visit(visitor: Interpreter) {
        val pos = visitor.environment.stack.popValue() as Value.Struct
        val seed = visitor.environment.stack.popValue().castToNumber()
        TODO()
    }
}

object GreaterThan : instructions.Visitable {
    override val code: Int get() = 40
    override val isExtension: Boolean get() = false
    override val command: String get() = ">"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "Left hand side")
            .addSingleArgument(ArgumentType.NUMBER, "Right hand side")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.BOOL
    override val description: String
        get() = "Check if a number is greater than another"
    override val pure: Boolean
        get() = true

    override suspend fun visit(visitor: Interpreter) {
        val rhs = visitor.environment.stack.popValue().castToNumber()
        val lhs = visitor.environment.stack.popValue().castToNumber()
        visitor.environment.stack.pushValue(Value.Bool(lhs > rhs))
    }
}

object GreaterThanOrEqual : instructions.Visitable {
    override val code: Int get() = 41
    override val isExtension: Boolean get() = false
    override val command: String get() = ">="
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "Left hand side")
            .addSingleArgument(ArgumentType.NUMBER, "Right hand side")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.BOOL
    override val description: String
        get() = "Check if a number is greater than or equal to another"
    override val pure: Boolean
        get() = true

    override suspend fun visit(visitor: Interpreter) {
        val rhs = visitor.environment.stack.popValue().castToNumber()
        val lhs = visitor.environment.stack.popValue().castToNumber()
        visitor.environment.stack.pushValue(Value.Bool(lhs >= rhs))
    }
}

object LessThan : instructions.Visitable {
    override val code: Int get() = 42
    override val isExtension: Boolean get() = false
    override val command: String get() = "<"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "Left hand side")
            .addSingleArgument(ArgumentType.NUMBER, "Right hand side")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.BOOL
    override val description: String
        get() = "Check if a number is less than another"
    override val pure: Boolean
        get() = true
    override suspend fun visit(visitor: Interpreter) {
        val rhs = visitor.environment.stack.popValue().castToNumber()
        val lhs = visitor.environment.stack.popValue().castToNumber()
        visitor.environment.stack.pushValue(Value.Bool(lhs < rhs))
    }
}

object LessThanOrEqual : instructions.Visitable {
    override val code: Int get() = 43
    override val isExtension: Boolean get() = false
    override val command: String get() = "<="
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "Left hand side")
            .addSingleArgument(ArgumentType.NUMBER, "Right hand side")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.BOOL
    override val description: String
        get() = "Check if a number is less than or equal to another"
    override val pure: Boolean
        get() = true

    override suspend fun visit(visitor: Interpreter) {
        val rhs = visitor.environment.stack.popValue().castToNumber()
        val lhs = visitor.environment.stack.popValue().castToNumber()
        visitor.environment.stack.pushValue(Value.Bool(lhs <= rhs))
    }
}

object EqualTo : instructions.Visitable {
    override val code: Int get() = 44
    override val isExtension: Boolean get() = false
    override val command: String get() = "=="
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.ANY, "Left hand side")
            .addSingleArgument(ArgumentType.ANY, "Right hand side")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.BOOL
    override val description: String
        get() = "Check if a value is equal to another"
    override val pure: Boolean
        get() = true

    override suspend fun visit(visitor: Interpreter) {
        val rhs = visitor.environment.stack.popValue()
        val lhs = visitor.environment.stack.popValue()
        visitor.environment.stack.pushValue(Value.Bool(lhs == rhs))
    }
}