package code.instructions

import code.Interpreter
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
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
        var amount = 1.0
        if(visitor.environment.argumentCount >= 2) {
            amount = visitor.environment.stack.popValue().castToNumber()
        }
        val id = visitor.environment.stack.popValue().castToString()
        visitor.environment.stack.pushValue(Value.Item(
            ItemStack.of(Material.fromNamespaceId(id) ?: Material.AIR, amount.toInt())))
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

object StringList : Visitable {
    override val code: Int get() = 15
    override val isExtension: Boolean get() = false
    override val command: String get() = "listOf<string>"
    override val returnType: ArgumentType
        get() = ArgumentType.STRING_LIST
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addPluralArgument(ArgumentType.STRING, "Strings to put in list")
            .build()

    override val description: String
        get() = "Generate a list of strings."

    override suspend fun visit(visitor: Interpreter) {
        val size = visitor.environment.argumentCount
        val output = mutableListOf<Value.String>()
        for(x in 1..size) {
            val addon = visitor.environment.stack.popValue() as Value.String
            output.add(addon)
        }
        visitor.environment.stack.pushValue(Value.StringList(output.reversed()))
    }
}

object NumberList : Visitable {
    override val code: Int get() = 16
    override val isExtension: Boolean get() = false
    override val command: String get() = "listOf<number>"
    override val returnType: ArgumentType
        get() = ArgumentType.NUMBER_LIST
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addPluralArgument(ArgumentType.NUMBER, "Numbers to put in list")
            .build()

    override val description: String
        get() = "Generate a list of numbers."

    override suspend fun visit(visitor: Interpreter) {
        val size = visitor.environment.argumentCount
        val output = mutableListOf<Value.Number>()
        for(x in 1..size) {
            val addon = visitor.environment.stack.popValue() as Value.Number
            output.add(addon)
        }
        visitor.environment.stack.pushValue(Value.NumberList(output.reversed()))
    }
}