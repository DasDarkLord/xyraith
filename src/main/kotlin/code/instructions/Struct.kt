package code.instructions

import code.Interpreter
import parser.Value
import typechecker.ArgumentList
import typechecker.ArgumentType
import typechecker.NodeBuilder

object StructField : Visitable {
    override val code: Int get() = 60
    override val isExtension: Boolean get() = false
    override val command: String get() = "struct.field"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.SYMBOL, "Field name")
            .addSingleArgument(ArgumentType.STRING, "Field type")
            .addSingleArgument(ArgumentType.ANY, "Value to initialize with")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.ANY
    override val description: String
        get() = "Define a field of a struct"

    override suspend fun visit(visitor: Interpreter) {
        val defaultValue = visitor.environment.stack.popValue()
        val typeName = visitor.environment.stack.popValue().castToString()
        val fieldName = visitor.environment.stack.popValue().castToString()
        visitor.environment.stack.pushValue(Value.StructField(
            type = ArgumentType(typeName),
            name = fieldName,
            value = defaultValue,
        ))
    }
}

object StructInit : Visitable {
    override val code: Int get() = 61
    override val isExtension: Boolean get() = false
    override val command: String get() = "struct.init"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.SYMBOL, "Struct to initialize")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Initialize a struct"

    override suspend fun visit(visitor: Interpreter) {
        val typeName = visitor.environment.stack.popValue()
        val currentStackSize = visitor.environment.stack.frameSize()+1
        if(typeName is Value.Symbol) {
            visitor.runFunction(typeName.value)
            val fields = mutableMapOf<String, Value>()
            val finalStackSize = visitor.environment.stack.frameSize()
            println("$currentStackSize..$finalStackSize")
            for(times in currentStackSize..finalStackSize) {
                val field = visitor.environment.stack.popValue()
                if(field is Value.StructField) {
                    fields[field.name] = field.value
                }
            }
            visitor.environment.stack.pushValue(Value.Struct(
                type = ArgumentType(typeName.value),
                fields = fields
            ))
        }

    }
}