package code

import code.instructions.*
import parser.ArgumentList

val visitables: List<Visitable> = listOf(
    Log,

    Add,

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