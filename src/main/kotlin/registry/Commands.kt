package registry

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
    val opcodes = mutableListOf<Byte>()
    val shortcodes = mutableListOf<Short>()
    for(code in commandRegistry) {
        val extension = code.value["opcodeExtension"] as? Short
        val opcode = code.value["opcode"] as? Byte
        if(!shortcodes.contains(extension)) {
            if(extension != null) {
                shortcodes.add(extension)
            }
        } else {
            println("WARNING: Duplicate shortcode $extension")
        }

        if(!opcodes.contains(opcode)) {
            if(opcode != null) {
                opcodes.add(opcode)
            }
        } else {
            println("WARNING: Duplicate shortcode $opcode")
        }
    }
}