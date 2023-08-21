package server

import ir.Argument
import parser.findOpcodeInRegistry
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer

class Interpreter(val bytes: ByteBuffer) {
    val blockMap: MutableMap<Int, ByteBuffer> = mutableMapOf()
    val instructions: MutableMap<Short, () -> Unit> = mutableMapOf()
    val registers: MutableList<Value> = MutableList(255) { return@MutableList Value.Null() }
    val constants: MutableMap<Int, Value> = mutableMapOf()
    private val opcodes: List<(ByteBuffer) -> Unit> = listOf(::mov, ::add)

    fun interpretEvent(id: Int) {
        for(pair in blockMap) {
            val k = pair.key
            val v = pair.value
            v.position(0)
            val eventId = v.getInt()
            if(eventId == id && k != -2122219135) {
                v.position(0)
                interpretBlock(k)
            }
        }
    }

    private fun interpretBlock(id: Int) {
        val block = blockMap[id]!!
        val eventId = block.getInt()
        while(true) {
            if(!block.hasRemaining()) break
            interpretOpcode(block)
        }
    }
    private fun interpretOpcode(buf: ByteBuffer) {
        val byte = buf.get().toInt()
        if(byte != 255) {
            println("accessing opcode $byte")
            val func = opcodes[byte]
            func(buf)
        }
    }

    fun printBlockMap() {
        for(pair in blockMap) {
            println("${pair.key} => ${pair.value.array().toList()}")
        }
    }

    fun addExtensionInstruction(id: Short, callback: () -> Unit) {

    }
}