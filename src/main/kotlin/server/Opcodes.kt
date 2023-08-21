package server

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

fun Interpreter.getCurrentTime(buf: ByteBuffer) {
    val unixTime = System.currentTimeMillis()
    val target = buf.getShort().toInt()
    registers[target] = Value.Number(unixTime.toDouble() / 1000.0)
}

fun Interpreter.store(buf: ByteBuffer) {
    val target = buf.getShort().toInt()
    val value = buf.getShort().toInt()
    val storeIn = buf.getShort().toInt()


    registers[target] = Value.Null
    println("vars: $variables")
    variables[registers[storeIn]] = registers[value]
}

fun Interpreter.load(buf: ByteBuffer) {
    val target = buf.getShort().toInt()
    val loadFrom = buf.getShort().toInt()
    println("vars: $variables")
    registers[target] = variables[registers[loadFrom]]!!
}