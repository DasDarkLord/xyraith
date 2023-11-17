package instructions

import runtime.Value
import typechecker.ArgumentList
import typechecker.ArgumentType
import typechecker.NodeBuilder

fun createCoreVisitables(): MutableList<Visitable> {
    val out = mutableListOf<Visitable>()
    out.add(
        Visitable(
            2,
            false,
            "console.log",
            NodeBuilder()
                .addSingleArgument(ArgumentType.ANY, "Value")
                .build(),
            "h",
            ArgumentType.STRING,
            { visitable ->
                val str = visitable.environment.stack.popValue().toDisplay()
                println(str)
                return@Visitable Value.String(str)
            },
            false
    ))
    return out
}