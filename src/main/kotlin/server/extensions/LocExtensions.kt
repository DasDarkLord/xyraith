package server.extensions

import registry.Extensions
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

    interpreter.addExtensionInstruction(Extensions.Location.SETX) {
        val target = it.getShort().toInt()
        val loc = interpreter.registers[it.getShort().toInt()]
        val value = interpreter.registers[it.getShort().toInt()].toNumber()
        if(loc is Value.Position) {
            interpreter.registers[target] = Value.Position(value, loc.y, loc.z, loc.pitch, loc.yaw)
            return@addExtensionInstruction
        }
        interpreter.registers[target] = Value.Position(value, 0.0, 0.0, 0.0, 0.0)
    }

    interpreter.addExtensionInstruction(Extensions.Location.SETY) {
        val target = it.getShort().toInt()
        val loc = interpreter.registers[it.getShort().toInt()]
        val value = interpreter.registers[it.getShort().toInt()].toNumber()
        if(loc is Value.Position) {
            interpreter.registers[target] = Value.Position(loc.x, value, loc.z, loc.pitch, loc.yaw)
            return@addExtensionInstruction
        }
        interpreter.registers[target] = Value.Position(0.0, value, 0.0, 0.0, 0.0)
    }

    interpreter.addExtensionInstruction(Extensions.Location.SETZ) {
        val target = it.getShort().toInt()
        val loc = interpreter.registers[it.getShort().toInt()]
        val value = interpreter.registers[it.getShort().toInt()].toNumber()
        if(loc is Value.Position) {
            interpreter.registers[target] = Value.Position(loc.x, loc.y, value, loc.pitch, loc.yaw)
            return@addExtensionInstruction
        }
        interpreter.registers[target] = Value.Position(0.0, 0.0, value, 0.0, 0.0)
    }

    interpreter.addExtensionInstruction(Extensions.Location.SETPITCH) {
        val target = it.getShort().toInt()
        val loc = interpreter.registers[it.getShort().toInt()]
        val value = interpreter.registers[it.getShort().toInt()].toNumber()
        if(loc is Value.Position) {
            interpreter.registers[target] = Value.Position(loc.x, loc.y, loc.z, value, loc.yaw)
            return@addExtensionInstruction
        }
        interpreter.registers[target] = Value.Position(0.0, 0.0, 0.0, value, 0.0)
    }

    interpreter.addExtensionInstruction(Extensions.Location.SETYAW) {
        val target = it.getShort().toInt()
        val loc = interpreter.registers[it.getShort().toInt()]
        val value = interpreter.registers[it.getShort().toInt()].toNumber()
        if(loc is Value.Position) {
            interpreter.registers[target] = Value.Position(loc.x, loc.y, loc.z, loc.pitch, value)
            return@addExtensionInstruction
        }
        interpreter.registers[target] = Value.Position(0.0, 0.0, 0.0, 0.0, value)
    }

    interpreter.addExtensionInstruction(Extensions.Location.SHIFTX) {
        val target = it.getShort().toInt()
        val loc = interpreter.registers[it.getShort().toInt()]
        val value = interpreter.registers[it.getShort().toInt()].toNumber()
        if(loc is Value.Position) {
            interpreter.registers[target] = Value.Position(value+loc.x, loc.y, loc.z, loc.pitch, loc.yaw)
            return@addExtensionInstruction
        }
        interpreter.registers[target] = Value.Position(value, 0.0, 0.0, 0.0, 0.0)
    }

    interpreter.addExtensionInstruction(Extensions.Location.SHIFTY) {
        val target = it.getShort().toInt()
        val loc = interpreter.registers[it.getShort().toInt()]
        val value = interpreter.registers[it.getShort().toInt()].toNumber()
        if(loc is Value.Position) {
            interpreter.registers[target] = Value.Position(loc.x, value+loc.y, loc.z, loc.pitch, loc.yaw)
            return@addExtensionInstruction
        }
        interpreter.registers[target] = Value.Position(0.0, value, 0.0, 0.0, 0.0)
    }

    interpreter.addExtensionInstruction(Extensions.Location.SHIFTZ) {
        val target = it.getShort().toInt()
        val loc = interpreter.registers[it.getShort().toInt()]
        val value = interpreter.registers[it.getShort().toInt()].toNumber()
        if(loc is Value.Position) {
            interpreter.registers[target] = Value.Position(loc.x, loc.y, value+loc.z, loc.pitch, loc.yaw)
            return@addExtensionInstruction
        }
        interpreter.registers[target] = Value.Position(0.0, 0.0, value, 0.0, 0.0)
    }

    interpreter.addExtensionInstruction(Extensions.Location.SHIFTPITCH) {
        val target = it.getShort().toInt()
        val loc = interpreter.registers[it.getShort().toInt()]
        val value = interpreter.registers[it.getShort().toInt()].toNumber()
        if(loc is Value.Position) {
            interpreter.registers[target] = Value.Position(loc.x, loc.y, loc.z, value, loc.yaw)
            return@addExtensionInstruction
        }
        interpreter.registers[target] = Value.Position(0.0, 0.0, 0.0, value, 0.0)
    }

    interpreter.addExtensionInstruction(Extensions.Location.SHIFTYAW) {
        val target = it.getShort().toInt()
        val loc = interpreter.registers[it.getShort().toInt()]
        val value = interpreter.registers[it.getShort().toInt()].toNumber()
        if(loc is Value.Position) {
            interpreter.registers[target] = Value.Position(loc.x, loc.y, loc.z, loc.pitch, value)
            return@addExtensionInstruction
        }
        interpreter.registers[target] = Value.Position(0.0, 0.0, 0.0, 0.0, value)
    }
}