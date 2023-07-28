//import dev.tacobrando.arcanewonders.ArcaneWonders
//import dev.tacobrando.arcanewonders.items.MagicWandItem
//import org.bukkit.Bukkit
//import org.bukkit.Location
//import org.bukkit.Material
//import org.bukkit.block.BlockFace
//import org.bukkit.event.EventHandler
//import org.bukkit.event.Listener
//import org.bukkit.event.player.PlayerTeleportEvent
//
//class PortalEventListener(private val magicWandItem: MagicWandItem) : Listener {
//
//    @EventHandler
//    fun onPlayerTeleport(event: PlayerTeleportEvent) {
//        val player = event.player
//        val enteredPortal = magicWandItem.createdPortals.firstOrNull { it.location.distance(player.location.block.location) <= 3 }
//
//        if (event.cause == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL && enteredPortal != null) { // Check if player is in a created portal
//            val bedSpawn = player.bedSpawnLocation
//            val worldSpawn = player.world.spawnLocation
//
//            val spawnLocation: Location = if (bedSpawn != null) {
//                bedSpawn
//            } else {
//                worldSpawn
//            }
//
//            val direction = magicWandItem.getPlayerCardinalFacing(player)
//            when (direction) {
//                BlockFace.NORTH -> spawnLocation.add(0.0, 0.0, -2.0)
//                BlockFace.SOUTH -> spawnLocation.add(0.0, 0.0, 2.0)
//                BlockFace.WEST -> spawnLocation.add(-2.0, 0.0, 0.0)
//                BlockFace.EAST -> spawnLocation.add(2.0, 0.0, 0.0)
//                else -> {}
//            }
//
//            Bukkit.getScheduler().runTask(ArcaneWonders.instance, Runnable {
//                // Teleport player to new spawn location after they have been teleported to the Nether
//                player.teleport(spawnLocation)
//
//                Bukkit.getScheduler().runTaskLater(ArcaneWonders.instance, Runnable {
//                    // Remove the portal at player location
//                    magicWandItem.removePortal(enteredPortal)
//                    // If player was in the exit portal, also remove it
//                    magicWandItem.createdExitPortals.forEach { exitPortalLocation ->
//                        val exitPortal = magicWandItem.createdPortals.firstOrNull { it.location.distance(exitPortalLocation) <= 3 }
//                        if (exitPortal != null) {
//                            magicWandItem.removePortal(exitPortal)
//                        }
//                    }
//                }, 0L) // 20 ticks delay = 1 second
//            })
//        }
//    }
//
//}
//
