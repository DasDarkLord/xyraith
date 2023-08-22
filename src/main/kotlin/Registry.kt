val commandRegistry = mutableMapOf(
    "add" to mutableMapOf(
        "arguments" to listOf("number", "number"),
        "opcode" to 1.toByte(),
    ),
    "console.log" to mutableMapOf(
        "arguments" to listOf("string"),
        "opcode" to 2.toByte(),
    ),
    "getCurrentTime" to mutableMapOf(
        "arguments" to listOf<String>(),
        "opcode" to 3.toByte(),
    ),
    "store" to mutableMapOf(
        "arguments" to listOf("symbol", "any"),
        "opcode" to 4.toByte(),
    ),
    "load" to mutableMapOf(
        "arguments" to listOf("symbol"),
        "opcode" to 5.toByte(),
    ),

    "player.sendMessage" to mutableMapOf(
        "arguments" to listOf("string"),
        "opcodeExtension" to 1.toShort(),
    ),
    "player.setSpawnpoint" to mutableMapOf(
        "arguments" to listOf("number", "number", "number"),
        "opcodeExtension" to 2.toShort(),
    ),

    "world.setBlock" to mutableMapOf(
        "arguments" to listOf("loc", "string"),
        "opcodeExtension" to 1001.toShort(),
    ),

    "vec" to mutableMapOf(
        "arguments" to listOf("number", "number", "number"),
        "opcode" to 7.toByte()
    ),
    "pos" to mutableMapOf(
        "arguments" to listOf("number", "number", "number", "number", "number"),
        "opcode" to 6.toByte()
    )
)

fun findOpcodeInRegistry(opcode: Int): MutableMap.MutableEntry<String, MutableMap<String, Any>>? {
    for(pair in commandRegistry) {
        if(pair.value["opcode"] != null && pair.value["opcode"] == opcode.toByte()) {
            return pair
        }
        if(pair.value["opcodeExtension"] != null && pair.value["opcodeExtension"] == opcode.toShort()) {
            return pair
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