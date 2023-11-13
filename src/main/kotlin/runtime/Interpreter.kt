package runtime

import blockMap
import instructions.Visitable
import instructions.visitables
import constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import lang.emitter.BUFFER_SIZE
import net.minestom.server.entity.Entity
import net.minestom.server.event.Event
import net.minestom.server.instance.Instance
import java.nio.ByteBuffer
import java.nio.BufferUnderflowException
import kotlin.Exception

val shortcodes: Map<Int, Visitable> =
    visitables.filter { obj -> obj.isExtension }.associateBy { obj -> obj.code }
val opcodes: Map<Int, Visitable> =
    visitables.filter { obj -> !obj.isExtension }.associateBy { obj -> obj.code }

fun peek(buf: ByteBuffer): Byte {
    val out = buf.get()
    buf.position(buf.position()-1)
    return out
}

fun runEvent(eventIdChk: Int, targets: MutableList<Entity> = mutableListOf(), instance: Instance? = null, event: Event? = null) {
    runBlocking {
        for(pair in blockMap) {
            println("Pair: $pair | ")
            val eventId = pair.value.event.id
            if(eventId == eventIdChk) {
                println("Is an OK event!")
                val interpreter = Interpreter(constants, blockMap, this)
                interpreter.environment.eventTargets = targets
                interpreter.environment.targets = targets
                interpreter.environment.instance = instance
                interpreter.environment.event = event
                interpreter.runBlock(pair.key)
            }
        }
    }
}

class InvalidBytecode : Exception()

sealed class InterpreterData {
    data class Event(val id: Int, val constant: Int?) : InterpreterData()
    data class BasicBlock(val code: ByteBuffer, val id: Int, val codeLength: Int, val event: InterpreterData.Event) : InterpreterData()
}

fun parseBytecode(buf: ByteBuffer): Pair<MutableList<InterpreterData.BasicBlock>, Map<Int, Value>> {
    val constants = parseConstants(buf)
    val blocks = mutableListOf<InterpreterData.BasicBlock>()
    try {
        while(true) {
            if(!buf.hasRemaining()) break
            val next = buf.get()
            if(next.toInt() != -127) throw InvalidBytecode()
            var id = -1
            var code: ByteBuffer? = null
            var codeLength: Int = -1
            var event: InterpreterData.Event? = null
            while(true) {
                val fn = buf.get()
                if(fn.toInt() == -120) {
                    codeLength = buf.getInt()
                }
                if(fn.toInt() == -121) {
                    id = buf.getInt()
                }
                if(fn.toInt() == -122) {
                    val eventId = buf.get().toInt()
                    if(eventId == 6) {
                        buf.get()
                        val constant = buf.getInt()
                        event = InterpreterData.Event(eventId, constant)
                    } else {
                        event = InterpreterData.Event(eventId, null)
                    }
                }
                if(fn.toInt() == -123) {
                    val out = ByteBuffer.allocate(BUFFER_SIZE)
                    while(true) {
                        val next2 = buf.get()
                        if(next2.toInt() == -126) {
                            buf.position(buf.position()-1)
                            break
                        }
                        out.put(next2)
                    }
                    code = out
                }
                if(fn.toInt() == -126) {
                    if(id < 0) throw InvalidBytecode()
                    if(code == null) throw InvalidBytecode()
                    if(codeLength < 0) throw InvalidBytecode()
                    if(event == null) throw InvalidBytecode()
                    blocks.add(InterpreterData.BasicBlock(
                        code, id, codeLength, event
                    ))
                    break
                }
            }
        }
    } catch(_: BufferUnderflowException) {}

    return Pair(blocks, constants)
}

fun parseConstants(buf: ByteBuffer): Map<Int, Value> {
    val map = mutableMapOf<Int, Value>()

    while(true) {
        val next = buf.get()

        buf.position(buf.position()-1)

        if(next.toInt() == -127) {
            break
        }

        val id = buf.getInt()
        val type = buf.get()


        when(type.toInt()) {
            1 -> {
                map[id] = Value.Number(buf.getDouble())
            }
            2 -> {
                var chars = ""
                while(true) {
                    val ch = buf.getChar()
                    if(ch.code == 0) break
                    chars += ch
                }
                map[id] = Value.String(chars)
            }
            3 -> {
                var chars = ""
                while(true) {
                    val ch = buf.getChar()
                    if(ch.code == 0) break
                    chars += ch
                }
                map[id] = Value.Symbol(chars)
            }
            4 -> {
                map[id] = Value.BasicBlockRef(buf.getInt())
            }
        }
    }

    return map
}

class Interpreter(val constants: Map<Int, Value>, val blockMap: Map<Int, InterpreterData.BasicBlock>, val coroutineScope: CoroutineScope) {
    val environment: Environment = Environment()
    val scope: MutableList<String> = mutableListOf()
    suspend fun runBlock(blockId: Int): Value {
        scope.add("block:$blockId")
        environment.localVariables.pushFrame()
        val block = blockMap[blockId]!!
        println("Id: $blockId | block: $block")
        val buf = block.code.duplicate().position(0)
        var opcode = peek(buf)
        while(opcode.toInt() != 0) {
            runInstruction(buf)
            opcode = peek(buf)
            if(environment.endBlock) {
                environment.localVariables.popFrame()
                scope.removeLast()
                return environment.returnValue
            }
        }
        environment.localVariables.popFrame()
        scope.removeLast()
        return Value.Null
    }

    suspend fun runFunction(functionName: String): Value {
        scope.add("function$functionName")
        for(block in blockMap) {
            val buf = block.value.code
            if(block.value.event.id != 6) {
                continue
            }
            val name = constants[block.value.event.constant]
            if(name is Value.Symbol && name.value == functionName) {
                val value = runBlock(block.value.id)
                environment.endBlock = false
                scope.removeLast()
                return value
            }
        }
        scope.removeLast()
        return Value.Null
    }

    private suspend fun runInstruction(buf: ByteBuffer) {
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
            println("""
==== INTERPRETER PRE STACK TRACE ==== 
Command: ${opcodes[opcode.toInt()]!!.command} 
Stack: ${this.environment.stack}
Locals: ${this.environment.localVariables}
Scope: ${this.scope}
========== AFTER COMMAND ===========""")
            opcodes[opcode.toInt()]!!.visit(this)
            println("""Command: ${opcodes[opcode.toInt()]!!.command} 
Stack: ${this.environment.stack}
Locals: ${this.environment.localVariables}
Scope: ${this.scope}
=================================
""")
        }
    }
}