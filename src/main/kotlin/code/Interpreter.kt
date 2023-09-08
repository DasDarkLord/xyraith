package code

import java.nio.ByteBuffer

val shortcodes: Map<Int, Visitable> = visitables.filter { obj -> obj.isExtension }.associateBy { obj -> obj.code }
val opcodes: Map<Int, Visitable> = visitables.filter { obj -> !obj.isExtension }.associateBy { obj -> obj.code }

fun ByteBuffer.peek(): Byte {
    val out = this.get()
    this.position(this.position()-1)
    return out
}
class Interpreter(val constants: Map<Int, parser.Value>, val blockMap: Map<Int, ByteBuffer>) {
    val environment: Environment = Environment()

    fun runBlock(blockId: Int) {
        val block = blockMap[blockId]!!.asReadOnlyBuffer().position(0)
        val zero = block.get()
        val id = block.getInt()
        val eventId = block.get()
        println("zero: $zero, id: $id, eventId: $eventId")
        var opcode = block.peek()
        println("peeked: $opcode")
        while(opcode.toInt() != 0) {
            runInstruction(block)
            opcode = block.peek()
        }
    }

    fun runInstruction(buf: ByteBuffer) {
        val opcode = buf.get()
        if(opcode.toInt() == 1) {
            val id = buf.getInt()
            environment.stack.add(constants[id]!!)
        } else if (opcode.toInt() == 127) {

        } else {
            opcodes[opcode.toInt()]!!.visit(this)
        }
    }
}