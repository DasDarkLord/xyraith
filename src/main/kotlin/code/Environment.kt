package code

import net.minestom.server.entity.Entity
import net.minestom.server.instance.Instance
import parser.Value

data class Environment(
    val localVariables: Map<String, Value> = mutableMapOf(),
    val stack: MutableList<Value> = mutableListOf(),
    var targets: MutableList<Entity> = mutableListOf(),
    var instance: Instance? = null,
    var argumentCount: Byte = 0,
)