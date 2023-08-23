package server.extensions

import registry.Extensions
import server.Value
import server.interpreter.Interpreter
import kotlin.random.Random

fun numberExtension(interpreter: Interpreter) {
    interpreter.addExtensionInstruction(Extensions.Number.RANDOM) {
        val target = it.getShort().toInt()
        val lhs = interpreter.registers[it.getShort().toInt()].toNumber()
        val rhs = interpreter.registers[it.getShort().toInt()].toNumber()
        interpreter.registers[target] = Value.Number(Random.nextDouble(lhs, rhs))
    }
}