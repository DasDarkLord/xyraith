package server

import parser.findOpcodeInRegistry
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer

fun Interpreter.disassemble() {
    for(pair in blockMap) {
        val k = pair.key
        val v = pair.value
        val buffer = ByteBuffer.wrap(v.toByteArray())
        if(k == -2122219135) {
            println("CONSTANTS:")
            while(true) {
                try {
                    val id = buffer.getInt()
                    val ty = buffer.get()
                    if(ty.toInt() == 1) {
                        val d = buffer.getDouble()
                        println("  #$id = $d")
                    }

                } catch(e: BufferUnderflowException) {
                    println()
                    break
                }
            }
        } else {
            val event = buffer.getInt()
            val header = when(event) {
                0 -> "block@$k:"
                1 -> "event::join@$k:"
                else -> "null@$k:"
            }
            println(header)
            while(true) {
                try {
                    val byte = buffer.get()
                    if(byte.toInt() == 0) {
                        val reg = buffer.getShort()
                        val constant = buffer.getInt()
                        println("  mov r$reg, #$constant")
                    } else {
                        val newPair: MutableMap.MutableEntry<String, MutableMap<String, Any>> = findOpcodeInRegistry(byte)!!
                        val regsUsed = newPair.value["registersUsed"]!! as Int
                        val name = newPair.key
                        print("  $name ")
                        for(x in 1..(regsUsed+1)) {
                            val reg = buffer.getShort()
                            print("r$reg, ")
                        }
                        println()
                    }
                } catch(e: BufferUnderflowException) {
                    println()
                    break
                }

            }
        }
    }
}
