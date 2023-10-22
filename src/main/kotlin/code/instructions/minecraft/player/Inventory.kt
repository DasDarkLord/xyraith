package code.instructions.minecraft.player

import code.Interpreter
import code.instructions.Visitable
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
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
    override val command: String get() = "player.hasItem"
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
    override val command: String get() = "player.holdingItem"
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

object HeldItem : Visitable {
    override val code: Int get() = 1403
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.getHeldItem"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.ITEM
    override val description: String
        get() = "Gets the item a player is holding"

    override suspend fun visit(visitor: Interpreter) {
        val target = visitor.environment.targets.firstOrNull()
        if(target is Player) {
            visitor.environment.stack.pushValue(Value.Item(target.inventory.getItemStack(target.heldSlot.toInt())))
            return
        }
        visitor.environment.stack.pushValue(Value.Item(ItemStack.AIR))
    }
}

object Helmet : Visitable {
    override val code: Int get() = 1404
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.getHelmet"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.ITEM
    override val description: String
        get() = "Gets the helmet a player is wearing"

    override suspend fun visit(visitor: Interpreter) {
        val target = visitor.environment.targets.firstOrNull()
        if(target is Player) {
            visitor.environment.stack.pushValue(Value.Item(target.helmet))
            return
        }
        visitor.environment.stack.pushValue(Value.Item(ItemStack.AIR))
    }
}

object Chestplate : Visitable {
    override val code: Int get() = 1405
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.getChestplate"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.ITEM
    override val description: String
        get() = "Gets the chestplate a player is wearing"

    override suspend fun visit(visitor: Interpreter) {
        val target = visitor.environment.targets.firstOrNull()
        if(target is Player) {
            visitor.environment.stack.pushValue(Value.Item(target.chestplate))
            return
        }
        visitor.environment.stack.pushValue(Value.Item(ItemStack.AIR))
    }
}

object Leggings : Visitable {
    override val code: Int get() = 1406
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.getLeggings"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.ITEM
    override val description: String
        get() = "Gets the leggings a player is wearing"

    override suspend fun visit(visitor: Interpreter) {
        val target = visitor.environment.targets.firstOrNull()
        if(target is Player) {
            visitor.environment.stack.pushValue(Value.Item(target.leggings))
            return
        }
        visitor.environment.stack.pushValue(Value.Item(ItemStack.AIR))
    }
}

object Boots : Visitable {
    override val code: Int get() = 1407
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.getBoots"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.ITEM
    override val description: String
        get() = "Gets the boots a player is wearing"

    override suspend fun visit(visitor: Interpreter) {
        val target = visitor.environment.targets.firstOrNull()
        if(target is Player) {
            visitor.environment.stack.pushValue(Value.Item(target.boots))
            return
        }
        visitor.environment.stack.pushValue(Value.Item(ItemStack.AIR))
    }
}