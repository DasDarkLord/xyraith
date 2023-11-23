package lang.parser

data class PathName(val path: MutableList<String>) {
    companion object {
        fun parse(path: String): PathName {
            return PathName(path.split(".").toMutableList())
        }
    }
    fun resolve(): String {
        return path.joinToString { "." }
    }


}