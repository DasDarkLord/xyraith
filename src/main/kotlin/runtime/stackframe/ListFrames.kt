package runtime.stackframe

class ListFrames<T> {
    private val inner: MutableList<MutableList<T>> = mutableListOf(mutableListOf())

    fun pushFrame(frame: MutableList<T> = mutableListOf()) {
        inner.add(frame)
    }

    fun popFrame() {
        inner.removeLast()
    }

    fun popValue(): T {
        return inner.last().removeLast()
    }

    fun getFrame(): MutableList<T> {
        return inner.last()
    }

    fun pushValue(item: T) {
        inner.last().add(item)
    }

    operator fun get(index: Int): T? {
        return inner.last().getOrNull(index)
    }

    fun frameSize(): Int {
        return inner.last().size
    }

    override fun toString(): String {
        return this.inner.toString()
    }
}