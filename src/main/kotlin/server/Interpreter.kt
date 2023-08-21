package server

import blockMap
import java.nio.ByteBuffer

class Interpreter(val bytes: ByteBuffer) {
    val instructions: MutableMap<Short, (ByteBuffer) -> Unit> = mutableMapOf()
    val registers: MutableList<Value> = MutableList(255) { return@MutableList Value.Null }
    val variables: MutableMap<Value, Value> = mutableMapOf()

    private val opcodes: List<(ByteBuffer) -> Unit> =
        listOf(::mov, ::add, ::consoleLog, ::getCurrentTime, ::store, ::load)

    fun interpretEvent(id: Int) {
        Logger.debug("Interpreter | Interpreting event $id")
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

    private fun interpretBlock(id: Int) {
        Logger.debug("Interpreter | Interpreting block $id")
        val block = blockMap[id]!!.asReadOnlyBuffer()
        val eventId = block.getInt()
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
            val short: Short = (buf.getShort() - 128).toShort()
            Logger.debug("Interpreter | Accessing subcode $short")
            val func = instructions[short]
            if(func != null) {
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
}