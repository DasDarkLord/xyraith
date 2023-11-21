package instructions

import instructions.primitives.fields
import instructions.primitives.reflectionInstructions
import runtime.Value
import typechecker.ArgumentList
import typechecker.ArgumentType
import typechecker.NodeBuilder

fun createCoreVisitables(): MutableList<Visitable> {
    val out = mutableListOf<Visitable>()
    out.addAll(reflectionInstructions)
    println(fields)
    out.add(
        Visitable(
            2,
            "console.log",
            NodeBuilder()
                .addSingleArgument(ArgumentType.ANY, "Value")
                .build(),
            "h",
            ArgumentType.STRING,
            { visitable ->
                val str = visitable.environment.passedValues[0]!!
                println(str.toDisplay())
                return@Visitable str
            },
            false
    ))
    out.add(Visitable(
        3,
        "add",
        NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "Lhs")
            .addSingleArgument(ArgumentType.NUMBER, "Rhs")
            .build(),
        "b",
        ArgumentType.NUMBER,
        { visitor ->
            return@Visitable Value.Number((visitor.environment.passedValues[0] as Value.Number).value
                + (visitor.environment.passedValues[1] as Value.Number).value)
        },
        true
    ))
    out.add(Visitable(
        4,
        "sub",
        NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "Lhs")
            .addSingleArgument(ArgumentType.NUMBER, "Rhs")
            .build(),
        "l",
        ArgumentType.NUMBER,
        { visitor ->
            return@Visitable Value.Number((visitor.environment.passedValues[0] as Value.Number).value
                    - (visitor.environment.passedValues[1] as Value.Number).value)
        },
        true
    ))
    return out
}