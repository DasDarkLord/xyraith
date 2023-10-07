package code.instructions

import code.Interpreter
import typechecker.ArgumentList
import typechecker.ArgumentType

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
    EventChatSetFormat,
    LoadAnvilWorld,

    // Player.kt
    SendMessage, SendActionBar, SendTitle,
    SetHealth, GetHealth, SetHunger, GetHunger, SetSaturation, GetSaturation, Heal, Damage,
    Teleport,
    SetGamemode,

    // Variables.kt
    FLocalStore, FLocalLoad, GlobalStore, GlobalLoad,
    EntityStore, EntityLoad,

    // ControlFlow.kt
    ForEach, If, Call, GetParam, Return,
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