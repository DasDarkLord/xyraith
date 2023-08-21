package server

import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.event.player.PlayerCommandEvent
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.event.player.PlayerTickEvent
import net.minestom.server.instance.block.Block
import java.nio.ByteBuffer

fun startupServer() {
    val minecraftServer = MinecraftServer.init()

    val instanceManager = MinecraftServer.getInstanceManager();
    // Create the instance
    val instanceContainer = instanceManager.createInstanceContainer();
    // Set the ChunkGenerator
    instanceContainer.setGenerator {unit -> unit.modifier().fillHeight(0, 1, Block.AIR) }
    // Add an event callback to specify the spawning instance (and the spawn position)
    val globalEventHandler = MinecraftServer.getGlobalEventHandler()
    globalEventHandler.addListener(PlayerLoginEvent::class.java) { event: PlayerLoginEvent ->
        val player = event.player
        event.setSpawningInstance(instanceContainer)
        player.respawnPoint = Pos(0.0, 42.0, 0.0)
        val interpreter = Interpreter(ByteBuffer.allocate(0))
        playerExtension(player, interpreter)
        interpreter.interpretEvent(2)
    }
    globalEventHandler.addListener(PlayerDisconnectEvent::class.java) { event ->
        val player = event.player

        val interpreter = Interpreter(ByteBuffer.allocate(0))
        playerExtension(player, interpreter)
        interpreter.interpretEvent(3)
    }
    globalEventHandler.addListener(PlayerCommandEvent::class.java) { event: PlayerCommandEvent ->
        val player = event.player

        val interpreter = Interpreter(ByteBuffer.allocate(0))
        playerExtension(player, interpreter)
        interpreter.interpretEvent(4)
    }
    globalEventHandler.addListener(PlayerTickEvent::class.java) { event ->
        val player = event.player
        val interpreter = Interpreter(ByteBuffer.allocate(0))
        playerExtension(player, interpreter)
        interpreter.interpretEvent(5)
    }

    val interpreter = Interpreter(ByteBuffer.allocate(0))
    interpreter.interpretEvent(1)

    minecraftServer.start("0.0.0.0", 25565)
}