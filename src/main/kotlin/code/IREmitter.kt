package code

import ir.IR
import registry.commandRegistry
import java.nio.ByteBuffer

const val IR_BUFFER_SIZE = 1000

class IREmitter(val module: IR.Module) {
    private var constantIdRecord: Int = 1
    private var constants: MutableMap<IR.Argument, Int> = mutableMapOf()
    private var constantsBytes: ByteBuffer = ByteBuffer.allocate(IR_BUFFER_SIZE)

    fun emit(): MutableList<Byte> {
        val out = mutableListOf<Byte>()
        for(block in module.blocks) {
            out.addAll(emitBlock(block).array().toMutableList())
        }
        return out
    }

    /**
     * ID MEANINGS:
     * -120 = Length of code segment
     * -121 = ID of block
     * -122 = Event data
     * -123 = Code section
     * -126 = End of block
     * -127 = Start of new block
     */
    private fun emitBlock(block: IR.BasicBlock): ByteBuffer {
        val buf = ByteBuffer.allocate(BUFFER_SIZE)
        buf.put(-121)
        buf.putInt(block.id)
        buf.put(-122)
        if (block.blockData is IR.BlockData.Function) {
            buf.put(6)
        } else if (block.blockData is IR.BlockData.Event) {
            val eventId = block.blockData.eventId
            buf.put(eventId.toByte())
        }
        buf.put(-123)
        val startPos = buf.position()
        for (command in block.commands) {
            emitCommand(command, buf)
        }
        val endCodePos = buf.position()
        buf.rewind()
        val arr = ByteArray(endCodePos)
        buf.get(arr)
        return ByteBuffer.allocate(arr.size + 7)
            .put(-127)
            .put(-120)
            .putInt(endCodePos-startPos)
            .put(arr)
            .put(-126)

    }

    private fun emitCommand(command: IR.Command, buf: ByteBuffer) {
        val commandObject = commandRegistry[command.name]!!
        for(argument in command.arguments) {
            pushRawValue(argument, buf)
        }
        if(commandObject["opcodeExtension"] == null && commandObject["opcode"] != null) {
            val opcode = commandObject["opcode"]!! as Byte
            buf.put(opcode)
        }
        if(commandObject["opcodeExtension"] != null && commandObject["opcode"] == null) {
            val shortcode = commandObject["opcodeExtension"]!! as Short
            buf.put(127)
            buf.putShort(shortcode)
        }
        buf.put(command.arguments.size.toByte())
    }

    fun pushRawValue(value: IR.Argument, buf: ByteBuffer) {
        val constantID = addConstant(value)
        if(value !is IR.Argument.SSARef) {
            buf.put(0)
            buf.putInt(constantID)
        }
    }

    private fun addConstant(value: IR.Argument): Int {
        return if(!constants.containsKey(value)) {
            constants[value] = ++constantIdRecord
            constantsBytes.putInt(constantIdRecord)
            when(value) {
                is IR.Argument.Number -> {
                    constantsBytes.put(1)
                    constantsBytes.putDouble(value.value)
                }
                is IR.Argument.BlockRef -> {
                    constantsBytes.put(4)
                }
                is IR.Argument.SSARef -> {
                    // will be on stack in bytecode, do nothing
                }
                is IR.Argument.String -> {
                    constantsBytes.put(2)
                    for(char in value.value.toCharArray()) {
                        constantsBytes.putChar(char)
                    }
                    constantsBytes.putShort(0)
                }
                is IR.Argument.Symbol -> {
                    constantsBytes.put(3)
                    for(char in value.value.toCharArray()) {
                        constantsBytes.putChar(char)
                    }
                    constantsBytes.putShort(0)
                }
            }
            constantIdRecord
        } else {
            constants[value]!!
        }
    }
}