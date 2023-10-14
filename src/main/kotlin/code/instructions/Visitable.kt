package code.instructions

import code.Interpreter
import code.instructions.io.Log
import code.instructions.minecraft.*
import code.instructions.primitives.*
import typechecker.ArgumentList
import typechecker.ArgumentType

val visitables: kotlin.collections.List<Visitable> = listOf(
    // Console.kt
    Log,

    // Math.kt
    Add, Sub, Mul, Div, Mod,
    Random, Range,
    Perlin,
    GreaterThan, LessThan, GreaterThanOrEqual, LessThanOrEqual, EqualTo,

    // Datatypes.kt
    Loc, Item, True, False, StringCmd,
    IsNull, ListCmd,

    // World.kt
    SetBlock,
    LoadAnvilWorld,

    // Entity.kt
    SpawnEntity,

    // Player.kt
    SendMessage, SendActionBar, SendTitle,
    SetHunger, GetHunger, SetSaturation, GetSaturation,
    SetGamemode, PlayerUsername,
    GiveItems, HasItems,

    // Target.kt
    Teleport, TargetUUID,
    SetHealth, GetHealth, Heal, Damage,

    // Variables.kt
    FLocalStore, FLocalLoad, GlobalStore, GlobalLoad,
    TargetStore, Targetload,

    // ControlFlow.kt
    ForEach, If, Call, AsyncCall, GetParam, Return, Sleep,
    Loop,

    // Select.kr
    Select, ResetSelection,

    // Struct.kt
    StructInit, StructField, StructGet, StructSet,

    // Item.kt
    SetItemName, SetItemLore,

    // Event.kt
    EventChatSetFormat, EventChatGetMessage,
    EventSetCancelled,
    EventBlockGetBlock, EventGetLocation,
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