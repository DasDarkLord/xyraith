package ir

import parser.Ast
import parser.Value

class Translation {
    // ID for SSA numbering
    var ssaId = 0
    // ID for basic block numbering
    var bbId = 0
    var output = listOf<BasicBlock>()
    var block = BasicBlock(-1, mutableListOf(), "callable", "event")
    var blockList = mutableListOf<BasicBlock>()
    fun translateAST(events: List<Ast.Event>): List<BasicBlock> {
        events.forEach {
            translateBlock(it.code, it.type)
        }
        return blockList
    }

    fun translateBlock(astBlock: Ast.Block, type: String) {
        val tempBlock = block
        block = BasicBlock(bbId++, mutableListOf(), astBlock.eventName, type)
        astBlock.nodes.forEach {
            translateCommand(it)
        }
        blockList.add(block)
        block = tempBlock
    }

    fun translateCommand(command: Ast.Command) {
        val args = mutableListOf<Argument>()
        command.arguments.forEach {
            args.add(parseArgument(it))
        }
        val node = Node(ssaId++, command.name, args)
        block.code.add(node)
    }

    fun parseArgument(value: Value): Argument {
        when(value) {
            is Value.Command -> {
                translateCommand(value.value)
                return Argument.SSARef(ssaId - 1)
            }
            is Value.Block -> {
                translateBlock(value.value, "callable")
                return Argument.BasicBlockRef(bbId - 1)
            }
            is Value.String -> {
                return Argument.String(value.value)
            }
            is Value.Number -> {
                return Argument.Number(value.value)
            }
            is Value.Symbol -> {
                return Argument.Symbol(value.value)
            }
            is Value.Selector -> {
                return Argument.Selector(value.value)
            }
        }
    }
}