package parser

val commandRegistry = mutableMapOf(
    "add" to mutableMapOf(
        "arguments" to listOf("number", "number"),
        "pure" to true,
        "opcode" to 1.toByte(),
        "registersUsed" to 2,
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