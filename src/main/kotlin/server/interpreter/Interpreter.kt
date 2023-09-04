package server.interpreter

import blockMap
import net.minestom.server.entity.Player
import net.minestom.server.instance.InstanceContainer
import server.*
import java.nio.ByteBuffer

class Interpreter(val bytes: ByteBuffer) {
    val instructions: MutableMap<Short, (ByteBuffer) -> Unit> = mutableMapOf()
    val registers: MutableList<Value> = MutableList(255) { return@MutableList Value.Null }
    val variables: MutableMap<Value, Value> = mutableMapOf()

    var player: Player? = null
    var instance: InstanceContainer? = null

    private val opcodes: List<(ByteBuffer) -> Unit> =
        listOf(::mov, ::add, ::consoleLog, ::getCurrentTime, ::store, ::load, ::pos, ::vec, ::call)

    fun interpretEvent(id: Int) {
        // Logger.debug("Interpreter | Interpreting event $id")
        for(pair in blockMap) {
            val k = pair.key
            val v = pair.value.asReadOnlyBuffer()
            v.position(0)
            val eventId = v.getInt()
            if(eventId == id && k != -2122219135) {
                v.position(0)
                interpretBlock(k)
            }
        }
    }

    fun interpretBlock(id: Int) {
        Logger.debug("Interpreter | Interpreting block $id")
        val block = blockMap[id]!!.asReadOnlyBuffer()
        val eventId = block.getInt()
        if(eventId == 6) {
            block.getInt() // skip the constant req
        }
        while(true) {
            if(!block.hasRemaining()) break
            interpretOpcode(block)
        }
    }
    private fun interpretOpcode(buf: ByteBuffer) {
        val byte = buf.get().toInt()
        Logger.debug("Interpreter | Interpreting byte $byte")
        if(byte != 127) {
            Logger.debug("Interpreter | Accessing opcode $byte")
            val func = opcodes[byte]
            func(buf)
        } else {
            Logger.debug("Interpreter | Accessing opcode $byte")
            val short: Short = buf.getShort()
            Logger.debug("Interpreter | Accessing subcode $short")
            val func = instructions[short]
            if(func == null) {
                Logger.error("Encountered an error during interpreting, shortcode $short isn't a valid shortcode. Please contact support on the Xyraith discord with your code.")
                throw IllegalArgumentException()
            } else {
                func(buf)
            }

        }
    }

    fun printBlockMap() {
        for(pair in blockMap) {
            Logger.trace("${pair.key} => ${pair.value.array().toList()}")
        }
    }

    fun addExtensionInstruction(id: Short, callback: (ByteBuffer) -> Unit) {
        instructions[id] = callback
    }

    fun eatRegister(buf: ByteBuffer): Value {
        return registers[buf.getShort().toInt()]
    }
    fun eatRawRegister(buf: ByteBuffer): Int {
        return buf.getShort().toInt()
    }
}