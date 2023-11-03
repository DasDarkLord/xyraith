package ir

sealed class IR {
    data class Command(val name: String, val arguments: List<IR.Argument>) : IR()
    public sealed class Argument : IR() {
        data class Number(val value: Double) : Argument()
        data class String(val value: kotlin.String) : Argument()
        data class SSARef(val value: Int) : Argument()
        data class BlockRef(val value: Int) : Argument()
    }
}