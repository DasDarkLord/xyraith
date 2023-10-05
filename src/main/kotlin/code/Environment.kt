package code

import code.stackframe.ListFrames
import code.stackframe.MapFrames
import net.minestom.server.entity.Entity
import net.minestom.server.event.Event
import net.minestom.server.instance.Instance
import parser.Value

data class Environment(
    val localVariables: MapFrames<String, Value> = MapFrames(),
    val stack: ListFrames<Value> = ListFrames(),
    val functionParameters: ListFrames<Value> = ListFrames(),

    var endBlock: Boolean = false,
    var returnValue: Value = Value.Null,

    var targets: MutableList<Entity> = mutableListOf(),
    var instance: Instance? = null,
    var event: Event? = null,
    var argumentCount: Byte = 0,
)