package instructions.primitives

import runtime.Interpreter
import runtime.Value
import typechecker.ArgumentList
import typechecker.ArgumentType
import typechecker.NodeBuilder


object StructField : instructions.Visitable {
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
        get() = ArgumentType.NONE
    override val description: String
        get() = "Define a field of a struct"
    override val pure: Boolean
        get() = false

    override suspend fun visit(visitor: Interpreter) {
        val defaultValue = visitor.environment.stack.popValue()
        val typeName = visitor.environment.stack.popValue().castToString()
        val fieldName = visitor.environment.stack.popValue().castToString()
        if(typeName.startsWith("list")) {
            TODO()
        }
        visitor.environment.stack.pushValue(
            Value.StructField(
            type = ArgumentType(typeName, listOf()),
            name = fieldName,
            value = defaultValue,
        ))
    }
}

object StructInit : instructions.Visitable {
    override val code: Int get() = 61
    override val isExtension: Boolean get() = false
    override val command: String get() = "struct.init"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.SYMBOL, "Struct to initialize")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.ANY
    override val description: String
        get() = "Initialize a struct"
    override val pure: Boolean
        get() = false

    override suspend fun visit(visitor: Interpreter) {
        val typeName = visitor.environment.stack.popValue()
        val currentStackSize = visitor.environment.stack.frameSize()+1
        if(typeName is Value.Symbol) {
            visitor.runFunction(":__struct_init_" + typeName.value)
            val fields = mutableMapOf<String, Value>()
            val finalStackSize = visitor.environment.stack.frameSize()
            for(times in currentStackSize..finalStackSize) {
                val field = visitor.environment.stack.popValue()
                if(field is Value.StructField) {
                    fields[field.name] = field.value
                }
            }
            visitor.environment.stack.pushValue(
                Value.Struct(
                type = ArgumentType(typeName.value, listOf()),
                fields = fields
            ))
        } else {
            println("Error typename isn't symbl: $typeName")
        }

    }
}

object StructGet : instructions.Visitable {
    override val code: Int get() = 62
    override val isExtension: Boolean get() = false
    override val command: String get() = "struct.get"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.ANY, "Struct to get field from")
            .addSingleArgument(ArgumentType.SYMBOL, "Field to get from a struct")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.ANY
    override val description: String
        get() = "Get a value from a field of a struct"
    override val pure: Boolean
        get() = true

    override suspend fun visit(visitor: Interpreter) {
        val field = visitor.environment.stack.popValue() as Value.Symbol
        val struct = visitor.environment.stack.popValue() as Value.Struct
        println("struct: $struct | field: $field")
        visitor.environment.stack.pushValue(struct.fields[field.value]!!)
    }
}

object StructSet : instructions.Visitable {
    override val code: Int get() = 63
    override val isExtension: Boolean get() = false
    override val command: String get() = "struct.set"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.ANY, "Struct to set field on")
            .addSingleArgument(ArgumentType.SYMBOL, "Field to set on a struct")
            .addSingleArgument(ArgumentType.ANY, "Value to set")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.ANY
    override val description: String
        get() = "Set a value of a field of a struct"
    override val pure: Boolean
        get() = false

    override suspend fun visit(visitor: Interpreter) {
        val field = visitor.environment.stack.popValue()
        val value = visitor.environment.stack.popValue()
        val struct = visitor.environment.stack.popValue()
        println("""
value: $value
field: $field
struct: $struct
        """.trimIndent())
        if(struct is Value.Struct && field is Value.Symbol) {
            struct.fields[field.value] = value
        }
        visitor.environment.stack.pushValue(struct)
    }
}