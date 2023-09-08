package code

import events
import parser.Ast
import parser.Value
import registry.commandRegistry
import java.nio.ByteBuffer

val BUFFER_SIZE = 100

fun ByteBuffer.prettyPrint(): String {
    var output = "["
    val cloned = this.duplicate().limit(this.position())
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

class Emitter(val ast: List<Ast.Event>) {
    var blockMap: MutableMap<Int, ByteBuffer> = mutableMapOf()
    var blockIdRecord: Int = 1
    var constantIdRecord: Int = 1
    var constants: MutableMap<Value, Int> = mutableMapOf()
    var constantsBytes: ByteBuffer = ByteBuffer.allocate(BUFFER_SIZE)
    override fun toString(): String {
        var output = """
Emitter {
  constants=$constants,
  blockMap={""".trimIndent()
        for(pair in blockMap) {
            output += """${pair.key}:${pair.value.prettyPrint()}"""
        }
        output += "}\n  constantsBytes=${constantsBytes.prettyPrint()}"
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
    private fun emitBlock(block: Ast.Block) {
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
    }

    private fun emitCommand(command: Ast.Command, blockId: Int) {
        val entry = commandRegistry[command.name]!!
        if(entry["opcode"] != null) {
            val opcode = entry["opcode"]!! as Byte
            for(value in command.arguments) {
                emitValue(value, blockId)
            }
            blockMap[blockId]?.put(opcode)
        } else if(entry["opcodeExtension"] != null) {
            val extension = entry["opcodeExtension"]!! as Short
            for(value in command.arguments) {
                emitValue(value, blockId)
            }
            blockMap[blockId]?.put(0xFF.toByte())
            blockMap[blockId]?.putShort(extension)
        }
    }
    private fun emitValue(value: Value, blockId: Int) {
        blockMap[blockId]?.put(1)
        println("emitter: $constants")
        if(constants.containsKey(value)) {
            blockMap[blockId]?.putInt(constants[value]!!)
            return
        }
        val id = constantIdRecord++
        blockMap[blockId]?.putInt(id)
        constants[value] = id
        println("emitter: $constants")
        when(value) {
            is Value.BasicBlockRef -> TODO()
            is Value.Block -> TODO()
            is Value.Command -> TODO()
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
            is Value.Symbol -> TODO()
        }

    }
}