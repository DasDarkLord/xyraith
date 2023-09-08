package server.interpreter

import blockMap
import constants
import registry.findOpcodeInRegistry
import functions
import parser.Value
import java.nio.BufferUnderflowException

fun Interpreter.disassemble() {
    for(pair in blockMap) {
        val k = pair.key
        val v = pair.value.asReadOnlyBuffer()
        val buffer = v
        val registerState = MutableList<Value>(255) { Value.Null() }
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
                    if(ty.toInt() == 5) {
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
                6 -> {
                    val constant = buffer.getInt()
                    functions[constants[constant]!!] = k
                    "function{$constant}"
                }
                else -> "unknown@$k:"
            }
            println(header)
            while(true) {
                try {
                    val byte = buffer.get()
                    if(byte.toInt() == 0) {
                        val reg = buffer.getShort()
                        val constant = buffer.getInt()
                        println("  mov r$reg, ${constants[constant]!!.toDisplay()}")
                        if(reg >= 0) {
                            registerState[reg.toInt()] = constants[constant]!!
                        }
                    } else {
                        if(byte.toInt() == 127) {
                            val nid = buffer.getShort()
                            val newPair2: MutableMap.MutableEntry<String, MutableMap<String, Any>> = findOpcodeInRegistry(
                                nid.toInt(), true
                            )!!
                            val regsUsed = (newPair2.value["arguments"]!! as List<*>).size
                            val name = newPair2.key
                            print("  $name ")
                            for(x in 1..(regsUsed+1)) {
                                val reg = buffer.getShort()
                                if(reg >= 0) {
                                    print("r$reg (${registerState[reg.toInt()]}), ")
                                } else {
                                    print("r$reg (no answer), ")
                                }
                            }
                            println()
                            continue
                        } else {
                            val newPair: MutableMap.MutableEntry<String, MutableMap<String, Any>> = findOpcodeInRegistry(
                                byte.toInt(), false
                            )!!
                            val regsUsed = (newPair.value["arguments"]!! as List<*>).size
                            val name = newPair.key
                            print("  $name ")
                            for(x in 1..(regsUsed+1)) {
                                val reg = buffer.getShort()
                                if(reg >= 0) {
                                    print("r$reg (${registerState[reg.toInt()]}), ")
                                } else {
                                    print("r$reg (no answer), ")
                                }

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
