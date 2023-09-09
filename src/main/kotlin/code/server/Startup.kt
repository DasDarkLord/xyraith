package code.server

import code.runEvent
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.event.player.*
import net.minestom.server.extras.MojangAuth
import net.minestom.server.instance.block.Block


fun startServer() {
    val server = MinecraftServer.init()
    MojangAuth.init()
    val instanceManager = MinecraftServer.getInstanceManager()
    val instanceContainer = instanceManager.createInstanceContainer()
    instanceContainer.setGenerator { unit -> unit.modifier().fillHeight(0, 40, Block.SANDSTONE) }
    val globalEventHandler = MinecraftServer.getGlobalEventHandler()
    runEvent(1)
    globalEventHandler.addListener(PlayerLoginEvent::class.java) { event ->
        val player = event.player
        event.setSpawningInstance(instanceContainer)
        player.respawnPoint = Pos(0.0, 42.0, 0.0)
    }
    globalEventHandler.addListener(PlayerSpawnEvent::class.java) { event ->
        if(event.isFirstSpawn) {
            val player = event.player
            runEvent(2, mutableListOf(player), player.instance)
        }
    }
    globalEventHandler.addListener(PlayerDisconnectEvent::class.java) { event ->
        val player = event.player
        runEvent(3, mutableListOf(player), player.instance)
    }
    globalEventHandler.addListener(PlayerCommandEvent::class.java) { event ->
        val player = event.player
        runEvent(4, mutableListOf(player), player.instance)
    }
    globalEventHandler.addListener(PlayerTickEvent::class.java) { event ->
        val player = event.player
        runEvent(5, mutableListOf(player), player.instance)
    }
    server.start("0.0.0.0", 25565)
}

/*
package demo;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.instance.*;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.world.biomes.Biome;

import java.util.Arrays;
import java.util.List;

public class MainDemo {

    public static void main(String[] args) {
        // Initialization
        MinecraftServer minecraftServer = MinecraftServer.init();
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        // Create the instance
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();
        // Set the ChunkGenerator
        instanceContainer.setGenerator(unit ->
                        unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK));
        // Add an event callback to specify the spawning instance (and the spawn position)
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();

        // Start the server on port 25565
        minecraftServer.start("0.0.0.0", 25565);
    }
}
 */