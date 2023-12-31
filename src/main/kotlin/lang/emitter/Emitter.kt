package lang.emitter

import error.Unreachable
import lang.ir.IR
import registry.commandRegistry
import java.nio.ByteBuffer

const val BUFFER_SIZE = 1000

class IREmitter(val module: IR.Module) {
    private var constantIdRecord: Int = 1
    private var constants: MutableMap<IR.Argument, Int> = mutableMapOf()
    private var constantsBytes: ByteBuffer = ByteBuffer.allocate(BUFFER_SIZE)
    private var register: Int = 1

    /**
     * Map of SSA ID <-> register allocated to it
     */
    private var registerAllocations: MutableMap<Int, Int> = mutableMapOf()

    fun emit(): List<Byte> {
        val out = mutableListOf<Byte>()
        for(block in module.blocks) {
            out.addAll(emitBlock(block).array().toMutableList())
        }

        val arr2 = ByteArray(constantsBytes.position())
        constantsBytes.rewind()
        constantsBytes.get(arr2)
        val arr3 = arr2.toMutableList()
        return arr3 + out
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
            buf.putInt(addConstant(IR.Argument.Symbol(block.blockData.functionName))!!)
        } else if (block.blockData is IR.BlockData.Event) {
            val eventId = block.blockData.eventId
            buf.put(eventId.toByte())
        }
        buf.put(-123)
        val startPos = buf.position()
        for (command in block.commands) {
            val newRegister = emitCommand(command, buf)
            registerAllocations[command.id] = newRegister
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

    private fun emitCommand(command: IR.Command, buf: ByteBuffer): Int {
        val preRegister = register
        val commandObject = commandRegistry[command.name]!!
        val usedRegisters = mutableListOf<Int>()
        for(argument in command.arguments) {
            usedRegisters.add(allocateRegisterWithConstant(argument, buf))
        }
        if(commandObject["opcodeExtension"] == null && commandObject["opcode"] != null) {
            val opcode = commandObject["opcode"]!! as Byte
            buf.put(opcode)
        }
        register -= usedRegisters.size

        buf.put(command.arguments.size.toByte())
        buf.putInt(++register)
        for(register in usedRegisters) {
            buf.putInt(register)
        }
        return register
    }

    private fun allocateRegisterWithConstant(value: IR.Argument, buf: ByteBuffer): Int {
        val constantID = addConstant(value)

        // TODO: fix ordering of output
        // currently, commands are placed out of order
        // %1 = load [:a]
        // %2 = string [%1, "hi"]
        // %3 = console.log %2
        // this results in the output of:
        // (console.log (string "hi" (load a))
        // this is not ok!! this is not right!!
        // fix this by properly associating commands to their SSA value
        // don't emit them immediately, only emit them if they're needed
        if(value !is IR.Argument.SSARef) {
            buf.put(1)
            buf.putInt(++register)
            buf.putInt(constantID!!)
            return register
        } else {
            return registerAllocations[value.value]!!
        }
    }

    private fun addConstant(value: IR.Argument): Int? {
        return if(!constants.containsKey(value) && value !is IR.Argument.SSARef) {
            constants[value] = ++constantIdRecord
            constantsBytes.putInt(constantIdRecord)
            when(value) {
                is IR.Argument.Number -> {
                    constantsBytes.put(1)
                    constantsBytes.putDouble(value.value)
                }
                is IR.Argument.BlockRef -> {
                    constantsBytes.put(4)
                    constantsBytes.putInt(value.value)
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

                else -> { throw Unreachable() }
            }
            constantIdRecord
        } else {
            constants[value]
        }
    }
}