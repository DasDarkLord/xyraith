package registry

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

    "pos" to mutableMapOf(
        "arguments" to listOf("number", "number", "number", "number", "number"),
        "opcode" to 6.toByte()
    ),
    "vec" to mutableMapOf(
        "arguments" to listOf("number", "number", "number"),
        "opcode" to 7.toByte()
    ),

    "store" to mutableMapOf(
        "arguments" to listOf("symbol", "any"),
        "opcode" to 4.toByte(),
    ),
    "load" to mutableMapOf(
        "arguments" to listOf("symbol"),
        "opcode" to 5.toByte(),
    ),
    "call" to mutableMapOf(
        "arguments" to listOf("symbol"),
        "opcode" to 8.toByte()
    ),

    "player.sendMessage" to mutableMapOf(
        "arguments" to listOf("string"),
        "opcodeExtension" to Extensions.Player.SENDMESSAGE
    ),
    "player.setSpawnpoint" to mutableMapOf(
        "arguments" to listOf("vec"),
        "opcodeExtension" to Extensions.Player.SETSPAWNPOINT
    ),
    "player.getLoc" to mutableMapOf(
        "arguments" to listOf<String>(),
        "opcodeExtension" to Extensions.Player.GETLOCATION
    ),
    "player.teleport" to mutableMapOf(
        "arguments" to listOf<String>(),
        "opcodeExtension" to Extensions.Player.GETLOCATION
    ),

    "loc.getX" to mutableMapOf(
        "arguments" to listOf("loc"),
        "opcodeExtension" to Extensions.Location.GETX
    ),
    "loc.getY" to mutableMapOf(
        "arguments" to listOf("loc"),
        "opcodeExtension" to Extensions.Location.GETY
    ),
    "loc.getZ" to mutableMapOf(
        "arguments" to listOf("loc"),
        "opcodeExtension" to Extensions.Location.GETZ
    ),
    "loc.getPitch" to mutableMapOf(
        "arguments" to listOf("loc"),
        "opcodeExtension" to Extensions.Location.GETPITCH
    ),
    "loc.getYaw" to mutableMapOf(
        "arguments" to listOf("loc"),
        "opcodeExtension" to Extensions.Location.GETYAW
    ),

    "loc.setX" to mutableMapOf(
        "arguments" to listOf("loc", "number"),
        "opcodeExtension" to Extensions.Location.SETX
    ),
    "loc.setY" to mutableMapOf(
        "arguments" to listOf("loc", "number"),
        "opcodeExtension" to Extensions.Location.SETY
    ),
    "loc.setZ" to mutableMapOf(
        "arguments" to listOf("loc", "number"),
        "opcodeExtension" to Extensions.Location.SETZ
    ),
    "loc.setPitch" to mutableMapOf(
        "arguments" to listOf("loc", "number"),
        "opcodeExtension" to Extensions.Location.SETPITCH
    ),
    "loc.setYaw" to mutableMapOf(
        "arguments" to listOf("loc", "number"),
        "opcodeExtension" to Extensions.Location.SETYAW
    ),

    "world.setBlock" to mutableMapOf(
        "arguments" to listOf("loc", "string"),
        "opcodeExtension" to Extensions.World.SETBLOCK
    ),
    "world.getBlock" to mutableMapOf(
        "arguments" to listOf("loc"),
        "opcodeExtension" to Extensions.World.GETBLOCK
    ),
    "number.random" to mutableMapOf(
        "arguments" to listOf("number", "number"),
        "opcodeExtension" to Extensions.Number.RANDOM
    )
)

fun findOpcodeInRegistry(opcode: Int, searchExtensions: Boolean): MutableMap.MutableEntry<String, MutableMap<String, Any>>? {
    for(pair in commandRegistry) {
        if(searchExtensions) {
            if(pair.value["opcodeExtension"] != null && pair.value["opcodeExtension"] == opcode.toShort()) {
                return pair
            }
        } else {
            if (pair.value["opcode"] != null && pair.value["opcode"] == opcode.toByte()) {
                return pair
            }
        }
    }
    return null
}