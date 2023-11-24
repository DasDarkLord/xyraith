package lang.ir

import instructions.Visitable
import lang.parser.PathName
import registry.commandRegistry

fun IR.Module.deadStoreElimination(): IR.Module {
    for(block in this.blocks) {
        val usedLocals = mutableListOf<String>()
        val usedGlobals = mutableListOf<String>()
        for(command in block.commands) {
            if(command.name == PathName(mutableListOf("load"))) {
                usedLocals.add((command.arguments[0] as IR.Argument.Symbol).value)
            }
            if(command.name == PathName(mutableListOf("global", "load"))) {
                usedGlobals.add((command.arguments[0] as IR.Argument.Symbol).value)
            }
        }
        val markForRemoval = mutableListOf<IR.Command>()
        for(command in block.commands) {
            if(command.name == PathName(mutableListOf("store"))) {
                if(!usedLocals.contains((command.arguments[0] as IR.Argument.Symbol).value)) {
                    markForRemoval.add(command)
                }
            }
            if(command.name == PathName(mutableListOf("global", "store"))) {
                if(!usedGlobals.contains((command.arguments[0] as IR.Argument.Symbol).value)) {
                    markForRemoval.add(command)
                }
            }
        }

        markForRemoval.forEach {
            block.commands.remove(it);
        }
    }
    return this
}

fun IR.Module.deadFunctionElimination(): IR.Module {
    for(x in 1..10) {
        val usedFunctions = mutableListOf<String>()
        for(block in this.blocks) {
            val functionName = if(block.blockData is IR.BlockData.Function)
                block.blockData.functionName
            else
                ""
            for(command in block.commands) {
                if(command.name == PathName(mutableListOf("call"))) {
                    val arg = (command.arguments[0] as IR.Argument.Symbol).value
                    if(arg != functionName) usedFunctions.add(arg)
                }
                if(command.name == PathName(mutableListOf("struct", "init"))) {
                    val arg = ":__struct_init_" + (command.arguments[0] as IR.Argument.Symbol).value
                    if(arg != functionName) usedFunctions.add(arg)
                }
            }
        }
        val markForRemoval = mutableListOf<IR.BasicBlock>()
        for(block in this.blocks) {
            if(block.blockData is IR.BlockData.Function && !usedFunctions.contains(block.blockData.functionName)) {
                println("block data: ${block.blockData}")
                markForRemoval.add(block)
            }
        }

        for(it in markForRemoval) {
            this.blocks.remove(it)
        }
    }

    return this
}

fun IR.Module.deadCallElimination(): IR.Module {
    for(x in 1..10) {
        val usedSSAs = mutableListOf<Int>()
        for(block in this.blocks) {
            for(command in block.commands) {
                for(argument in command.arguments) {
                    if(argument is IR.Argument.SSARef) {
                        usedSSAs.add(argument.value)
                    }
                }
            }
        }

        for(block in this.blocks) {
            val markForRemoval = mutableListOf<IR.Command>()
            for(command in block.commands) {
                // TODO: fix typechecker errors from kotlin
//                if(!usedSSAs.contains(command.id) && ((commandRegistry[command.name]!!["object"]!! as Visitable).pure)) {
//                    markForRemoval.add(command)
//                }
            }
            for(command in markForRemoval) {
                block.commands.remove(command)
            }
        }
    }
    return this
}