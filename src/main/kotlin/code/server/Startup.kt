package code.server

import code.runEvent
import events
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.player.*
import net.minestom.server.extras.MojangAuth
import net.minestom.server.instance.Chunk
import net.minestom.server.instance.LightingChunk
import net.minestom.server.instance.block.Block
import net.minestom.server.utils.chunk.ChunkSupplier


fun startServer() {
    val server = MinecraftServer.init()
    MojangAuth.init()
    val instanceManager = MinecraftServer.getInstanceManager()
    val instanceContainer = instanceManager.createInstanceContainer()
    instanceContainer.setGenerator { unit -> unit.modifier().fillHeight(0, 40, Block.SANDSTONE) }
    val globalEventHandler = MinecraftServer.getGlobalEventHandler()
    runEvent(1, mutableListOf(), instanceContainer, null)
    globalEventHandler.addListener(PlayerLoginEvent::class.java) { event ->
        val player = event.player
        event.setSpawningInstance(instanceContainer)
        player.respawnPoint = Pos(0.0, 42.0, 0.0)
    }
    addEvents(globalEventHandler)
    server.start("0.0.0.0", 25565)
}

fun addEvents(globalEventHandler: GlobalEventHandler) {
    globalEventHandler.addListener(PlayerSpawnEvent::class.java) { event ->
        if(event.isFirstSpawn) {
            val player = event.player
            runEvent(events["join"]!!, mutableListOf(player), player.instance, event)
        }
    }
    globalEventHandler.addListener(PlayerDisconnectEvent::class.java) { event ->
        val player = event.player
        runEvent(events["quit"]!!, mutableListOf(player), player.instance, event)
    }
    globalEventHandler.addListener(PlayerCommandEvent::class.java) { event ->
        val player = event.player
        runEvent(events["command"]!!, mutableListOf(player), player.instance, event)
    }
    globalEventHandler.addListener(PlayerTickEvent::class.java) { event ->
        val player = event.player
        runEvent(events["tick"]!!, mutableListOf(player), player.instance, event)
    }
    globalEventHandler.addListener(PlayerUseItemEvent::class.java) { event ->
        val player = event.player
        if(event.hand == Player.Hand.MAIN) {
            runEvent(events["rightClick"]!!, mutableListOf(player), player.instance, event)
        }
    }
    globalEventHandler.addListener(PlayerBlockInteractEvent::class.java) { event ->
        if(event.hand == Player.Hand.MAIN) {
            runEvent(events["rightClick"]!!, mutableListOf(event.player), event.player.instance, event)
        }
    }
    globalEventHandler.addListener(PlayerChatEvent::class.java) { event ->
        val player = event.player
        runEvent(events["chat"]!!, mutableListOf(player), player.instance, event)
    }
    globalEventHandler.addListener(PlayerStartSneakingEvent::class.java) { event ->
        runEvent(events["sneak"]!!, mutableListOf(event.player), event.player.instance, event)
    }
    globalEventHandler.addListener(PlayerSwapItemEvent::class.java) { event ->
        runEvent(events["swapHands"]!!, mutableListOf(event.player), event.player.instance, event)
    }
    globalEventHandler.addListener(PlayerStartDiggingEvent::class.java) { event ->
        runEvent(events["leftClick"]!!, mutableListOf(event.player), event.player.instance, event)
    }
    globalEventHandler.addListener(PlayerGameModeChangeEvent::class.java) { event ->
        runEvent(events["gamemodeChange"]!!, mutableListOf(event.player), event.player.instance, event)
    }
    globalEventHandler.addListener(PlayerRespawnEvent::class.java) { event ->
        runEvent(events["respawn"]!!, mutableListOf(event.player), event.player.instance, event)
    }
}