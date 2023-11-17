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
                .addSingleArgument(ArgumentType.STRING, "Value")
                .build(),
            "h",
            ArgumentType.NONE,
            { visitable ->
                println(visitable.environment.stack.popValue().toDisplay())
                return@Visitable Value.Null
            },
            false
    ))
    return out
}