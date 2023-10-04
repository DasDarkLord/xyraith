package code

import events
import parser.Ast
import parser.Value
import registry.commandRegistry
import java.nio.ByteBuffer

const val BUFFER_SIZE = 10000

fun prettyPrint(buf: ByteBuffer): String {
    var output = "["
    val cloned = buf.duplicate().limit(buf.position())
    var count = 1
    for(byte in cloned.array()) {
        count++
        if(count > cloned.limit()) {
            output += "$byte]"
            return output
        } else {
            output += "$byte, "
        }
    }
    return output
}
// the large allocations of bytebuffers aren't a big deal - most OSs give pages
// of memory, so the memory allocated will only be kept
// if it gets actively used

class Emitter(private val ast: List<Ast.Event>) {
    var blockMap: MutableMap<Int, ByteBuffer> = mutableMapOf()
    private var blockIdRecord: Int = 1
    private var constantIdRecord: Int = 1
    var constants: MutableMap<Value, Int> = mutableMapOf()
    private var constantsBytes: ByteBuffer = ByteBuffer.allocate(BUFFER_SIZE)
    override fun toString(): String {
        var output = """
Emitter {
  constants=$constants,
  blockMap={""".trimIndent()
        for(pair in blockMap) {
            output += """${pair.key}:${prettyPrint(pair.value)}"""
        }
        output += "}\n  constantsBytes=${prettyPrint(constantsBytes)}"
        return output
    }

    fun emit() {
        for(event in ast) {
            emitEvent(event)
        }
    }

    private fun emitEvent(event: Ast.Event) {
        emitBlock(event.code)
    }

    private fun emitBlock(block: Ast.Block): Int {
        val eventName = block.eventName
        val eventId = events[eventName]!!
        val blockId = blockIdRecord++
        blockMap[blockId] = ByteBuffer.allocate(BUFFER_SIZE)
        blockMap[blockId]?.put(0)
        blockMap[blockId]?.putInt(blockId)
        blockMap[blockId]?.put(eventId.toByte())
        for(command in block.nodes) {
            emitCommand(command, blockId)
        }
        return blockId
    }

    private fun emitCommand(command: Ast.Command, blockId: Int) {
        val entry = commandRegistry[command.name]!!
        if(entry["opcode"] != null) {
            val opcode = entry["opcode"]!! as Byte
            for(value in command.arguments) {
                emitValue(value, blockId)
            }
            blockMap[blockId]?.put(opcode)
            blockMap[blockId]?.put(command.arguments.size.toByte())
        } else if(entry["opcodeExtension"] != null) {
            val extension = entry["opcodeExtension"]!! as Short
            for(value in command.arguments) {
                emitValue(value, blockId)
            }
            blockMap[blockId]?.put(127.toByte())
            blockMap[blockId]?.putShort(extension)
            blockMap[blockId]?.put(command.arguments.size.toByte())
        }
    }
    private fun emitValue(value: Value, blockId: Int) {
        if(constants.containsKey(value)) {
            blockMap[blockId]?.put(1)
            blockMap[blockId]?.putInt(constants[value]!!)
            return
        }
        val id = constantIdRecord++
        println("Preparing to emit constant $id with $value")
        if(value !is Value.Command && value !is Value.Block) {
            blockMap[blockId]?.put(1)
            blockMap[blockId]?.putInt(id)
            constants[value] = id
        }
        when(value) {
            is Value.BasicBlockRef -> TODO("this isnt compilable")
            is Value.Block -> {
                val newId = emitBlock(value.value)
                println("Emitting block: ${value.value} to $newId with constant $id")
                constants[Value.BasicBlockRef(newId)] = id
                println("constant ${constants[Value.BasicBlockRef(newId)]} == $id")
                constantsBytes.putInt(id)
                constantsBytes.put(4)
                constantsBytes.putInt(newId)

                blockMap[blockId]?.put(1)
                blockMap[blockId]?.putInt(id)
            }
            is Value.Command -> {
                emitCommand(value.value, blockId)
            }
            is Value.Null -> TODO()
            is Value.Number -> {
                constantsBytes.putInt(constantIdRecord)
                constantsBytes.put(1)
                constantsBytes.putDouble(value.value)
            }
            is Value.Position -> TODO()
            is Value.Selector -> TODO()
            is Value.String -> {
                constantsBytes.putInt(constantIdRecord)
                constantsBytes.put(2)
                for(char in value.value.toCharArray()) {
                    constantsBytes.putChar(char)
                }
                constantsBytes.putShort(0.toShort())
            }
            is Value.Symbol -> {
                constantsBytes.putInt(constantIdRecord)
                constantsBytes.put(3)
                for(char in value.value.toCharArray()) {
                    constantsBytes.putChar(char)
                }
                constantsBytes.putShort(0.toShort())
            }
            is Value.Array -> TODO()
            is Value.Bool -> TODO()
        }

    }
}