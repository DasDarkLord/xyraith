package code.instructions

import code.Interpreter
import typechecker.ArgumentList
import typechecker.ArgumentType
import typechecker.NodeBuilder
import parser.Value

object Loc : Visitable {
    override val code: Int get() = 10
    override val isExtension: Boolean get() = false
    override val command: String get() = "loc"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "X coordinate")
            .addSingleArgument(ArgumentType.NUMBER, "Y coordinate")
            .addSingleArgument(ArgumentType.NUMBER, "Z coordinate")
            .addOptionalArgument(ArgumentType.NUMBER, Value.Number(0.0), "Pitch coordinate")
            .addOptionalArgument(ArgumentType.NUMBER, Value.Number(0.0),"Yaw coordinate")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.LOCATION

    override val description: String
        get() = "Generate a location from coordinates."

    override suspend fun visit(visitor: Interpreter) {
        var pitch = 0.0
        var yaw = 0.0
        if(visitor.environment.argumentCount >= 5) {
            yaw = visitor.environment.stack.popValue().castToNumber()
        }
        if(visitor.environment.argumentCount >= 4) {
            pitch = visitor.environment.stack.popValue().castToNumber()
        }
        val z = visitor.environment.stack.popValue().castToNumber()
        val y = visitor.environment.stack.popValue().castToNumber()
        val x = visitor.environment.stack.popValue().castToNumber()
        visitor.environment.stack.pushValue(Value.Position(x, y, z, pitch, yaw))
    }
}

object Item : Visitable {
    override val code: Int get() = 11
    override val isExtension: Boolean get() = false
    override val command: String get() = "item"
    override val returnType: ArgumentType
        get() = ArgumentType.ITEM
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.STRING, "Namespace ID")
            .addOptionalArgument(ArgumentType.NUMBER, Value.Number(1.0), "Amount of item")
            .build()

    override val description: String
        get() = "Generate an item from an ID and an amount"

    override suspend fun visit(visitor: Interpreter) {
        TODO()
    }
}

object True : Visitable {
    override val code: Int get() = 12
    override val isExtension: Boolean get() = false
    override val command: String get() = "true"
    override val returnType: ArgumentType
        get() = ArgumentType.BOOL
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .build()

    override val description: String
        get() = "Return a true boolean."

    override suspend fun visit(visitor: Interpreter) {
        visitor.environment.stack.pushValue(Value.Bool(true))
    }
}

object False : Visitable {
    override val code: Int get() = 13
    override val isExtension: Boolean get() = false
    override val command: String get() = "false"
    override val returnType: ArgumentType
        get() = ArgumentType.BOOL
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .build()

    override val description: String
        get() = "Return a false boolean."

    override suspend fun visit(visitor: Interpreter) {
        visitor.environment.stack.pushValue(Value.Bool(false))
    }
}

object StringCmd : Visitable {
    override val code: Int get() = 14
    override val isExtension: Boolean get() = false
    override val command: String get() = "string"
    override val returnType: ArgumentType
        get() = ArgumentType.STRING
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addPluralArgument(ArgumentType.ANY, "Values to concatenate")
            .build()

    override val description: String
        get() = "Return a string with all values concatenated."

    override suspend fun visit(visitor: Interpreter) {
        val size = visitor.environment.argumentCount
        var output = ""
        for(x in 1..size) {
            val addon = visitor.environment.stack.popValue()
            output = addon.toDisplay() + output
        }
        visitor.environment.stack.pushValue(Value.String(output))
    }
}