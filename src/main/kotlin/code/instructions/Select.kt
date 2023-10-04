package code.instructions

import code.Interpreter
import code.Visitable
import net.minestom.server.entity.Player
import parser.ArgumentList
import parser.ArgumentType
import parser.NodeBuilder
import java.lang.IllegalArgumentException
import java.util.*

// TODO: finish command and make it stable by supporting usernames
object Select : Visitable {
    override val code: Int get() = 3000
    override val isExtension: Boolean get() = true
    override val command: String get() = "select"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.SELECTOR, "Player UUID to target")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Set a block at a location."

    override fun visit(visitor: Interpreter) {
        val selector = visitor.environment.stack.removeLast().castToString()

    }
}