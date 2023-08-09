package ir.optimizations

import ir.Argument
import ir.BasicBlock
import ir.Node

fun convertIntrinsics(blocks: List<BasicBlock>): List<BasicBlock> {
    var output = listOf<BasicBlock>()
    output = applyLoadOpt(output)
    return output
}

    /*
    This part converts certain `add`, `sub`, `mul`, and `div` commands into `loadAnd<op>` commands.
    E.g
    %1 = load :x
    %2 = add :x, 5
    %3 = store :x, %2

    gets transformed into

    %2 = loadAndAdd :x, 5
    %3 = store :x, %2

     */
private fun applyLoadOpt(blocks: List<BasicBlock>): List<BasicBlock> {
    val output = mutableListOf<BasicBlock>()
    blocks.forEach { block ->
        val addedBlock = BasicBlock(block.id, mutableListOf())
        block.code.forEach { node ->
            val addedNode = Node(node.id, node.name, node.arguments)
            val map = mapOf(
                "add" to "loadAndAdd",
                "sub" to "loadAndSub",
                "mul" to "loadAndMul",
                "div" to "loadAndDiv",
            )
            map.forEach { entry ->
                if(addedNode.name == entry.key) {
                    addedNode.arguments.forEach { arg ->
                        if(arg is Argument.SSARef) {
                            block.code.forEach { otherNode ->
                                if(otherNode.name == "load" && otherNode.id == arg.value) {
                                    val removed = addedBlock.code.remove(otherNode)
                                    println("removed? $removed")
                                    val newArguments = mutableListOf<Argument>()
                                    addedNode.arguments.forEach {
                                        if(!(it is Argument.SSARef && it.value == otherNode.id)) {
                                            newArguments.add(it)
                                        } else {
                                            newArguments.add(otherNode.arguments[0])
                                        }
                                    }
                                    addedNode.arguments = newArguments
                                    addedNode.name = entry.value
                                }
                            }
                        }
                    }
                }
            }
            addedBlock.code.add(addedNode)
        }
        output.add(addedBlock)
    }
    return output
}