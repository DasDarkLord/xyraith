object Extensions {
    object Player {
        const val SENDMESSAGE = 1.toShort()
        const val SETSPAWNPOINT = 10.toShort()
        const val GETLOCATION = 20.toShort()
        const val TELEPORT = 21.toShort()
    }
    object World {
        const val SETBLOCK = 1001.toShort()
        const val GETBLOCK = 1001.toShort()
    }
    object Location {
        const val GETX = 2001.toShort()
        const val GETY = 2002.toShort()
        const val GETZ = 2003.toShort()
        const val GETPITCH = 2004.toShort()
        const val GETYAW = 2005.toShort()
        const val SETX = 2006.toShort()
        const val SETY = 2007.toShort()
        const val SETZ = 2008.toShort()
        const val SETPITCH = 2009.toShort()
        const val SETYAW = 2010.toShort()
    }


}


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

    "player.sendMessage" to mutableMapOf(
        "arguments" to listOf("string"),
        "opcodeExtension" to Extensions.Player.SENDMESSAGE
    ),
    "player.setSpawnpoint" to mutableMapOf(
        "arguments" to listOf("vec"),
        "opcodeExtension" to Extensions.Player.SETSPAWNPOINT
    ),
    "player.getLocation" to mutableMapOf(
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

    "world.setBlock" to mutableMapOf(
        "arguments" to listOf("loc", "string"),
        "opcodeExtension" to Extensions.World.SETBLOCK
    ),
    "world.getBlock" to mutableMapOf(
        "arguments" to listOf("loc"),
        "opcodeExtension" to Extensions.World.GETBLOCK
    ),



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