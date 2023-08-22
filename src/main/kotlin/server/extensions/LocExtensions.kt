package server.extensions

import Extensions
import server.Value
import server.interpreter.Interpreter

fun locExtensions(interpreter: Interpreter) {
    interpreter.addExtensionInstruction(Extensions.Location.GETX) {
        val target = it.getShort().toInt()
        val loc = interpreter.registers[it.getShort().toInt()]
        if(loc is Value.Position) {
            interpreter.registers[target] = Value.Number(loc.x)
        } else {
            interpreter.registers[target] = Value.Number(0.0)
        }
    }
    interpreter.addExtensionInstruction(Extensions.Location.GETY) {
        val target = it.getShort().toInt()
        val loc = interpreter.registers[it.getShort().toInt()]
        if(loc is Value.Position) {
            interpreter.registers[target] = Value.Number(loc.y)
        } else {
            interpreter.registers[target] = Value.Number(0.0)
        }
    }
    interpreter.addExtensionInstruction(Extensions.Location.GETZ) {
        val target = it.getShort().toInt()
        val loc = interpreter.registers[it.getShort().toInt()]
        if(loc is Value.Position) {
            interpreter.registers[target] = Value.Number(loc.z)
        } else {
            interpreter.registers[target] = Value.Number(0.0)
        }
    }
    interpreter.addExtensionInstruction(Extensions.Location.GETPITCH) {
        val target = it.getShort().toInt()
        val loc = interpreter.registers[it.getShort().toInt()]
        if(loc is Value.Position) {
            interpreter.registers[target] = Value.Number(loc.pitch)
        } else {
            interpreter.registers[target] = Value.Number(0.0)
        }
    }
    interpreter.addExtensionInstruction(Extensions.Location.GETYAW) {
        val target = it.getShort().toInt()
        val loc = interpreter.registers[it.getShort().toInt()]
        if(loc is Value.Position) {
            interpreter.registers[target] = Value.Number(loc.yaw)
        } else {
            interpreter.registers[target] = Value.Number(0.0)
        }
    }
}