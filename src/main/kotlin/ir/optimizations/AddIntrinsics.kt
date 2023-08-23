package ir.optimizations

import ir.Argument
import ir.BasicBlock
import ir.Node

fun convertIntrinsics(blocks: List<BasicBlock>): List<BasicBlock> {
    val output = applyLoadOpt(blocks)
    return output
}

     /**
      * This part converts certain `add`, `sub`, `mul`, and `div` commands into `loadAnd<op>` commands.
      *
      * for example:
      *
      * %1 = load :x
      *
      * %2 = add :x, 5
      *
      * %3 = store :x, %2
      *
      * gets transformed into:
      *
      * %2 = loadAndAdd :x, 5
      *
      * %3 = store :x, %2
      *
      * @param blocks a List<BasicBlock> to optimize.
      * @return a List<BasicBlock> with the modifications.
     */
private fun applyLoadOpt(blocks: List<BasicBlock>): List<BasicBlock> {
    val output = mutableListOf<BasicBlock>()
    blocks.forEach { block ->
        val addedBlock = BasicBlock(block.id, mutableListOf(), block.eventId, block.eventType)
        block.code.forEach { node ->
            val addedNode = Node(node.id, node.name, node.arguments)
            val map = mapOf(
                "add" to "loadAndAdd",
                "sub" to "loadAndSub",
                "mul" to "loadAndMul",
                "div" to "loadAndDiv",
                "+" to "loadAndAdd",
                "-" to "loadAndSub",
                "*" to "loadAndMul",
                "/" to "loadAndDiv",
            )
            map.forEach { entry ->
                if (addedNode.name == entry.key) {
                    addedNode.arguments.forEach { arg ->
                        if (arg is Argument.SSARef) {
                            block.code.forEach { otherNode ->
                                if (otherNode.name == "load" && otherNode.id == arg.value) {
                                    addedBlock.code.remove(otherNode)
                                    val newArguments = mutableListOf<Argument>()
                                    addedNode.arguments.forEach {
                                        if (!(it is Argument.SSARef && it.value == otherNode.id)) {
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

