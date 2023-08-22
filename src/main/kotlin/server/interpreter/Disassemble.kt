package server.interpreter

import blockMap
import constants
import findOpcodeInRegistry
import server.Value
import java.nio.BufferUnderflowException

fun Interpreter.disassemble() {
    for(pair in blockMap) {
        val k = pair.key
        val v = pair.value.asReadOnlyBuffer()
        val buffer = v
        if(k == -2122219135) {
            println("CONSTANTS:")
            while(true) {
                try {
                    val id = buffer.getInt()
                    val ty = buffer.get()
                    if(ty.toInt() == 1) {
                        val d = buffer.getDouble()
                        constants[id] = Value.Number(d)
                        println("  #$id = $d")
                    }
                    if(ty.toInt() == 2) {
                        var str = ""
                        while(true) {
                            val d = buffer.getChar()
                            if(d == Char.MIN_VALUE) break
                            str = "$str$d"
                        }
                        constants[id] = Value.String(str)
                        println("  #$id = \"$str\"")
                    }
                    if(ty.toInt() == 3) {
                        var str = ""
                        while(true) {
                            val d = buffer.getChar()
                            if(d == Char.MIN_VALUE) break
                            str = "$str$d"
                        }
                        constants[id] = Value.Symbol(str)
                        println("  #$id = :$str")
                    }
                    if(ty.toInt() == 3) {
                        val d = buffer.getInt()
                        constants[id] = Value.BasicBlockRef(d)
                        println("  #$id = {basicBlock#$d}")
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
                        println("byte: $byte")
                        if(byte.toInt() == 127) {
                            val nid = buffer.getShort()
                            println("short: $nid")
                            val newPair2: MutableMap.MutableEntry<String, MutableMap<String, Any>> = findOpcodeInRegistry(
                                nid.toInt()
                            )!!
                            val regsUsed = (newPair2.value["arguments"]!! as List<*>).size
                            val name = newPair2.key
                            print("  $name ")
                            for(x in 1..(regsUsed+1)) {
                                val reg = buffer.getShort()
                                print("r$reg, ")
                            }
                            println()
                            continue
                        } else {
                            val newPair: MutableMap.MutableEntry<String, MutableMap<String, Any>> = findOpcodeInRegistry(
                                byte.toInt()
                            )!!
                            val regsUsed = (newPair.value["arguments"]!! as List<*>).size
                            val name = newPair.key
                            print("  $name ")
                            for(x in 1..(regsUsed+1)) {
                                val reg = buffer.getShort()
                                print("r$reg, ")
                            }
                            println()
                        }

                    }
                } catch(e: BufferUnderflowException) {
                    println()
                    break
                }

            }
        }
    }
}
