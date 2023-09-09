package code

import net.minestom.server.entity.Entity
import parser.Value

data class Environment(
    val localVariables: Map<String, Value> = mutableMapOf(),
    val stack: MutableList<Value> = mutableListOf(),
    var targets: MutableList<Entity> = mutableListOf(),
)