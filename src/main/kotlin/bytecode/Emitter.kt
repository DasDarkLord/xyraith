package bytecode

import ir.Argument
import ir.BasicBlock
import ir.Node
import java.nio.ByteBuffer

class Emitter(val blocks: List<BasicBlock>) {
    val array = ByteBuffer.allocate(10000)
    fun startEmitting() {
        blocks.forEach {
            emitBlock(it)
        }
        for(x in 1..20) {
            array.put(-127)
        }
    }

    fun emitBlock(block: BasicBlock) {
        for(x in 1..20) {
            array.put(-127)
        }
        array.putInt(block.id)

        block.code.forEach {
            emitInstruction(it)
        }
    }

    fun emitInstruction(node: Node) {
        node.arguments.forEach {
            emitArgument(it)
        }
        when(node.name) {
            "add" -> array.put(3)
            "sub" -> array.put(4)
            "mul" -> array.put(5)
            "div" -> array.put(6)
            "player.sendMessage" -> {
                array.put(10)
                array.putShort(1.toShort())
            }
            else -> {}
        }
    }

    fun emitArgument(argument: Argument) {
        when(argument) {
            is Argument.BasicBlockRef -> {

            }
            is Argument.Number -> {
                array.put(1) // push
                array.put(1) // number
                array.putDouble(argument.value)
            }
            is Argument.SSARef -> { /* do nothing, it's already on the stack */ }
            is Argument.Selector -> {

            }
            is Argument.String -> {
                array.put(1) // push
                array.put(1) // number
                argument.value.toCharArray().forEach {
                    array.putChar(it)
                }
                array.put(0)
            }
            is Argument.Symbol -> {

            }
        }
    }
    fun getBytes(): ByteBuffer {
        return array
    }
}