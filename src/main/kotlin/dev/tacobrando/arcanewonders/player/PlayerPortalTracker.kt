package dev.tacobrando.arcanewonders.player

import dev.tacobrando.arcanewonders.ArcaneWondersPlugin
import dev.tacobrando.arcanewonders.entities.PortalEntity
import dev.tacobrando.arcanewonders.items.wands.teleport.TeleportWandItem
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.pow
import kotlin.math.sqrt

class PlayerPortalTracker : BukkitRunnable() {
    private val teleportDurations: MutableMap<Player, Long> = mutableMapOf()

    init {
        // Start the task immediately and repeat every tick
        this.runTaskTimer(ArcaneWondersPlugin.instance, 0L, 1L)
    }

    override fun run() {
        for (player in Bukkit.getOnlinePlayers()) {
            checkPlayerPortalInteraction(player)
        }
    }
    private fun checkPlayerPortalInteraction(player: Player) {
        val playerLocation = player.location
        val portals = TeleportWandItem.activePortals[player] ?: return
        for (portal in portals) {
            val portalLocation = portal.getPortalLocation()
            val portalRadius: Double = portal.getCurrentRadiusX()
            // Calculate the horizontal distance between the player and the center of the portal
            val horizontalDistance: Double = getHorizontalDistance(playerLocation, portalLocation)
            // Check if the player is within the portal's radius
            if (horizontalDistance <= portalRadius && (teleportDurations[player] ?: 0L) < System.currentTimeMillis()) {
                handlePlayerPortalEntry(player, portalLocation)
            }
        }
    }
    private fun handlePlayerPortalEntry(player: Player, portalLocation: Location) {
        // Get the second portal's destination
        val destinationPortal = TeleportWandItem.activePortals[player]?.get(1)
        val destinationLocation = destinationPortal?.getPortalLocation() ?: player.bedSpawnLocation ?: player.world.spawnLocation

        schedulePlayerTeleportation(player, portalLocation, destinationLocation)
        scheduleAllPortalsRemoval(player)
        teleportDurations[player] = System.currentTimeMillis() + 2000 // 2 second cool-down
    }


    private fun scheduleAllPortalsRemoval(player: Player) {
        val portals = TeleportWandItem.activePortals[player] ?: return
        for (portal in portals) {
            schedulePortalRemoval(portal, player)
        }
    }
    private fun schedulePlayerTeleportation(player: Player, portalLocation: Location, destinationLocation: Location) {
        val entryYawDifference = player.location.yaw - portalLocation.yaw
        object : BukkitRunnable() {
            override fun run() {
                val teleportLocation = destinationLocation.clone()
                teleportLocation.yaw = portalLocation.yaw + entryYawDifference // Adjusts the yaw based on the entry yaw difference

                // Adjusts the teleport location based on the direction the player was facing
                when (entryYawDifference) {
                    in -45.0..45.0 -> teleportLocation.add(0.0, 0.0, 0.0) // North
                    in 45.0..135.0 -> teleportLocation.add(-1.0, 0.0, 0.0) // West
                    in -135.0..-45.0 -> teleportLocation.add(1.0, 0.0, 0.0) // East
                    else -> teleportLocation.add(0.0, 0.0, 0.0) // South
                }

                teleportLocation.subtract(0.0, 1.0, 0.0)

                player.teleport(teleportLocation)
            }
        }.runTaskLater(ArcaneWondersPlugin.instance, 0L)
    }

    private fun schedulePortalRemoval(portal: PortalEntity, player: Player? = null) {
        object : BukkitRunnable() {
            override fun run() {
                portal.cancel()
                player?.let { TeleportWandItem.activePortals.remove(it) } // Remove player from the activePortals map
            }
        }.runTaskLater(ArcaneWondersPlugin.instance, 20L) // 20 ticks = 1 second
    }
    private fun getHorizontalDistance(loc1: Location, loc2: Location): Double {
        return sqrt((loc1.x - loc2.x).pow(2) + (loc1.z - loc2.z).pow(2))
    }
}
