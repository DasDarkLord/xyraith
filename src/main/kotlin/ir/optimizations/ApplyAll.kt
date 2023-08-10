package ir.optimizations

import ir.BasicBlock

fun applyAllTransformations(blocks: List<BasicBlock>): List<BasicBlock> {
    var output = blocks
    output = convertIntrinsics(output)
    output = reduceStrength(output)
    return output
}