package instructions.primitives

import runtime.Interpreter
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import typechecker.ArgumentList
import typechecker.ArgumentType
import typechecker.NodeBuilder
import runtime.Value

object Item : instructions.Visitable {
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
    override val pure: Boolean
        get() = true

    override suspend fun visit(visitor: Interpreter) {
        var amount = 1.0
        if(visitor.environment.argumentCount >= 2) {
            amount = visitor.environment.stack.popValue().castToNumber()
        }
        val id = visitor.environment.stack.popValue().castToString()
        visitor.environment.stack.pushValue(
            Value.Item(
            ItemStack.of(Material.fromNamespaceId(id) ?: Material.AIR, amount.toInt())))
    }
}

object True : instructions.Visitable {
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
    override val pure: Boolean
        get() = true
    override suspend fun visit(visitor: Interpreter) {
        visitor.environment.stack.pushValue(Value.Bool(true))
    }
}

object False : instructions.Visitable {
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
    override val pure: Boolean
        get() = true
    override suspend fun visit(visitor: Interpreter) {
        visitor.environment.stack.pushValue(Value.Bool(false))
    }
}

object StringCmd : instructions.Visitable {
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
    override val pure: Boolean
        get() = true
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

object IsNull : instructions.Visitable {
    override val code: Int get() = 17
    override val isExtension: Boolean get() = false
    override val command: String get() = "isNull"
    override val returnType: ArgumentType
        get() = ArgumentType.BOOL
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.ANY, "Value to compare to null")
            .build()

    override val description: String
        get() = "Returns true if the value is null."
    override val pure: Boolean
        get() = true
    override suspend fun visit(visitor: Interpreter) {
        val value = visitor.environment.stack.popValue()
        visitor.environment.stack.pushValue(Value.Bool(value == Value.Null))
    }
}

object ListCmd : instructions.Visitable {
    override val code: Int get() = 18
    override val isExtension: Boolean get() = false
    override val command: String get() = "list"
    override val returnType: ArgumentType
        get() = ArgumentType.GENERIC_LIST
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addPluralArgument(ArgumentType.ANY, "Values of the list")
            .build()

    override val description: String
        get() = "Makes a list with the given values.\nIf the values are not of the same type, an error will be thrown\nat compile-time."
    override val pure: Boolean
        get() = true
    override suspend fun visit(visitor: Interpreter) {
        val list = mutableListOf<Value>()
        for(x in 1..visitor.environment.argumentCount) {
            list.add(visitor.environment.stack.popValue())
        }
        list.reverse()
        visitor.environment.stack.pushValue(Value.GenericList(list))
    }
}

object StringListCmd : instructions.Visitable {
    override val code: Int get() = 19
    override val isExtension: Boolean get() = false
    override val command: String get() = "stringlist"
    override val returnType: ArgumentType
        get() = ArgumentType.STRING_LIST
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addPluralArgument(ArgumentType.STRING, "Values of the list")
            .build()

    override val description: String
        get() = "Makes a list[string] with the given values.\nIf the values are not a string, an error will be thrown\nat compile-time."
    override val pure: Boolean
        get() = true
    override suspend fun visit(visitor: Interpreter) {
        val list = mutableListOf<Value.String>()
        for(x in 1..visitor.environment.argumentCount) {
            list.add(visitor.environment.stack.popValue() as Value.String)
        }
        list.reverse()
        visitor.environment.stack.pushValue(Value.StringList(list))
    }
}