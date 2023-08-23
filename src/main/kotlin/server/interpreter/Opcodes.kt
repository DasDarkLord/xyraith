package server.interpreter

import constants
import functions
import server.Value
import java.nio.ByteBuffer

fun Interpreter.mov(buf: ByteBuffer) {
    val register = buf.getShort().toInt()
    val constant = buf.getInt()
    println("reg: $register constant: $constant")
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
    val storeIn = buf.getShort().toInt()
    val value = buf.getShort().toInt()


    registers[target] = Value.Null
    println("vars: $variables")
    variables[registers[storeIn]] = registers[value]
}

fun Interpreter.load(buf: ByteBuffer) {
    val target = buf.getShort().toInt()
    val loadFrom = buf.getShort().toInt()
    println("vars: $variables")
    if(variables[registers[loadFrom]] != null) {
        registers[target] = variables[registers[loadFrom]]!!
    } else {
        println("Warning: Variable ${registers[loadFrom]} is not in scope. Returning a `null` value from `load`.")
        registers[target] = Value.Null
    }

}

fun Interpreter.vec(buf: ByteBuffer) {
    val target = buf.getShort().toInt()
    val x = registers[buf.getShort().toInt()].toNumber()
    val y = registers[buf.getShort().toInt()].toNumber()
    val z = registers[buf.getShort().toInt()].toNumber()
    registers[target] = Value.Position(x, y, z)
}

fun Interpreter.pos(buf: ByteBuffer) {
    val target = buf.getShort().toInt()
    val x = registers[buf.getShort().toInt()].toNumber()
    val y = registers[buf.getShort().toInt()].toNumber()
    val z = registers[buf.getShort().toInt()].toNumber()
    val pitch = registers[buf.getShort().toInt()].toNumber()
    val yaw = registers[buf.getShort().toInt()].toNumber()
    registers[target] = Value.Position(x, y, z, pitch, yaw)
}

fun Interpreter.call(buf: ByteBuffer) {
    val target = buf.getShort().toInt()
    val invoke = buf.getShort().toInt()
    println("invoking: $invoke")
    val ip = buf.position()
    println("ip: $ip")
    println("searching for: ${registers[invoke]}")
    if(functions[registers[invoke]] != null) {
        println("IT'S A FUNCTION HOORAY!")
        interpretBlock(functions[registers[invoke]]!!)
        buf.position(ip)
    } else {
        println("it's NOT a function...")
        println("$functions")
    }
}