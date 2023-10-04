package code

import net.minestom.server.entity.Entity
import net.minestom.server.event.Event
import net.minestom.server.instance.Instance
import parser.Value

data class Environment(
    val localVariables: MutableMap<String, Value> = mutableMapOf(),
    var functionLocalVariables: MutableMap<String, Value> = mutableMapOf(),
    var functionLocalVariablesStack: MutableList<Map<String, Value>> = mutableListOf(),
    val stack: MutableList<Value> = mutableListOf(),
    var targets: MutableList<Entity> = mutableListOf(),
    var instance: Instance? = null,
    var event: Event? = null,
    var argumentCount: Byte = 0,
)