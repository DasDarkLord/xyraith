package code.instructions.minecraft.player

import code.Interpreter
import code.instructions.Visitable
import net.minestom.server.entity.Player
import parser.Value
import typechecker.ArgumentList
import typechecker.ArgumentType
import typechecker.NodeBuilder


object GiveItems : Visitable {
    override val code: Int get() = 1400
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.giveItems"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addPluralArgument(ArgumentType.ITEM, "Item(s) to give.")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Give a player items."

    override suspend fun visit(visitor: Interpreter) {
        for(x in 1..visitor.environment.argumentCount) {
            val item = visitor.environment.stack.popValue() as Value.Item
            for (target in visitor.environment.targets) {
                if (target is Player) {
                    target.inventory.addItemStack(item.itemStack)
                }
            }
        }
    }
}

/*
INVENTORY COMMANDS
 */
object HasItems : Visitable {
    override val code: Int get() = 1401
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.hasItems"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.ITEM, "Item to check if they have.")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.BOOL
    override val description: String
        get() = "Check if a player has an item."

    override suspend fun visit(visitor: Interpreter) {
        val item = visitor.environment.stack.popValue() as Value.Item
        for(target in visitor.environment.targets) {
            if(target is Player) {
                for(slot in 0..36) {
                    if(target.inventory.getItemStack(slot) == item.itemStack) {
                        visitor.environment.stack.pushValue(Value.Bool(true))
                        return
                    }
                }
            }
        }
        visitor.environment.stack.pushValue(Value.Bool(false))
    }
}

object HoldingItems : Visitable {
    override val code: Int get() = 1402
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.holdingItems"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.ITEM, "Item to check if they have.")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.BOOL
    override val description: String
        get() = "Check if a player has an item."

    override suspend fun visit(visitor: Interpreter) {
        val item = visitor.environment.stack.popValue() as Value.Item
        for(target in visitor.environment.targets) {
            if(target is Player) {
                if(target.inventory.getItemStack(target.heldSlot.toInt()) == item.itemStack) {
                    visitor.environment.stack.pushValue(Value.Bool(true))
                    return
                }
            }
        }
        visitor.environment.stack.pushValue(Value.Bool(false))
    }
}