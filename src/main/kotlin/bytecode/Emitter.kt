package bytecode

import ir.Argument
import ir.BasicBlock
import ir.Node
import parser.commandRegistry
import java.nio.ByteBuffer

class Emitter(val blocks: List<BasicBlock>) {
    val array = ByteBuffer.allocate(1000)
    var stackCounter: Short = 0
    val constants: MutableMap<Argument, Int> = mutableMapOf()
    val constantsArray = ByteBuffer.allocate(1000)

    inline fun mov(id: Short, constant: Int) {
        println("Emitter State | Inserting `mov` command (id: $id) (constant: $constant) (registers: $stackCounter)")
        array.put(0)
        array.putShort(id)
        array.putInt(constant)
    }
    fun startEmitting(): ByteBuffer {
        println("Emitter State | Starting emission (registers: $stackCounter)")
        blocks.forEach {
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

        println("Emitter State | Here's a progress report! $clone1")
    }
    fun emitBlock(block: BasicBlock) {
        println("Emitter State | Emitting block (registers: $stackCounter)")
        for(x in 1..20) {
            array.put(-127)
        }
        array.putInt(block.id)
        array.putInt(0)

        block.code.forEach {
            emitInstruction(it)
        }
    }

    fun emitInstruction(node: Node) {
        println("Emitter State | Emitting instruction ${node.display()} (registers: $stackCounter)")
        node.arguments.forEach {
            emitArgument(it)
        }
        val regsUsed: Int = commandRegistry[node.name]!!["registersUsed"]!! as Int
        val regsAdded: Short = (commandRegistry[node.name]!!["registersAdded"]!! as Int).toShort()
        if(commandRegistry[node.name]!!["opcode"] == null) {
            array.putShort(commandRegistry[node.name]!!["opcodeExtension"] as Short)
            println("Emitter State | Emitting instruction with $regsUsed registers used & extended opcodes (registers: $stackCounter)")
        } else {
            array.put(commandRegistry[node.name]!!["opcode"] as Byte)
            println("Emitter State | Emitting instruction with $regsUsed registers used & regular opcodes (registers: $stackCounter) (opcode inserted: ${commandRegistry[node.name]!!["opcode"] as Byte})")
        }
        array.putShort((stackCounter-regsUsed+regsAdded).toShort())
        for(x in 1..regsUsed) {
            array.putShort(stackCounter--)
        }
        stackCounter = (stackCounter + regsAdded).toShort()

        checkState()
        println("Emitter State | Finishing instruction ${node.display()} (registers: $stackCounter) (regsUsed: $regsUsed) (regsAdded: $regsAdded)")
        println("-------------------------")
    }

    fun insertArgument(argument: Argument) {
        println("Emitter State | Inserting argument ${argument.display()} (registers: $stackCounter)")
        when(argument) {
            is Argument.BasicBlockRef -> {}
            is Argument.Number -> {
                constants[argument] = constants.size+1
                constantsArray.putInt(constants.size)
                constantsArray.put(1)
                constantsArray.putDouble(argument.value)
            }
            is Argument.SSARef -> {}
            is Argument.Selector -> {}
            is Argument.String -> {}
            is Argument.Symbol -> {}
        }
    }
    fun emitArgument(argument: Argument) {
        println("Emitter State | Emitting argument ${argument.display()} (registers: $stackCounter)")
        if(!constants.containsKey(argument)) {
            insertArgument(argument)
        }

        if(constants.containsKey(argument)) {
            println("Emitter State | Valid argument! Inserting it into the bytecode via `mov` instruction. (registers: $stackCounter)")
            mov(++stackCounter, constants[argument]!!)
        }
        checkState()
        println("Emitter State | Ending emitting argument ${argument.display()} (registers: $stackCounter)")
    }

    fun getBytes(): ByteBuffer {
        return array
    }

}