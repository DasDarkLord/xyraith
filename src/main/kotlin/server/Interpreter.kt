package server

import parser.findOpcodeInRegistry
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer

class Interpreter(val bytes: ByteBuffer) {
    val blockMap: MutableMap<Int, List<Byte>> = mutableMapOf()

    fun interpretBlock(id: Int, instructions: Map<Short, () -> Unit>) {
        val block = blockMap[id]!!

    }


}