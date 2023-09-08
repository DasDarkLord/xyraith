package bytecode

import parser.Ast
import parser.Value

class Pretransformer(val ast: List<Ast.Event>) {
    val output = mutableListOf<Ast.Block>()

    fun transform(): List<Ast.Block> {
        for(event in ast) {
            transformBlock(event.code)
        }
        return output
    }
    fun transformBlock(block: Ast.Block) {
        for(command in block.nodes) {
            for(arg in command.arguments) {
                var iter = 0
                if(arg is Value.Block) {
                    iter++
                    command.arguments[iter] = Value.BasicBlockRef(output.size)
                    transformBlock(arg.value)
                }
            }
        }
        output.add(block)
    }
}