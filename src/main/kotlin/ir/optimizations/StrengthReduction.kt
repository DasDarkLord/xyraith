package ir.optimizations

import ir.Argument
import ir.BasicBlock
import ir.Node
import kotlin.math.log2
import kotlin.math.roundToInt

/**
 * This allows certain operations to go faster than others.
 * For example, multiplying by two is converted into bitshift-right by 1.
 * This is faster for the CPU to process than multiplication.
 *
 *@param blocks a List<BasicBlock> to optimize.
 *@return a List<BasicBlock> with the modifications.
 */
fun reduceStrength(blocks: List<BasicBlock>): List<BasicBlock> {
    val output = mutableListOf<BasicBlock>()
    blocks.forEach { block ->
        val addedBlock = BasicBlock(block.id, mutableListOf(), block.eventId, block.eventType)
        block.code.forEach { node ->
            fun localLoop() {
                var addedNode = node
                if(node.arguments.size != 2) {
                    addedBlock.code.add(addedNode)
                    return
                }
                val secondArgument = node.arguments[1]
                if(secondArgument !is Argument.Number) {
                    addedBlock.code.add(addedNode)
                    return
                }
                if(secondArgument.value.toInt().toDouble() != secondArgument.value) {
                    addedBlock.code.add(addedNode)
                    return
                }
                val rounded = secondArgument.value.roundToInt()
                // Check if it is a power of two
                if(rounded > 0 && (rounded and (rounded - 1)) == 0) {
                    val log = log2(rounded.toDouble())
                    if(addedNode.name == "mul") {
                        addedNode = Node(node.id, "shl", listOf(node.arguments[0], Argument.Number(log)))
                    }
                    if(addedNode.name == "loadAndMul") {
                        addedNode = Node(node.id, "loadAndShl", listOf(node.arguments[0], Argument.Number(log)))
                    }
                    if(addedNode.name == "div") {
                        addedNode = Node(node.id, "shr", listOf(node.arguments[0], Argument.Number(log)))
                    }
                    if(addedNode.name == "loadAndDiv") {
                        addedNode = Node(node.id, "loadAndShr", listOf(node.arguments[0], Argument.Number(log)))
                    }
                }
                addedBlock.code.add(addedNode)
            }
            localLoop()
        }
        output.add(addedBlock)
    }
    return output
}