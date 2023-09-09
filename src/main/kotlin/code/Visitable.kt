package code

import code.instructions.*
import parser.ArgumentList

val visitables: List<Visitable> = listOf(
    // Console.kt
    Log,

    // Math.kt
    Add, Sub, Mul, Div, Mod,

    // Player.kt
    SendMessage, SendActionBar, SendTitle,
    SetHealth, GetHealth, SetHunger, GetHunger, SetSaturation, GetSaturation,
)

interface Visitable {
    val code: Int
    val isExtension: Boolean
    val command: String
    val arguments: ArgumentList
    fun visit(visitor: Interpreter)
}