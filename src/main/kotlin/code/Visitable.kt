package code

import code.instructions.*

val visitables: List<Visitable> = listOf(
    Log
)

interface Visitable {
    val code: Int
    val isExtension: Boolean
    fun visit(visitor: Interpreter)
}