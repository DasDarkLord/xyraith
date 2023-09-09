package code.instructions

import code.Interpreter
import code.Visitable
import net.minestom.server.entity.Player
import parser.ArgumentList
import parser.ArgumentType
import parser.NodeBuilder

object SendMessage : Visitable {
    override val code: Int
        get() = 27
    override val isExtension: Boolean
        get() = false
    override val command: String
        get() = "player.sendMessage"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.STRING)
            .build()

    override fun visit(visitor: Interpreter) {
        println("printing msg")
        val disp = visitor.environment.stack.removeLast().toDisplay()
        println("disp: $disp (targets: ${visitor.environment.targets}")
        for(target in visitor.environment.targets) {
            println("target: ${target.uuid}")
            if(target as? Player != null) {
                println("sending msg")
                target.sendMessage(disp)
            }
        }
    }
}