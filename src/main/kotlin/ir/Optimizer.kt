package ir

fun IR.Module.dse(): IR.Module {
    for(block in this.blocks) {
        val usedLocals = mutableListOf<String>()
        val usedGlobals = mutableListOf<String>()
        for(command in block.commands) {
            if(command.name == "load") {
                usedLocals.add((command.arguments[0] as IR.Argument.Symbol).value)
            }
            if(command.name == "global.load") {
                usedGlobals.add((command.arguments[0] as IR.Argument.Symbol).value)
            }
        }
        val markForRemoval = mutableListOf<IR.Command>()
        for(command in block.commands) {
            if(command.name == "store") {
                if(!usedLocals.contains((command.arguments[0] as IR.Argument.Symbol).value)) {
                    markForRemoval.add(command)
                }
            }
            if(command.name == "global.store") {
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

fun IR.Module.dce(): IR.Module {
    for(x in 1..10) {
        val usedFunctions = mutableListOf<String>()
        for(block in this.blocks) {
            val functionName = if(block.blockData is IR.BlockData.Function)
                block.blockData.functionName
            else
                ""
            for(command in block.commands) {
                if(command.name == "call") {
                    val arg = (command.arguments[0] as IR.Argument.Symbol).value
                    if(arg != functionName) usedFunctions.add(arg)
                }
            }
        }
        val markForRemoval = mutableListOf<IR.BasicBlock>()
        for(block in this.blocks) {
            if(block.blockData is IR.BlockData.Function && !usedFunctions.contains(block.blockData.functionName)) {
                markForRemoval.add(block)
            }
        }

        for(it in markForRemoval) {
            this.blocks.remove(it)
        }
    }

    return this
}