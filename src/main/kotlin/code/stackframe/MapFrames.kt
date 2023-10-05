package code.stackframe

class MapFrames<K, V> {
    private val inner: MutableList<MutableMap<K, V>> = mutableListOf(mutableMapOf())

    fun pushFrame() {
        inner.add(mutableMapOf())
    }

    fun popFrame() {
        inner.removeLast()
    }

    operator fun get(item: K): V? {
        return inner.last()[item]
    }

    fun getFrame(): MutableMap<K, V> {
        return inner.last()
    }

    operator fun set(key: K, value: V) {
        inner.last()[key] = value
    }
}