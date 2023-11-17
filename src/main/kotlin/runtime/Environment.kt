package runtime

import runtime.stackframe.ListFrames
import runtime.stackframe.MapFrames
import net.minestom.server.entity.Entity
import net.minestom.server.event.Event
import net.minestom.server.instance.Instance

data class Environment(
    val localVariables: MapFrames<String, Value> = MapFrames(),
    val registers: MapFrames<Int, Value> = MapFrames(),
    val passedValues: ListFrames<Value> = ListFrames(),
    val structFieldStack: ListFrames<Value> = ListFrames(),
    val functionParameters: ListFrames<Value> = ListFrames(),

    var endBlock: Boolean = false,
    var returnValue: Value = Value.Null,

    var eventTargets: MutableList<Entity> = mutableListOf(),
    var targets: MutableList<Entity> = mutableListOf(),
    var instance: Instance? = null,
    var event: Event? = null,
    var argumentCount: Byte = 0,

    )