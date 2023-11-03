package instructions.io

import code.Interpreter
import instructions.Visitable
import typechecker.ArgumentList
import typechecker.ArgumentType
import typechecker.NodeBuilder
import parser.Value
import java.io.File

object ReadFileText : instructions.Visitable {
    override val code: Int get() = -2
    override val isExtension: Boolean get() = false
    override val command: String get() = "io.readFile"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.STRING, "File path to read from")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.STRING
    override val description: String
        get() = "Read text from a file. Kotlin exceptions are not handled."

    override suspend fun visit(visitor: Interpreter) {
        visitor.environment.stack.pushValue(
            Value.String(File(visitor.environment.stack.popValue().toDisplay()).readText())
        )
    }
}

object ReadFileBytes : instructions.Visitable {
    override val code: Int get() = -3
    override val isExtension: Boolean get() = false
    override val command: String get() = "io.readFileBytes"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.STRING, "File path to read from")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NUMBER_LIST
    override val description: String
        get() = "Read bytes from a file. Kotlin exceptions are not handled."

    override suspend fun visit(visitor: Interpreter) {
        val bytes = File(visitor.environment.stack.popValue().toDisplay()).readBytes()
        visitor.environment.stack.pushValue(
            Value.NumberList(bytes.toList().map { Value.Number(it.toDouble()) })
        )
    }
}
