package registry

import code.instructions.*
import code.opcodes
import code.shortcodes

val commandRegistry = (
    opcodes.map {
        it.value.command to mutableMapOf(
            "opcode" to it.value.code.toByte(),
            "command" to it.value.command,
            "object" to it.value,
        )
    }.toMap().toMutableMap() +
            shortcodes.map {
                it.value.command to mutableMapOf(
                    "opcodeExtension" to it.value.code.toShort(),
                    "command" to it.value.command,
                    "object" to it.value,
                )
            }.toMap().toMutableMap()).toMutableMap()

fun validateRegistry() {
    val opcodes = mutableListOf<Int>()
    val shortcodes = mutableListOf<Int>()
    for(code in commandRegistry) {
        val extension = code.value["opcodeExtension"] as? Int
        val opcode = code.value["opcode"] as? Int
        if(!shortcodes.contains(extension)) {
            if(extension != null)
                shortcodes.add(extension)
        } else {
            println("WARNING: Duplicate shortcode $extension")
        }

        if(!opcodes.contains(opcode)) {
            if(opcode != null)
                opcodes.add(opcode)
        } else {
            println("WARNING: Duplicate shortcode $opcode")
        }
    }
}