package instructions.primitives

import StdBuiltins
import instructions.Visitable
import runtime.Value
import runtime.getXyraithObject
import typechecker.ArgumentType
import typechecker.NodeBuilder
import java.lang.reflect.Field
import java.lang.reflect.Method

fun getStdBuiltin(name: String, vararg parameters: Class<*>): Method {
    return StdBuiltins().javaClass.getMethod(name, *parameters)
}

val fields: MutableMap<String, Method> = mutableMapOf(
    "fs::readFile" to getStdBuiltin("fs_readFile", java.lang.String::class.java),
    "fs::writeFile" to getStdBuiltin("fs_writeFile", java.lang.String::class.java, java.lang.String::class.java),
    "string::trim" to getStdBuiltin("string_trim", java.lang.String::class.java),
    "string::replace" to getStdBuiltin("string_replace", java.lang.String::class.java, java.lang.String::class.java, java.lang.String::class.java),
    "string::length" to getStdBuiltin("string_length", java.lang.String::class.java),
    "console::log" to getStdBuiltin("console_log", java.lang.String::class.java),
)

val reflectionInstructions: MutableList<Visitable> = mutableListOf(
    Visitable(
        10,
        "jvm_method",
        NodeBuilder()
            .addSingleArgument(ArgumentType.STRING, "Method to invoke")
            .addOptionalPluralArgument(ArgumentType.ANY, "Arguments to call with")
            .build(),
        "@internal Call a method based off an index.",
        ArgumentType.ANY,
        { visitor ->
            val arguments = mutableListOf<Any?>()
            var index = 0
            for(argument in visitor.environment.passedValues.getFrame()) {
                index++
                if(index == 1) continue
                arguments.add(argument.getJavaObject())
            }
            val h = visitor.environment.passedValues[0]!!.toDisplay()
            if(!fields.containsKey(h)) {
                return@Visitable Value.Null
            }
            val method = fields[h]!!
            val output = method.invoke(null, *arguments.toTypedArray())
            return@Visitable output.getXyraithObject()
        },
        false
    )
)