package lang.ir

import lang.parser.PathName

sealed class IR {
    data class Module(val blocks: MutableList<BasicBlock>) : IR() {
        override fun toString(): String {
            return """$blocks"""
        }
    }
    data class BasicBlock(val id: Int, val commands: MutableList<Command>, val blockData: BlockData) : IR() {
        override fun toString(): String {
            return """   
bb$id $blockData =>
    ${commands.joinToString("\n\t")}"""
        }
    }
    sealed class BlockData : IR() {
        data class Event(val eventId: Int) : BlockData() {
            override fun toString(): String {
                return """(event $eventId)"""
            }
        }
        data class Function(val functionName: String) : BlockData() {
            override fun toString(): String {
                return """(function $functionName)"""
            }
        }
    }
    data class Command(val id: Int, val name: PathName, val arguments: MutableList<Argument>) : IR() {
        override fun toString(): String {
            return "%$id = ${name.resolve()} $arguments"
        }
    }
    public sealed class Argument : IR() {
        data class Number(val value: Double) : Argument() {
            override fun toString(): kotlin.String {
                return """$value"""
            }
        }
        data class String(val value: kotlin.String) : Argument() {
            override fun toString(): kotlin.String {
                return "\"$value\""
            }
        }
        data class SSARef(val value: Int) : Argument() {
            override fun toString(): kotlin.String {
                return """%$value"""
            }
        }
        data class BlockRef(val value: Int) : Argument() {
            override fun toString(): kotlin.String {
                return """bb$value"""
            }
        }
        data class Symbol(val value: kotlin.String) : Argument() {
            override fun toString(): kotlin.String {
                return value
            }
        }
    }
}