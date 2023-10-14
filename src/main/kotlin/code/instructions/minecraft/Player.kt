package code.instructions.minecraft

import code.Interpreter
import code.instructions.Visitable
import mm
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.network.packet.server.play.SetTitleSubTitlePacket
import net.minestom.server.network.packet.server.play.SetTitleTextPacket
import net.minestom.server.network.packet.server.play.SetTitleTimePacket
import net.minestom.server.particle.Particle
import net.minestom.server.particle.ParticleCreator
import typechecker.ArgumentList
import typechecker.ArgumentType
import typechecker.NodeBuilder
import parser.Value

object SendMessage : Visitable {
    override val code: Int get() = 1000
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.sendMessage"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.STRING, "Message to send")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Send the player a message in chat."

    override suspend fun visit(visitor: Interpreter) {
        val display = visitor.environment.stack.popValue().toDisplay()
        val component = mm(display)
        for(target in visitor.environment.targets) {
            if(target as? Player != null) {
                target.sendMessage(component)
            }
        }
    }
}

object SendActionBar : Visitable {
    override val code: Int get() = 1001
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.sendActionBar"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.STRING, "Actionbar to send")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Send a player a message in the actionbar."

    override suspend fun visit(visitor: Interpreter) {
        val display = visitor.environment.stack.popValue().toDisplay()
        for(target in visitor.environment.targets) {
            if(target as? Player != null) {
                target.sendActionBar(mm(display))
            }
        }
    }
}

object UnsafeSendTitleText : Visitable {
    override val code: Int get() = 1002
    override val isExtension: Boolean get() = true
    override val command: String get() = "unsafe.player.sendTitleText"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.STRING, "Title to send")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Send a player a message through a title. Sends the main title"

    override suspend fun visit(visitor: Interpreter) {
        val title = visitor.environment.stack.popValue().toDisplay()
        val comp = mm(title)
        visitor.environment.targets.forEach { if(it is Player) it.sendPacket(SetTitleTextPacket(comp)) }
    }
}

object UnsafeSendSubtitleText : Visitable {
    override val code: Int get() = 1003
    override val isExtension: Boolean get() = true
    override val command: String get() = "unsafe.player.sendSubtitleText"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.STRING, "Subtitle to send")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Send a player a message through a title. Sends the main subtitle."

    override suspend fun visit(visitor: Interpreter) {
        val title = visitor.environment.stack.popValue().toDisplay()
        val comp = mm(title)
        visitor.environment.targets.forEach { if(it is Player) it.sendPacket(SetTitleSubTitlePacket(comp)) }
    }
}

object UnsafeSendTitleTimes : Visitable {
    override val code: Int get() = 1003
    override val isExtension: Boolean get() = true
    override val command: String get() = "unsafe.player.sendTitleTimes"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "Fade in (in ticks)")
            .addSingleArgument(ArgumentType.NUMBER, "Stay time (in ticks)")
            .addSingleArgument(ArgumentType.NUMBER, "Fade out (in ticks)")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Sets a players' title times."

    override suspend fun visit(visitor: Interpreter) {
        val fadeStay = visitor.environment.stack.popValue().castToNumber()
        val fadeIn = visitor.environment.stack.popValue().castToNumber()
        val fadeOut = visitor.environment.stack.popValue().castToNumber()
        visitor.environment.targets.forEach { if(it is Player) it.sendPacket(SetTitleTimePacket(fadeIn.toInt(), fadeStay.toInt(), fadeOut.toInt())) }
    }
}

object SetHunger : Visitable {
    override val code: Int get() = 1102
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.setHunger"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "Points to set hunger to")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Set a player's hunger points."

    override suspend fun visit(visitor: Interpreter) {
        val food = visitor.environment.stack.popValue().castToNumber()
        for(target in visitor.environment.targets) {
            if(target as? Player != null) {
                target.food = food.toInt()
            }
        }
    }
}

object GetHunger : Visitable {
    override val code: Int get() = 1105
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.getSaturation"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NUMBER
    override val description: String
        get() = "Get a player's hunger points."

    override suspend fun visit(visitor: Interpreter) {
        for(target in visitor.environment.targets) {
            if(target as? Player != null) {
                visitor.environment.stack.pushValue(Value.Number(target.food.toDouble()))
            }
        }
    }
}

object SetSaturation : Visitable {
    override val code: Int get() = 1104
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.setSaturation"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "Points to set saturation to")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Set a player's saturation points."

    override suspend fun visit(visitor: Interpreter) {
        val food = visitor.environment.stack.popValue().castToNumber()
        for(target in visitor.environment.targets) {
            if(target as? Player != null) {
                target.foodSaturation = food.toFloat()
            }
        }
    }
}

object GetSaturation : Visitable {
    override val code: Int get() = 1106
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.getSaturation"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NUMBER
    override val description: String
        get() = "Get a player's saturation points."

    override suspend fun visit(visitor: Interpreter) {
        for(target in visitor.environment.targets) {
            if(target as? Player != null) {
                visitor.environment.stack.pushValue(Value.Number(target.foodSaturation.toDouble()))
            }
        }
    }
}

object PlayerUsername : Visitable {
    override val code: Int get() = 1109
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.username"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.STRING
    override val description: String
        get() = "Get the username of a player"

    override suspend fun visit(visitor: Interpreter) {
        val target = visitor.environment.targets.firstOrNull()
        if(target as? Player != null) {
            visitor.environment.stack.pushValue(Value.String(target.username))
        } else {
            visitor.environment.stack.pushValue(Value.Null)
        }
    }
}

object SetGamemode : Visitable {
    override val code: Int get() = 1300
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.gamemode"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.STRING, "Game mode to change to.")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Change a player's game mode."

    override suspend fun visit(visitor: Interpreter) {
        val mode = visitor.environment.stack.popValue().castToString()
        if(mode == "gmc" || mode == "c" || mode == "creative") {
            for(target in visitor.environment.targets) {
                if(target is Player) {
                    target.gameMode = GameMode.CREATIVE
                }
            }
        }
        if(mode == "gms" || mode == "s" || mode == "survival") {
            for(target in visitor.environment.targets) {
                if(target is Player) {
                    target.gameMode = GameMode.SURVIVAL
                }
            }
        }
        if(mode == "gma" || mode == "a" || mode == "adventure") {
            for(target in visitor.environment.targets) {
                if(target is Player) {
                    target.gameMode = GameMode.ADVENTURE
                }
            }
        }
        if(mode == "gmsp" || mode == "sp" || mode == "spectator") {
            for(target in visitor.environment.targets) {
                if(target is Player) {
                    target.gameMode = GameMode.SPECTATOR
                }
            }
        }
    }
}

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

object UnsafePlayParticle : Visitable {
    override val code: Int get() = 1500
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.playParticle"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType(":particle", listOf()), "Particle to display.")
            .addSingleArgument(ArgumentType.LOCATION, "Location to render")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Display a particle to a player."

    override suspend fun visit(visitor: Interpreter) {
        val loc = visitor.environment.stack.popValue() as Value.Struct
        val particle = visitor.environment.stack.popValue() as Value.Struct

        val packet = ParticleCreator.createParticlePacket(
            Particle.fromNamespaceId((particle.fields[":id"] as Value.String).value)!!,
            loc.fields[":x"]!!.castToNumber(),
            loc.fields[":y"]!!.castToNumber(),
            loc.fields[":z"]!!.castToNumber(),
            0.0f,
            0.0f,
            0.0f,
            0
        )
        for(target in visitor.environment.targets) {
            if(target is Player) {
                target.sendPacket(packet)
            }
        }
    }
}