package server

import parser.findOpcodeInRegistry
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer

class Interpreter(val bytes: ByteBuffer) {
    val blockMap: MutableMap<Int, List<Byte>> = mutableMapOf()
    fun transform() {
        var buf: MutableList<Byte> = mutableListOf()
        var counter = 0
        var latestBlock = -10
        while(true) {
            if(bytes.remaining() == 0) {
                break
            }
            val it = bytes.get()
            if(it.toInt() == -127) {
                counter++
            } else {
                for(x in 1..counter) {
                    buf.add(-127)
                }
                counter = 0
                buf.add(it)
            }
            if(counter == 20) {
                if(latestBlock != -10) {
                    for(x in 1..20) {
                        buf.removeAt(0)
                    }
                    blockMap[latestBlock] = buf
                }
                buf = mutableListOf()
                latestBlock = bytes.getInt()
            }
        }
    }

    fun interpretBlock(id: Int, instructions: Map<Short, () -> Unit>) {
        val block = blockMap[id]!!

    }

    fun disassemble() {
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
}