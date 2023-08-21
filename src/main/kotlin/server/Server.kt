package server

import globalInterpreter
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.instance.block.Block

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
        globalInterpreter.addExtensionInstruction(0) {
            val target = it.getShort()
            val reg = it.getShort().toInt()
            println("Interpreter | Sending message of a funny value ")
            player.sendMessage(globalInterpreter.registers[reg].toDisplay())
        }
        globalInterpreter.bytes.position(0)
        globalInterpreter.interpretEvent(2)
    }

    globalInterpreter.disassemble()
    globalInterpreter.bytes.position(0)
    globalInterpreter.interpretEvent(1)

    minecraftServer.start("0.0.0.0", 25565)


}