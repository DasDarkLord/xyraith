package parser

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
)

fun findOpcodeInRegistry(opcode: Byte): MutableMap.MutableEntry<String, MutableMap<String, Any>>? {
    for(pair in commandRegistry) {
        if(pair.value["opcode"] != null && pair.value["opcode"] == opcode) {
            return pair
        }
    }
    return null
}