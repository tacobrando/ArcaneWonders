import dev.tacobrando.arcanewonders.ArcaneWonders
import dev.tacobrando.arcanewonders.items.wands.teleport.TeleportWandItem
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.sqrt

object PlayerPortalTracker : BukkitRunnable() {
    init {
        // Start the task immediately and repeat every tick
        this.runTaskTimer(ArcaneWonders.instance, 0L, 1L)
    }

    override fun run() {
        for (player in Bukkit.getOnlinePlayers()) {
            val playerLocation = player.location
            for (entry in TeleportWandItem.activePortals.entries) {
                val playerPortal = entry.key
                val portal = entry.value
                val portalLocation = portal.getPortalLocation()
                val portalRadius: Double = portal.getCurrentRadiusX() // Specify the type as Double

                // Calculate the distance between the player and the center of the portal
                val distance: Double = playerLocation.distance(portalLocation) // Specify the type as Double
                // Check if the player is within the portal's radius

                if (distance <= portalRadius) {

                    val bedSpawnLocation = player.bedSpawnLocation
                    if (bedSpawnLocation != null) {
                        player.teleport(bedSpawnLocation)
                    } else {
                        player.teleport(player.world.spawnLocation)
                    }
                }
            }
        }
    }
}
