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

    // Entity.kt
    SpawnEntity,

    // Player.kt
    SendMessage, SendActionBar, SendTitle,
    SetHunger, GetHunger, SetSaturation, GetSaturation,
    SetGamemode, PlayerUsername,

    // Target.kt
    Teleport, TargetUUID,
    SetHealth, GetHealth, Heal, Damage,

    // Variables.kt
    FLocalStore, FLocalLoad, GlobalStore, GlobalLoad,
    TargetStore, Targetload,

    // ControlFlow.kt
    ForEach, If, Call, AsyncCall, GetParam, Return, Sleep,

    // Select.kr
    Select, ResetSelection,
)

interface Visitable {
    val code: Int
    val isExtension: Boolean
    val command: String
    val arguments: ArgumentList
    val description: String
    val returnType: ArgumentType
    suspend fun visit(visitor: Interpreter)
}