package code

import constants
import registry.commandRegistry
import java.nio.BufferUnderflowException

object Disassembler {
    fun dissasemble(emitter: Emitter) {
        println("CONSTANTS")
        for(constant in constants) {
            println("${constant.key} => ${constant.value.toDisplay()} (${constant.value.castToArgumentType()})")
        }
        println()
        for(block in emitter.blockMap) {
            try {
                println("bb${block.key}:")
                val iter = block.value.duplicate().flip()
                val empty = iter.get()
                val blockId = iter.getInt()
                val event = iter.get()
                if(event.toInt() == 6) {
                    iter.get()
                    iter.getInt()
                }
                while(true) {
                    val opcode = iter.get()
                    if(opcode.toInt() == 0) return
                    if(opcode.toInt() == 1) {
                        val constant = iter.getInt()
                        val value = constants[constant]
                        println("\tpush ${value?.toDisplay()} (constant $constant)")
                    } else if(opcode.toInt() == 127) {
                        val shortcode = iter.getShort()
                        val argCount = iter.get()
                        val obj = commandRegistry
                            .filter { entry -> entry.value["opcodeExtension"] == shortcode }
                            .map { entry -> entry.value["command"] }
                        println("\tshortcode $obj ($shortcode)")
                    } else {
                        val argCount = iter.get()
                        val obj = commandRegistry
                            .filter { entry -> entry.value["opcode"] == opcode }
                            .map { entry -> entry.value["command"] }
                        println("\topcode $obj ($opcode)")
                    }
                }
            } catch(_: BufferUnderflowException) {}

        }
    }
}