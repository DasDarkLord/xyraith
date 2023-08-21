package server

import ir.Argument
import java.nio.ByteBuffer

fun Interpreter.mov(buf: ByteBuffer) {
    val register = buf.getShort().toInt()
    val constant = buf.getInt()
    registers[register] = constants[constant]!!
}

fun Interpreter.add(buf: ByteBuffer) {
    val target = buf.getShort().toInt()
    val lhs = buf.getShort().toInt()
    val rhs = buf.getShort().toInt()
    registers[target] = Value.Number(registers[lhs].toNumber() + registers[rhs].toNumber())
}

fun Interpreter.consoleLog(buf: ByteBuffer) {
    buf.getShort()
    val reg = buf.getShort().toInt()
    println(registers[reg].toDisplay())
}