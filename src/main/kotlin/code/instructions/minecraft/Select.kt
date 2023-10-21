package code.instructions.minecraft

import code.Interpreter
import code.instructions.Visitable
import net.minestom.server.entity.Player
import typechecker.ArgumentList
import typechecker.ArgumentType
import typechecker.NodeBuilder

// TODO: finish command and make it stable by supporting usernames
object Select : Visitable {
    override val code: Int get() = 4000
    override val isExtension: Boolean get() = true
    override val command: String get() = "select"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.STRING, "Type to target")
            .addPluralArgument(ArgumentType.STRING, "Requirements to target")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Set targets based on selector."

    override suspend fun visit(visitor: Interpreter) {
        val requirements = mutableListOf<String>()
        for(index in 2..visitor.environment.argumentCount) {
            requirements.add(visitor.environment.stack.popValue().castToString())
        }
        val type = visitor.environment.stack.popValue().castToString()
        var selection = visitor.environment.instance!!.entities.toList()
        if(type == "players") {
            selection = selection.filterIsInstance<Player>()
        }
        if(type == "mobs") {
            selection = selection.filter { selection !is Player }
        }
        val regex = Regex("(.*?)=(.*?)")
        for(requirement in requirements) {
            if(requirement.matches(regex)) {
                val matchResult = regex.find(requirement)!!
                val lhs = matchResult.groups[0]!!.value
                val rhs = matchResult.groups[1]!!.value
                when(lhs) {
                    "username" -> selection = selection.filter { it is Player && it.username == rhs }
                    "uuid" -> selection = selection.filter { it.uuid.toString() == rhs }
                }
                println(selection)
            }
        }
        visitor.environment.targets = selection.toMutableList()
    }
}

object ResetSelection : Visitable {
    override val code: Int get() = 4999
    override val isExtension: Boolean get() = true
    override val command: String get() = "resetSelection"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Set targets based on selector."

    override suspend fun visit(visitor: Interpreter) {
       TODO()
    }
}