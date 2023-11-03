package code

import blockMap
import instructions.Visitable
import instructions.visitables
import constants
import error.Unreachable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import net.minestom.server.entity.Entity
import net.minestom.server.event.Event
import net.minestom.server.instance.Instance
import java.nio.ByteBuffer
import parser.Value
import java.nio.BufferUnderflowException
import kotlin.Exception
import kotlin.concurrent.thread

val shortcodes: Map<Int, instructions.Visitable> = instructions.visitables.filter { obj -> obj.isExtension }.associateBy { obj -> obj.code }
val opcodes: Map<Int, instructions.Visitable> = instructions.visitables.filter { obj -> !obj.isExtension }.associateBy { obj -> obj.code }

fun peek(buf: ByteBuffer): Byte {
    val out = buf.get()
    buf.position(buf.position()-1)
    return out
}

fun runEvent(eventIdChk: Int, targets: MutableList<Entity> = mutableListOf(), instance: Instance? = null, event: Event? = null) {
    runBlocking {
        for(pair in blockMap) {
            val block = pair.value.duplicate().position(0)
            val zero = block.get()
            val id = block.getInt()
            val eventId = block.get()
            if(eventId.toInt() == eventIdChk) {
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

fun parseBytecode(buf: ByteBuffer): MutableList<InterpreterData.BasicBlock> {
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
                    val out = ByteBuffer.allocate(IR_BUFFER_SIZE)
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
                    println("""
                        id: $id
                        code: $code
                        codeLength: $codeLength
                        event: $event
                    """.trimIndent())
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

    return blocks
}

class Interpreter(val constants: Map<Int, Value>, val blockMap: Map<Int, ByteBuffer>, val coroutineScope: CoroutineScope) {
    val environment: Environment = Environment()

    suspend fun runBlock(blockId: Int): Value {
        val block = blockMap[blockId]!!.asReadOnlyBuffer().position(0)
        val zero = block.get()
        val id = block.getInt()
        val eventId = block.get()
        if(eventId.toInt() == 6) {
            block.get()
            block.getInt()
        }
        var opcode = peek(block)
        while(opcode.toInt() != 0) {
            runInstruction(block)
            opcode = peek(block)
            if(environment.endBlock) {
                return environment.returnValue
            }
        }
        return Value.Null
    }

    suspend fun runFunction(functionName: String): Value {
        for(block in blockMap) {
            val buf = block.value.asReadOnlyBuffer().position(0)
            buf.get()
            val id = buf.getInt()
            val eventId = buf.get()
            if(eventId.toInt() != 6) {
                continue
            }
            buf.get()
            val name = constants[buf.getInt()]
            if(name is Value.Symbol && name.value == functionName) {
                val value = runBlock(id)
                environment.endBlock = false
                return value
            }
        }
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
            opcodes[opcode.toInt()]!!.visit(this)
        }
    }
}