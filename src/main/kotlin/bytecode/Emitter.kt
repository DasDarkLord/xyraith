package bytecode

import events
import parser.Ast
import parser.Value
import registry.commandRegistry
import java.nio.ByteBuffer

class Emitter {
    val array = ByteBuffer.allocate(1000)
    var stackCounter: Short = 0
    val constants: MutableMap<Value, Int> = mutableMapOf()
    val constantsArray = ByteBuffer.allocate(1000)

    var blockId = 0

    fun mov(id: Short, constant: Int) {
        Logger.trace("Emitter State | Inserting `mov` command (id: $id) (constant: $constant) (registers: $stackCounter)")
        array.put(0)
        array.putShort(id)
        array.putInt(constant)
    }
    fun startEmitting(tree: List<Ast.Event>): ByteBuffer {
        Logger.trace("Emitter State | Starting emission (registers: $stackCounter)")
        for(event in tree) {
            emitBlock(event.code)
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

    fun emitBlock(block: Ast.Block) {
        Logger.trace("Emitter State | Emitting block (registers: $stackCounter)")
        for(x in 1..20) {
            array.put(-127)
        }
        array.putInt(++blockId)
        if(block.eventName.startsWith(":")) {
            val sym = Value.Symbol(block.eventName)
            if(!constants.containsKey(sym)) {
                insertValue(sym)
            }
            array.putInt(6)
            array.putInt(constants[sym]!!)
        } else {
            array.putInt(events[block.eventName]!!)
        }

        for(node in block.nodes) {
            emitNode(node)
        }
    }

    fun emitNode(node: Ast.Command) {
        val opcode = commandRegistry[node.name]!!["opcode"]!!

    }

    fun insertValue(value: Value) {
        when(value) {
            is Value.BasicBlockRef -> {
                constants[value] = constants.size+1
                constantsArray.putInt(constants.size)
                constantsArray.put(5)
                constantsArray.putInt(value.value)
            }

            is Value.Block -> {

            }
            is Value.Command -> TODO()
            is Value.Null -> TODO()
            is Value.Number -> {
                constants[value] = constants.size+1
                constantsArray.putInt(constants.size)
                constantsArray.put(1)
                constantsArray.putDouble(value.value)
            }
            is Value.Position -> TODO()
            is Value.Selector -> TODO()
            is Value.String -> {
                constants[value] = constants.size+1
                constantsArray.putInt(constants.size)
                constantsArray.put(2)
                for(char in value.value.toCharArray()) {
                    constantsArray.putChar(char)
                }
                constantsArray.putChar(Char.MIN_VALUE)
            }
            is Value.Symbol -> {
                constants[value] = constants.size+1
                constantsArray.putInt(constants.size)
                constantsArray.put(3)
                for(char in value.value.toCharArray()) {
                    constantsArray.putChar(char)
                }
                constantsArray.putChar(Char.MIN_VALUE)
            }
        }
    }
    fun emitArgument(argument: Value) {
        Logger.trace("Emitter State | Emitting argument ${argument.toDisplay()} (registers: $stackCounter)")
        if(!constants.containsKey(argument)) {
            insertValue(argument)
        }

        if(constants.containsKey(argument)) {
            Logger.trace("Emitter State | Valid argument! Inserting it into the bytecode via `mov` instruction. (registers: $stackCounter)")
            mov(++stackCounter, constants[argument]!!)
        }
        checkState()
        Logger.trace("Emitter State | Ending emitting argument ${argument.toDisplay()} (registers: $stackCounter)")
    }
}