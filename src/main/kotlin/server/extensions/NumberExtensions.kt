package server.extensions

import parser.Value
import registry.Extensions
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