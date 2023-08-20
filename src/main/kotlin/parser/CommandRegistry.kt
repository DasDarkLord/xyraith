package parser

val commandRegistry = mutableMapOf(
    "console.log" to mutableMapOf(
        "arguments" to listOf("any"),
        "pure" to false,
        "opcode" to 50.toByte(),
        "registersUsed" to 1,
        "registersAdded" to 0,
    ),
    "loc" to mutableMapOf(
        "arguments" to listOf("number", "number", "number"),
        "pure" to true,
        "opcodeExtension" to 2000,
        "registersUsed" to 3,
        "registersAdded" to 1,
    ),
    "item" to mutableMapOf(
        "arguments" to listOf("string"),
        "pure" to true,
        "opcodeExtension" to 3000,
        "registersUsed" to 1,
        "registersAdded" to 1,
    ),
    "add" to mutableMapOf(
        "arguments" to listOf("number", "number"),
        "pure" to true,
        "opcode" to 1.toByte(),
        "registersUsed" to 2,
        "registersAdded" to 1,
    ),
    "sub" to mutableMapOf(
        "arguments" to listOf("number", "number"),
        "pure" to true,
        "opcode" to 2.toByte(),
        "registersUsed" to 2,
        "registersAdded" to 1,
    ),
    "mul" to mutableMapOf(
        "arguments" to listOf("number", "number"),
        "pure" to true,
        "opcode" to 3.toByte(),
        "registersUsed" to 2,
        "registersAdded" to 1,
    ),
    "div" to mutableMapOf(
        "arguments" to listOf("number", "number"),
        "pure" to true,
        "opcode" to 4.toByte(),
        "registersUsed" to 2,
        "registersAdded" to 1,
    ),
    "mod" to mutableMapOf(
        "arguments" to listOf("number", "number"),
        "pure" to true,
        "opcode" to 5.toByte(),
        "registersUsed" to 2,

    ),
    "shl" to mutableMapOf(
        "arguments" to listOf("number", "number"),
        "pure" to true,
        "opcode" to 6.toByte(),
        "registersUsed" to 2,
    ),
    "shr" to mutableMapOf(
        "arguments" to listOf("number", "number"),
        "pure" to true,
        "opcode" to 7.toByte(),
        "registersUsed" to 2,
    ),

    "loadAndAdd" to mutableMapOf(
        "arguments" to listOf("symbol", "number"),
        "pure" to false,
        "opcode" to 16.toByte(),
        "registersUsed" to 2,
    ),
    "loadAndSub" to mutableMapOf(
        "arguments" to listOf("symbol", "number"),
        "pure" to false,
        "opcode" to 17.toByte(),
        "registersUsed" to 2,
    ),
    "loadAndMul" to mutableMapOf(
        "arguments" to listOf("symbol", "number"),
        "pure" to false,
        "opcode" to 18.toByte(),
        "registersUsed" to 2,
    ),
    "loadAndDiv" to mutableMapOf(
        "arguments" to listOf("symbol", "number"),
        "pure" to false,
        "opcode" to 19.toByte(),
        "registersUsed" to 2,
    ),
    "loadAndMod" to mutableMapOf(
        "arguments" to listOf("symbol", "number"),
        "pure" to false,
        "opcode" to 20.toByte(),
        "registersUsed" to 2,
    ),
    "loadAndShl" to mutableMapOf(
        "arguments" to listOf("symbol", "number"),
        "pure" to false,
        "opcode" to 21.toByte(),
        "registersUsed" to 2,
    ),
    "loadAndShr" to mutableMapOf(
        "arguments" to listOf("symbol", "number"),
        "pure" to false,
        "opcode" to 22.toByte(),
        "registersUsed" to 2,
    ),

    "store" to mutableMapOf(
        "arguments" to listOf("symbol", "any"),
        "pure" to false,
        "opcode" to 8.toByte(),
        "registersUsed" to 2,
    ),
    "load" to mutableMapOf(
        "arguments" to listOf("symbol"),
        "pure" to false,
        "opcode" to 9.toByte(),
        "registersUsed" to 1,
    ),
    "globals.store" to mutableMapOf(
        "arguments" to listOf("symbol", "any"),
        "pure" to false,
        "opcode" to 10.toByte(),
        "registersUsed" to 2,
    ),
    "globals.load" to mutableMapOf(
        "arguments" to listOf("symbol"),
        "pure" to false,
        "opcode" to 11.toByte(),
        "registersUsed" to 1,
    ),
    "player.store" to mutableMapOf(
        "arguments" to listOf("symbol", "any"),
        "pure" to false,
        "opcode" to 12.toByte(),
        "registersUsed" to 2,
    ),
    "player.load" to mutableMapOf(
        "arguments" to listOf("symbol"),
        "pure" to false,
        "opcode" to 13.toByte(),
        "registersUsed" to 1,
    ),
    "persistent.store" to mutableMapOf(
        "arguments" to listOf("symbol", "any"),
        "pure" to false,
        "opcode" to 14.toByte(),
        "registersUsed" to 2,
    ),
    "persistent.load" to mutableMapOf(
        "arguments" to listOf("symbol"),
        "pure" to false,
        "opcode" to 15.toByte(),
        "registersUsed" to 1,
    ),
    "player.sendMessage" to mutableMapOf(
        "arguments" to listOf("selector", "string"),
        "pure" to false,
        "opcodeExtension" to 1000,
        "registersUsed" to 2,
    )
)

fun findOpcodeInRegistry(opcode: Byte): MutableMap.MutableEntry<String, MutableMap<String, Any>>? {
    for(pair in commandRegistry) {
        if(pair.value["opcode"] != null && pair.value["opcode"] == opcode) {
            return pair
        }
    }
    return null
}