package ir

import error.Unreachable
import events
import parser.Ast
import parser.EventType
import parser.Value

var basicBlockId = 0
var ssaId = 0

val headers = mutableListOf<IR.BasicBlock>()
var currentBlock = mutableListOf<IR.Command>()
fun transformAst(ast: List<Ast.Event>): IR.Module {
    headers.clear()
    for(event in ast) {
        transformBlock(event.code, event)
    }
    return IR.Module(headers)
}

fun transformBlock(block: Ast.Block, eventData: Ast.Event?): IR.BasicBlock {
    val commands = mutableListOf<IR.Command>()
    val prev = currentBlock
    currentBlock = mutableListOf()
    for(command in block.nodes) {
        commands.add(transformCommand(command))
    }
    if(eventData == null) {
        currentBlock = prev
        headers.add(
            IR.BasicBlock(
                ++basicBlockId,
                commands,
                IR.BlockData.Event(events["callable"]!!)
            )
        )
        return IR.BasicBlock(
            basicBlockId,
            commands,
            IR.BlockData.Event(events["callable"]!!)
        )

    }
    val data: IR.BlockData = when(eventData.eventType) {
        is EventType.Struct -> IR.BlockData.Function(eventData.name)
        is EventType.Function -> IR.BlockData.Function(eventData.name)
        is EventType.Event -> IR.BlockData.Event(events[eventData.name]!!)
    }
    headers.add(
        IR.BasicBlock(
            ++basicBlockId,
            currentBlock,
            data
        )
    )
    val rt = IR.BasicBlock(
        basicBlockId,
        currentBlock,
        data
    )
    currentBlock = prev
    return rt
}

fun transformCommand(command: Ast.Command): IR.Command {
    val prev = currentBlock
    val arguments = command.arguments.map { transformArgument(it) }
    currentBlock = prev
    currentBlock.add(IR.Command(++ssaId, command.name, arguments))
    return IR.Command(ssaId, command.name, arguments)
}

fun transformArgument(argument: Value): IR.Argument {
    val prev = currentBlock
    val rt = when(argument) {
        is Value.String -> IR.Argument.String(argument.value)
        is Value.Number -> IR.Argument.Number(argument.value)
        is Value.Block -> { IR.Argument.BlockRef(transformBlock(argument.value, null).id) }
        is Value.Command -> IR.Argument.SSARef(transformCommand(argument.value).id)
        is Value.Symbol -> IR.Argument.Symbol(argument.value)
        else -> throw Unreachable()
    }
    currentBlock = prev
    return rt
}