package code.instructions.minecraft.player

import code.Interpreter
import code.instructions.Visitable
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.instance.block.Block
import net.minestom.server.network.packet.server.play.BlockChangePacket
import net.minestom.server.network.packet.server.play.TimeUpdatePacket
import net.minestom.server.particle.Particle
import net.minestom.server.particle.ParticleCreator
import typechecker.ArgumentList
import typechecker.ArgumentType
import typechecker.NodeBuilder

object PlayRawParticle : Visitable {
    override val code: Int get() = 1500
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.playRawParticle"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.STRING, "Particle to display.")
            .addSingleArgument(ArgumentType.NUMBER, "X of Particle")
            .addSingleArgument(ArgumentType.NUMBER, "Y of Particle")
            .addSingleArgument(ArgumentType.NUMBER, "Z of Particle")
            .addSingleArgument(ArgumentType.NUMBER, "Offset X of Particle")
            .addSingleArgument(ArgumentType.NUMBER, "Offset Y of Particle")
            .addSingleArgument(ArgumentType.NUMBER, "Offset Z of Particle")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Display a particle to a player through packets."

    override suspend fun visit(visitor: Interpreter) {
        val fields = listOf(
            visitor.environment.stack.popValue().castToNumber(),
            visitor.environment.stack.popValue().castToNumber(),
            visitor.environment.stack.popValue().castToNumber(),
            visitor.environment.stack.popValue().castToNumber(),
            visitor.environment.stack.popValue().castToNumber(),
            visitor.environment.stack.popValue().castToNumber(),
        ).reversed()
        val particleName = visitor.environment.stack.popValue().castToString()

        val packet = ParticleCreator.createParticlePacket(
            Particle.fromNamespaceId(particleName)!!,
            fields[0],
            fields[1],
            fields[2],
            fields[3].toFloat(),
            fields[4].toFloat(),
            fields[5].toFloat(),
            0
        )
        for(target in visitor.environment.targets) {
            if(target is Player) {
                target.sendPacket(packet)
            }
        }
    }
}

object PlayRawDisplayBlock : Visitable {
    override val code: Int get() = 1501
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.playRawClientBlock"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.STRING, "Block ID to display.")
            .addSingleArgument(ArgumentType.NUMBER, "X of block")
            .addSingleArgument(ArgumentType.NUMBER, "Y of block")
            .addSingleArgument(ArgumentType.NUMBER, "Z of block")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Display a client-side block to a player through packets."

    override suspend fun visit(visitor: Interpreter) {
        val fields = listOf(
            visitor.environment.stack.popValue().castToNumber(),
            visitor.environment.stack.popValue().castToNumber(),
            visitor.environment.stack.popValue().castToNumber(),
        ).reversed()
        val id = visitor.environment.stack.popValue().castToString()


        for(target in visitor.environment.targets) {
            if(target is Player) {
                target.sendPacket(BlockChangePacket(
                    Pos(
                        fields[0],
                        fields[1],
                        fields[2]
                    ),
                    Block.fromNamespaceId(id)!!
                ))
            }
        }
    }
}


object PlayUpdateTime : Visitable {
    override val code: Int get() = 1502
    override val isExtension: Boolean get() = true
    override val command: String get() = "player.updateTime"
    override val arguments: ArgumentList
        get() = NodeBuilder()
            .addSingleArgument(ArgumentType.NUMBER, "New time to set")
            .build()
    override val returnType: ArgumentType
        get() = ArgumentType.NONE
    override val description: String
        get() = "Set a players' time on their client."

    override suspend fun visit(visitor: Interpreter) {
        val time = visitor.environment.stack.popValue().castToNumber()


        for(target in visitor.environment.targets) {
            if(target is Player) {
                target.sendPacket(TimeUpdatePacket(0, time.toLong()))
            }
        }
    }
}