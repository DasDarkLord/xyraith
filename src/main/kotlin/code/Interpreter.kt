package code

import blockMap
import code.instructions.Visitable
import code.instructions.visitables
import constants
import net.minestom.server.entity.Entity
import net.minestom.server.event.Event
import net.minestom.server.instance.Instance
import java.nio.ByteBuffer
import parser.Value

val shortcodes: Map<Int, Visitable> = visitables.filter { obj -> obj.isExtension }.associateBy { obj -> obj.code }
val opcodes: Map<Int, Visitable> = visitables.filter { obj -> !obj.isExtension }.associateBy { obj -> obj.code }

fun peek(buf: ByteBuffer): Byte {
    val out = buf.get()
    buf.position(buf.position()-1)
    return out
}

fun runEvent(eventIdChk: Int, targets: MutableList<Entity> = mutableListOf(), instance: Instance? = null, event: Event? = null) {
    for(pair in blockMap) {
        val block = pair.value.duplicate().position(0)
        val zero = block.get()
        val id = block.getInt()
        val eventId = block.get()
        if(eventId.toInt() == eventIdChk) {
            val interpreter = Interpreter(constants, blockMap)
            interpreter.environment.targets = targets
            interpreter.environment.instance = instance
            interpreter.environment.event = event
            interpreter.runBlock(pair.key)
        }
    }
}

class Interpreter(val constants: Map<Int, Value>, val blockMap: Map<Int, ByteBuffer>) {
    val environment: Environment = Environment()


    fun runBlock(blockId: Int): Value {
        val block = blockMap[blockId]!!.asReadOnlyBuffer().position(0)
        val zero = block.get()
        val id = block.getInt()
        val eventId = block.get()
        if(eventId.toInt() == 6) {
            block.get()
            block.getInt()
        }
        var opcode = peek(block)
        while(opcode.toInt() != 0) {
            runInstruction(block)
            opcode = peek(block)
            if(environment.endBlock) {
                return environment.returnValue
            }
        }
        return Value.Null
    }

    fun runFunction(functionName: String): Value {
        for(block in blockMap) {
            val buf = block.value.asReadOnlyBuffer().position(0)
            buf.get()
            val id = buf.getInt()
            val eventId = buf.get()
            if(eventId.toInt() != 6) {
                continue
            }
            buf.get()
            val name = constants[buf.getInt()]
            if(name is Value.Symbol && name.value == functionName) {
                val value = runBlock(id)
                environment.endBlock = false
                return value
            }
        }
        return Value.Null
    }

    private fun runInstruction(buf: ByteBuffer) {
        val opcode = buf.get()
        if(opcode.toInt() == 1) {
            val id = buf.getInt()
            environment.stack.pushValue(constants[id]!!)
        } else if (opcode.toInt() == 127) {
            val id = buf.getShort()
            val argumentCount = buf.get()
            this.environment.argumentCount = argumentCount
            shortcodes[id.toInt()]!!.visit(this)
        } else {
            val argumentCount = buf.get()
            this.environment.argumentCount = argumentCount
            opcodes[opcode.toInt()]!!.visit(this)
        }
    }
}