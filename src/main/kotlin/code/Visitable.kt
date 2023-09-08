package code

import code.instructions.*

val visitables: List<Visitable> = listOf(
    Log, Add, Sub, Mul, Div
)

interface Visitable {
    val code: Int
    val isExtension: Boolean
    val command: String
    val arguments: List<String>
    fun visit(visitor: Interpreter)
}