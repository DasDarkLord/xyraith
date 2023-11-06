package instructions.io

import runtime.Interpreter
import runtime.Value
import typechecker.ArgumentList
import typechecker.ArgumentType
import typechecker.NodeBuilder
import java.lang.reflect.Method
import java.lang.reflect.Modifier

object JavaClass : instructions.Visitable {
    override val code: Int get() = 71
    override val isExtension: Boolean get() = false
    override val command: String get() = "javaClass"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.STRING, "The path to the class")
            .build()
    override val description: String
        get() = "Gets a class in Java as a Struct"
    override val returnType: ArgumentType
        get() = ArgumentType.ANY
    override val pure: Boolean
        get() = true

    override suspend fun visit(visitor: Interpreter) {
        val clazz = Class.forName(visitor.environment.stack.popValue().castToString())

        val fields = mutableMapOf<String, Value>()
        for (field in clazz.declaredFields) {
            if (!Modifier.isStatic(field.modifiers)) continue
            if (!Modifier.isPublic(field.modifiers)) continue
            val value = field.get(null)
            fields[field.name] = when (field.type) {
                String::class.java -> Value.String(value as String)
                Double::class.java, Float::class.java, Long::class.java, Int::class.java, Short::class.java, Byte::class.java -> Value.Number((value as Number).toDouble())
                Boolean::class.java -> Value.Bool(value as Boolean)
                else -> Value.Null
            }
        }
        fields["java_path"] = Value.String(clazz.name)

        val clazzStruct = Value.Struct(ArgumentType(":javaClass", emptyList()), fields)
        visitor.environment.stack.pushValue(clazzStruct)
    }

}

object GetMethod : instructions.Visitable {
    override val code: Int
        get() = 72
    override val isExtension: Boolean
        get() = false
    override val command: String
        get() = "getMethod"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType(":javaClass", emptyList()), "Class to get method of (static)")
            .addSingleArgument(ArgumentType.STRING, "The signature of the method")
            .build()
    override val description: String
        get() = "Gets a (static) method of a java class"
    override val returnType: ArgumentType
        get() = ArgumentType.ANY
    override val pure: Boolean
        get() = true

    override suspend fun visit(visitor: Interpreter) {
        val targetMethod = visitor.environment.stack.popValue()
        val classStruct = visitor.environment.stack.popValue() as Value.Struct
        val javaClass = Class.forName(classStruct.fields["java_path"]!!.castToString())
        for (method in javaClass.declaredMethods) {
            println("method: ${getMethodDescriptor(method)}")
            if (getMethodDescriptor(method) == targetMethod.castToString()) {
                val parameterTypesField = mutableListOf<Value>()
                for (type in method.parameterTypes) parameterTypesField.add(Value.String(type.descriptorString()))

                val parameterTypesFieldValue = Value.GenericList(parameterTypesField)

                val fields = mutableMapOf(
                    "class_path" to classStruct.fields["java_path"]!!,
                    "method_desc" to Value.String(getMethodDescriptor(method)),
                    "types" to parameterTypesFieldValue,
                    "return_type" to Value.String(method.returnType.descriptorString()),
                    "name" to Value.String(method.name)
                )

                val methodStruct = Value.Struct(ArgumentType(":javaMethod", emptyList()), fields)
                visitor.environment.stack.pushValue(methodStruct)

                return
            }
        }

        visitor.environment.stack.pushValue(Value.Null)
    }

}

object InvokeMethod : instructions.Visitable {
    override val code: Int get() = 73
    override val isExtension: Boolean get() = false
    override val command: String
        get() = "invokeMethod"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType(":javaMethod", emptyList()), "Method to invoke")
            .addOptionalPluralArgument(ArgumentType.ANY, "Arguments")
            .build()
    override val description: String
        get() = "Invokes a method"
    override val returnType: ArgumentType
        get() = ArgumentType.ANY
    override val pure: Boolean
        get() = false
    override suspend fun visit(visitor: Interpreter) {
        val arguments = mutableListOf<Value>()
        for (i in 2..visitor.environment.argumentCount) {
            arguments.add(visitor.environment.stack.popValue())
        }
        arguments.reverse()

        val javaMethodStruct = visitor.environment.stack.popValue() as Value.Struct
        val javaArguments = mutableListOf<Any?>()

        val javaClass = Class.forName(javaMethodStruct.fields["class_path"]!!.castToString())
        var method: Method? = null
        val methodDescriptor = javaMethodStruct.fields["method_desc"]!!.castToString()
        for (m in javaClass.declaredMethods) {
            if (getMethodDescriptor(m) == methodDescriptor) {
                method = m
                break
            }
        }
        if (method == null) return

        for ((index, arg) in arguments.withIndex()) {
            val expected = descriptorTypeToClass((javaMethodStruct.fields["types"]!! as Value.GenericList).value[index].castToString())
            javaArguments.add(when {
                expected == Byte::class.java -> arg.castToNumber().toInt().toByte()
                expected == Char::class.java -> arg.castToNumber().toInt().toChar()
                expected == Double::class.java -> arg.castToNumber()
                expected == Float::class.java -> arg.castToNumber().toFloat()
                expected == Int::class.java -> arg.castToNumber().toInt()
                expected == Long::class.java -> arg.castToNumber().toLong()
                expected == Short::class.java -> arg.castToNumber().toInt().toShort()
                expected == Boolean::class.java -> arg.castToBoolean()
                expected == Void::class.java -> null
                expected == String::class.java -> arg.castToString()
                else -> null
            })
        }

        val returned: Any? = if (javaArguments.isNotEmpty()) method.invoke(null, *javaArguments.toTypedArray())
        else method.invoke(null)
        if (returned == null) visitor.environment.stack.pushValue(Value.Null)
        else when {
            returned.javaClass == String::class.java -> visitor.environment.stack.pushValue(Value.String(returned as String))
            Number::class.java.isInstance(returned) -> visitor.environment.stack.pushValue(Value.Number((returned as Number).toDouble()))
            returned.javaClass == Boolean::class.java -> visitor.environment.stack.pushValue(Value.Bool(returned as Boolean))
        }
    }

}

fun descriptorTypeToClass(typeStr: String): Class<*> {
    var type = when (typeStr) {
        "B" -> Byte::class.java
        "C" -> Char::class.java
        "D" -> Double::class.java
        "F" -> Float::class.java
        "I" -> Int::class.java
        "J" -> Long::class.java
        "S" -> Short::class.java
        "Z" -> Boolean::class.java
        "V" -> Void.TYPE
        else -> null
    }
    if (type == null) {
        if (typeStr.startsWith("L")) {
            type = Class.forName(typeStr
                .replace(Regex("^L"), "")
                .replace(";", "")
                .replace("/", "."))
        }
    }

    return type!!
}

fun getMethodDescriptor(method: Method): String {
    val descriptor = StringBuilder(method.name).append("(")
    for (parameter in method.parameterTypes) {
        descriptor.append(parameter.descriptorString())
    }
    descriptor.append(")").append(method.returnType.descriptorString())
    return descriptor.toString()
}