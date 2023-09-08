package registry

import code.instructions.*
import code.opcodes
import code.shortcodes

val commandRegistry = (
    opcodes.map {
        it.value.command to mutableMapOf(
            "opcode" to it.value.code.toByte(),
            "command" to it.value.command,
        )
    }.toMap().toMutableMap() +
            shortcodes.map {
                it.value.command to mutableMapOf(
                    "opcodeExtension" to it.value.code.toShort(),
                    "command" to it.value.command,
                )
            }.toMap().toMutableMap()).toMutableMap()