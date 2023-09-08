package code

interface Visitable {
    fun visit(visitor: Visitor)
}