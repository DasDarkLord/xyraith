package instructions.primitives

import StdBuiltins
import instructions.Visitable
import runtime.Value
import runtime.getXyraithObject
import typechecker.ArgumentType
import typechecker.NodeBuilder
import java.lang.reflect.Field
import java.lang.reflect.Method

val fields: MutableList<Method> = mutableListOf(
    StdBuiltins().javaClass.getMethod("fs_readFile", java.lang.String::class.java)
)

val reflectionInstructions: MutableList<Visitable> = mutableListOf(
    Visitable(
        10,
        "reflection.method",
        NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "Field index to call")
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
            val method = fields[visitor.environment.passedValues[0]!!.castToNumber().toInt()]
            val output = method.invoke(null, *arguments.toTypedArray())
            return@Visitable output.getXyraithObject()
        },
        false
    )
)