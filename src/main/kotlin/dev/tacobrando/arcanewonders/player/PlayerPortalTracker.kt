import dev.tacobrando.arcanewonders.ArcaneWonders
import dev.tacobrando.arcanewonders.items.wands.teleport.TeleportWandItem
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.pow
import kotlin.math.sqrt

class PlayerPortalTracker : BukkitRunnable() {
    private val teleportCooldowns: MutableMap<Player, Long> = mutableMapOf()

    init {
        // Start the task immediately and repeat every tick
        this.runTaskTimer(ArcaneWonders.instance, 0L, 1L)
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
            val portalRadius: Double = portal.getCurrentRadiusX() // Specify the type as Double

            // Calculate the horizontal distance between the player and the center of the portal
            val horizontalDistance: Double = getHorizontalDistance(playerLocation, portalLocation)

            // Check if the player is within the portal's radius (ignoring height)
            if (horizontalDistance <= portalRadius && (teleportCooldowns[player] ?: 0L) < System.currentTimeMillis()) {
                handlePlayerPortalEntry(player, portal, portalLocation)
            }
        }
    }
    private fun handlePlayerPortalEntry(player: Player, portal: PortalEntity, portalLocation: Location) {
        val bedSpawnLocation = player.bedSpawnLocation
        schedulePlayerTeleportation(player, portalLocation, bedSpawnLocation)
        scheduleAllPortalsRemoval(player)
        teleportCooldowns[player] = System.currentTimeMillis() + 2000 // 2 second cooldown
    }

    private fun scheduleAllPortalsRemoval(player: Player) {
        val portals = TeleportWandItem.activePortals[player] ?: return
        for (portal in portals) {
            schedulePortalRemoval(portal, player)
        }
    }

    private fun schedulePlayerTeleportation(player: Player, portalLocation: Location, bedSpawnLocation: Location?) {
        object : BukkitRunnable() {
            override fun run() {
                val teleportLocation = (bedSpawnLocation ?: player.world.spawnLocation).clone()
                teleportLocation.add(0.0, 0.0, 0.5) // Add a small offset to the z-coordinate
                teleportLocation.yaw = player.location.yaw + (portalLocation.yaw - player.location.yaw) // Adjust the yaw based on the player's current yaw and the portal's yaw
                player.teleport(teleportLocation)
            }
        }.runTaskLater(ArcaneWonders.instance, 0L)
    }



    private fun schedulePortalRemoval(portal: PortalEntity, player: Player? = null) {
        object : BukkitRunnable() {
            override fun run() {
                portal.cancel() // Cancel the BukkitRunnable task
                player?.let { TeleportWandItem.activePortals.remove(it) } // Remove the player from the activePortals map
            }
        }.runTaskLater(ArcaneWonders.instance, 20L) // 20 ticks = 1 seconds
    }

    // Helper function to calculate the horizontal distance between two locations
    private fun getHorizontalDistance(loc1: Location, loc2: Location): Double {
        return Math.sqrt((loc1.x - loc2.x).pow(2) + (loc1.z - loc2.z).pow(2))
    }
}
