package code

import code.instructions.*
import parser.ArgumentList
import parser.ArgumentType

val visitables: List<Visitable> = listOf(
    // Console.kt
    Log,

    // Math.kt
    Add, Sub, Mul, Div, Mod,
    Random, Range,
    Perlin,
    GreaterThan, LessThan, GreaterThanOrEqual, LessThanOrEqual,

    // Datatypes.kt
    Loc, Item, True, False, StringCmd,

    // World.kt
    SetBlock,
    SetChatFormat,
    LoadAnvilWorld,

    // Player.kt
    SendMessage, SendActionBar, SendTitle,
    SetHealth, GetHealth, SetHunger, GetHunger, SetSaturation, GetSaturation, Heal, Damage,
    Teleport,
    SetGamemode,

    // Variables.kt
    FLocalStore, FLocalLoad,

    // ControlFlow.kt
    ForEach, If,
)

interface Visitable {
    val code: Int
    val isExtension: Boolean
    val command: String
    val arguments: ArgumentList
    val description: String
    val returnType: ArgumentType
    fun visit(visitor: Interpreter)
}