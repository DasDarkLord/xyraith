val commandRegistry = mutableMapOf(
    "add" to mutableMapOf(
        "arguments" to listOf("number", "number"),
        "pure" to true,
        "opcode" to 1.toByte(),
        "registersUsed" to 2,
        "registersAdded" to 1,
    ),
    "console.log" to mutableMapOf(
        "arguments" to listOf("string"),
        "pure" to false,
        "opcode" to 2.toByte(),
        "registersUsed" to 1,
        "registersAdded" to 0,
    ),
    "getCurrentTime" to mutableMapOf(
        "arguments" to listOf<String>(),
        "pure" to false,
        "opcode" to 3.toByte(),
        "registersUsed" to 0,
        "registersAdded" to 1,
    ),
    "store" to mutableMapOf(
        "arguments" to listOf("symbol", "any"),
        "pure" to false,
        "opcode" to 4.toByte(),
        "registersUsed" to 2,
        "registersAdded" to 0,
    ),
    "load" to mutableMapOf(
        "arguments" to listOf("symbol"),
        "pure" to false,
        "opcode" to 5.toByte(),
        "registersUsed" to 1,
        "registersAdded" to 1,
    ),
    "player.sendMessage" to mutableMapOf(
        "arguments" to listOf("string"),
        "pure" to false,
        "opcodeExtension" to 128.toShort(),
        "registersUsed" to 1,
        "registersAdded" to 0,
    ),
)

fun findOpcodeInRegistry(opcode: Int): MutableMap.MutableEntry<String, MutableMap<String, Any>>? {
    for(pair in commandRegistry) {
        if(opcode < 127) {
            if(pair.value["opcode"] != null && pair.value["opcode"] == opcode.toByte()) {
                return pair
            }
        } else {
            if(pair.value["opcodeExtension"] != null && pair.value["opcodeExtension"] == opcode.toShort()) {
                return pair
            }
        }
    }
    return null
}

val events = mapOf(
    "callable" to 0,
    "startup" to 1,
    "join" to 2,
    "quit" to 3,
    "command" to 4,
    "playerTick" to 5,
)