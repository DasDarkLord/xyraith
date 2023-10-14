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

/**
 * Visitable is an interface that allows you to create a Xyraith command.
 * In order to make one, create an `object` that inherits this interface
 * and put it in the list above.
 */
interface Visitable {
    /**
     * The opcode of the command.
     */
    val code: Int

    /**
     * Determines whether the command is an extension opcode.
     * If the opcode is an extension, mark it as true.
     * If it is not, mark it as false and keep code within -127 to 127.
     */
    val isExtension: Boolean

    /**
     * The name of the command that users will call it by.
     * Unless you will make an API for it in the standard
     * library, give this a sensible name.
     */
    val command: String

    /**
     * A list of arguments the command can accept.
     * Automatically validated for you,
     * so you can do unsafe type casts in the `visit` function.
     */
    val arguments: ArgumentList

    /**
     * Description of the command. This needs to describe what the command does.
     */
    val description: String

    /**
     * The return type of the command. If there is a special case for it's return
     * type, add a case in `Typechecker` and mark this as `ANY`.
     */
    val returnType: ArgumentType

    /**
     * The code to run when the interpreter comes across your command.
     */
    suspend fun visit(visitor: Interpreter)
}