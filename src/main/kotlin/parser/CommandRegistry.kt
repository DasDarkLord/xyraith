package parser

val commandRegistry = mutableMapOf(
    "console.log" to mutableMapOf(
        "arguments" to listOf("string"),
        "pure" to false,
    ),
    "loc" to mutableMapOf(
        "arguments" to listOf("number", "number", "number"),
        "pure" to true
    ),
    "item" to mutableMapOf(
        "arguments" to listOf("string"),
        "pure" to true,
    ),
    "add" to mutableMapOf(
        "arguments" to listOf("number", "number"),
        "pure" to true,
    ),
    "sub" to mutableMapOf(
        "arguments" to listOf("number", "number"),
        "pure" to true,
    ),
    "mul" to mutableMapOf(
        "arguments" to listOf("number", "number"),
        "pure" to true,
    ),
    "div" to mutableMapOf(
        "arguments" to listOf("number", "number"),
        "pure" to true,
    ),
    "mod" to mutableMapOf(
        "arguments" to listOf("number", "number"),
        "pure" to true,

    ),
    "shl" to mutableMapOf(
        "arguments" to listOf("number", "number"),
        "pure" to true,
    ),
    "shr" to mutableMapOf(
        "arguments" to listOf("number", "number"),
        "pure" to true,
    ),

    "loadAndAdd" to mutableMapOf(
        "arguments" to listOf("symbol", "number"),
        "pure" to false,
    ),
    "loadAndSub" to mutableMapOf(
        "arguments" to listOf("symbol", "number"),
        "pure" to false,
    ),
    "loadAndMul" to mutableMapOf(
        "arguments" to listOf("symbol", "number"),
        "pure" to false,
    ),
    "loadAndDiv" to mutableMapOf(
        "arguments" to listOf("symbol", "number"),
        "pure" to false,
    ),
    "loadAndMod" to mutableMapOf(
        "arguments" to listOf("symbol", "number"),
        "pure" to false,
    ),
    "loadAndShl" to mutableMapOf(
        "arguments" to listOf("symbol", "number"),
        "pure" to false,
    ),
    "loadAndShr" to mutableMapOf(
        "arguments" to listOf("symbol", "number"),
        "pure" to false,
    ),

    "store" to mutableMapOf(
        "arguments" to listOf("symbol", "any"),
        "pure" to false,
    ),
    "load" to mutableMapOf(
        "arguments" to listOf("symbol"),
        "pure" to false,
    ),
    "globals.store" to mutableMapOf(
        "arguments" to listOf("symbol", "any"),
        "pure" to false,
    ),
    "globals.load" to mutableMapOf(
        "arguments" to listOf("symbol"),
        "pure" to false,
    ),
    "player.store" to mutableMapOf(
        "arguments" to listOf("symbol", "any"),
        "pure" to false,
    ),
    "player.load" to mutableMapOf(
        "arguments" to listOf("symbol"),
        "pure" to false,
    ),
    "persistent.store" to mutableMapOf(
        "arguments" to listOf("symbol", "any"),
        "pure" to false,
    ),
    "persistent.load" to mutableMapOf(
        "arguments" to listOf("symbol"),
        "pure" to false,
    ),
)