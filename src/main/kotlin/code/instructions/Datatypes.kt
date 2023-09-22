package code.instructions

import code.Interpreter
import code.Visitable
import parser.ArgumentList
import parser.ArgumentType
import parser.NodeBuilder
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

    override val description: String
        get() = "Generate a location from coordinates."

    override fun visit(visitor: Interpreter) {
        var pitch = 0.0
        var yaw = 0.0
        if(visitor.environment.argumentCount >= 5) {
            yaw = visitor.environment.stack.removeLast().castToNumber()
        }
        if(visitor.environment.argumentCount >= 4) {
            pitch = visitor.environment.stack.removeLast().castToNumber()
        }
        val z = visitor.environment.stack.removeLast().castToNumber()
        val y = visitor.environment.stack.removeLast().castToNumber()
        val x = visitor.environment.stack.removeLast().castToNumber()
        visitor.environment.stack.add(Value.Position(x, y, z, pitch, yaw))
    }
}

object Item : Visitable {
    override val code: Int get() = 11
    override val isExtension: Boolean get() = false
    override val command: String get() = "item"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.STRING, "Namespace ID")
            .addOptionalArgument(ArgumentType.NUMBER, Value.Number(1.0), "Amount of item")
            .build()

    override val description: String
        get() = "Generate a location from coordinates."

    override fun visit(visitor: Interpreter) {
        var pitch = 0.0
        var yaw = 0.0
        if(visitor.environment.argumentCount >= 5) {
            yaw = visitor.environment.stack.removeLast().castToNumber()
        }
        if(visitor.environment.argumentCount >= 4) {
            pitch = visitor.environment.stack.removeLast().castToNumber()
        }
        val z = visitor.environment.stack.removeLast().castToNumber()
        val y = visitor.environment.stack.removeLast().castToNumber()
        val x = visitor.environment.stack.removeLast().castToNumber()
        visitor.environment.stack.add(Value.Position(x, y, z, pitch, yaw))
    }
}