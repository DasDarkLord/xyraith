package bytecode

import Logger
import events
import ir.Argument
import ir.BasicBlock
import ir.Node
import registry.commandRegistry
import java.nio.ByteBuffer

class Emitter(val blocks: List<BasicBlock>) {
    val array = ByteBuffer.allocate(1000)
    var stackCounter: Short = 0
    val constants: MutableMap<Argument, Int> = mutableMapOf()
    val constantsArray = ByteBuffer.allocate(1000)
    val resultRegisters = mutableListOf<Short>()

    fun mov(id: Short, constant: Int) {
        Logger.trace("Emitter State | Inserting `mov` command (id: $id) (constant: $constant) (registers: $stackCounter)")
        array.put(0)
        array.putShort(id)
        array.putInt(constant)
    }
    fun startEmitting(): ByteBuffer {
        Logger.trace("Emitter State | Starting emission (registers: $stackCounter)")
        blocks.forEach {
            stackCounter = 0
            emitBlock(it)
        }
        for(x in 1..20) {
            array.put(-127)
        }
        val caPos = constantsArray.position()
        return ByteBuffer.wrap(ByteArray(24) { return@ByteArray -127 } + constantsArray.array().sliceArray(0..caPos) + array.array())
    }

    fun checkState() {
        val clone1 = array.duplicate()!!.flip().array().toList()

        Logger.trace("Emitter State | Here's a progress report! $clone1")
    }
    fun emitBlock(block: BasicBlock) {
        Logger.trace("Emitter State | Emitting block (registers: $stackCounter)")
        for(x in 1..20) {
            array.put(-127)
        }
        array.putInt(block.id)
        println("eventId: ${block.eventId}")
        if(block.eventId.startsWith(":")) {
            val sym = Argument.Symbol(block.eventId)
            if(!constants.containsKey(sym)) {
                insertArgument(sym)
            }
            array.putInt(6)
            array.putInt(constants[sym]!!)
        } else {
            array.putInt(events[block.eventId]!!)
        }

        block.code.forEach {
            emitInstruction(it)
        }
    }

    fun emitInstruction(node: Node) {
        Logger.trace("Emitter State | Emitting instruction ${node.display()} (registers: $stackCounter)")
        node.arguments.forEach {
            emitArgument(it)
        }
        val size = node.arguments.size
        if(commandRegistry[node.name]!!["opcode"] == null) {
            array.put(127)
            array.putShort(commandRegistry[node.name]!!["opcodeExtension"] as Short)
            Logger.trace("Emitter State | Emitting instruction with $size registers used & extended opcodes (registers: $stackCounter)")
        } else {
            array.put(commandRegistry[node.name]!!["opcode"] as Byte)
            Logger.trace("Emitter State | Emitting instruction with $size registers used & regular opcodes (registers: $stackCounter) (opcode inserted: ${commandRegistry[node.name]!!["opcode"] as Byte})")
        }
        stackCounter = (stackCounter - size + 1).toShort()
        val final = stackCounter
        array.putShort(final) // 0 | 5 (-5)

        for(x in 1..size) {
            Logger.trace("Emitter State | Emitting register $stackCounter")
            array.putShort(stackCounter++)
            Logger.trace("Emitter State | Stackcounter is now $stackCounter")
        }
        stackCounter = final
        checkState()
        Logger.trace("Emitter State | Finishing instruction ${node.display()} (registers: $stackCounter) (size: $size)")
        Logger.trace("-------------------------")
    }

    fun insertArgument(argument: Argument) {
        Logger.trace("Emitter State | Inserting argument ${argument.display()} (registers: $stackCounter)")
        when(argument) {
            is Argument.BasicBlockRef -> {
                constants[argument] = constants.size+1
                constantsArray.putInt(constants.size)
                constantsArray.put(5)
                constantsArray.putInt(argument.value)
            }
            is Argument.Number -> {
                constants[argument] = constants.size+1
                constantsArray.putInt(constants.size)
                constantsArray.put(1)
                constantsArray.putDouble(argument.value)
            }
            is Argument.SSARef -> {}
            is Argument.Selector -> {}
            is Argument.String -> {
                constants[argument] = constants.size+1
                constantsArray.putInt(constants.size)
                constantsArray.put(2)
                for(char in argument.value.toCharArray()) {
                    constantsArray.putChar(char)
                }
                constantsArray.putChar(Char.MIN_VALUE)
            }
            is Argument.Symbol -> {
                constants[argument] = constants.size+1
                constantsArray.putInt(constants.size)
                constantsArray.put(3)
                for(char in argument.value.toCharArray()) {
                    constantsArray.putChar(char)
                }
                constantsArray.putChar(Char.MIN_VALUE)
            }
        }
    }
    fun emitArgument(argument: Argument) {
        Logger.trace("Emitter State | Emitting argument ${argument.display()} (registers: $stackCounter)")
        if(!constants.containsKey(argument)) {
            insertArgument(argument)
        }

        if(constants.containsKey(argument)) {
            Logger.trace("Emitter State | Valid argument! Inserting it into the bytecode via `mov` instruction. (registers: $stackCounter)")
            mov(++stackCounter, constants[argument]!!)
        }
        checkState()
        Logger.trace("Emitter State | Ending emitting argument ${argument.display()} (registers: $stackCounter)")
    }

    fun getBytes(): ByteBuffer {
        return array
    }

}