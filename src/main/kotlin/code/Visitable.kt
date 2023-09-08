package code

import code.instructions.*
import parser.ArgumentList

val visitables: List<Visitable> = listOf(
    Log, Add,
)

interface Visitable {
    val code: Int
    val isExtension: Boolean
    val command: String
    val arguments: ArgumentList
    fun visit(visitor: Interpreter)
}