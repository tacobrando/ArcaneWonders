import dev.tacobrando.arcanewonders.ArcaneWonders
import dev.tacobrando.arcanewonders.items.wands.teleport.TeleportWandItem
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

class PortalEntity(private val player: Player, private val customPortalLocation: Location? = null) : BukkitRunnable() {
    companion object {
        const val PORTAL_DISTANCE = 1
        const val PORTAL_DURATION = 200
        const val PORTAL_RADIUS_X_START = 0.1
        const val PORTAL_RADIUS_Y_START = 0.1
        const val PORTAL_RADIUS_X_END = 0.5
        const val PORTAL_RADIUS_Y_END = 1.0
        const val PARTICLE_COUNT_OUTLINE = 100
        const val PARTICLE_COUNT_INSIDE = 100
    }

    private var currentRadiusX: Double = PORTAL_RADIUS_X_START
    private var currentRadiusY: Double = PORTAL_RADIUS_Y_START
    private val initialYaw: Double = Math.toRadians(player.location.yaw.toDouble())
    private val initialDirection = player.location.direction
    private var count = 0
    private val portalLocation: Location = customPortalLocation ?: calculatePortalSpawnLocation()
    init {
        this.runTaskTimer(ArcaneWonders.instance, 0L, 1L)
    }

    private fun calculatePortalSpawnLocation(): Location {
        val spawnLocation = player.eyeLocation.add(player.location.direction.multiply(1))
        val block = spawnLocation.block
        if (!block.type.isAir) {
            return if (block.getRelative(BlockFace.UP).type.isAir) {
                // If there's a block where the portal is supposed to spawn, spawn it on top
                block.location.add(0.0, 1.0, 0.0)
            } else {
                // If there's a block in front of the player, spawn the portal a block further
                spawnLocation.add(player.location.direction)
            }
        }
        return spawnLocation
    }

    fun getPortalLocation(): Location = portalLocation
    fun getCurrentRadiusX(): Double = currentRadiusX
    fun getCurrentRadiusY(): Double = currentRadiusY
    override fun run() {
        player.isInvulnerable = false
        if (count >= PORTAL_DURATION) {
            TeleportWandItem.activePortals.remove(player)
            this.cancel()
            return
        }

        val darkCyan = Color.fromRGB(0, 139, 139)
        val lightAqua = Color.fromRGB(127, 255, 212)

        val fraction = count.toDouble() / PORTAL_DURATION.toDouble()
        val lerpFraction = 1 - (1 - fraction).pow(100) // Increase for speed

        currentRadiusX = lerp(PORTAL_RADIUS_X_START, PORTAL_RADIUS_X_END, lerpFraction)
        currentRadiusY = lerp(PORTAL_RADIUS_Y_START, PORTAL_RADIUS_Y_END, lerpFraction)


        // Calculate the cos and sin of the yaw angle
        val cosYaw = cos(initialYaw)
        val sinYaw = sin(initialYaw)

        for (i in 0 until PARTICLE_COUNT_OUTLINE) {
            val angle = 2 * Math.PI * i / PARTICLE_COUNT_OUTLINE

            val radiusX = currentRadiusX * cos(angle)
            val radiusY = currentRadiusY * sin(angle)

            // Rotate the particleLocation around the Y-axis based on the player's yaw
            val particleLocation = portalLocation.clone().add(radiusX * cosYaw, radiusY, radiusX * sinYaw)

            val dustOptions = Particle.DustOptions(lightAqua, 1.0F)
            player.world.spawnParticle(Particle.REDSTONE, particleLocation, 0, dustOptions)
        }

        for (i in 0 until PARTICLE_COUNT_INSIDE) {
            val innerFraction = i.toDouble() / PARTICLE_COUNT_INSIDE.toDouble()

            val angle = 2 * Math.PI * innerFraction * (count % PARTICLE_COUNT_INSIDE)
            val x = currentRadiusX * innerFraction * cos(angle)
            val y = currentRadiusY * innerFraction * sin(angle)

            // Rotate the particleLocation around the Y-axis based on the player's yaw
            val particleLocation = portalLocation.clone().add(x * cosYaw, y, x * sinYaw)

            val distance = Math.sqrt(x * x + y * y)
            val colorFraction = 1 - distance / Math.sqrt(currentRadiusX * currentRadiusX + currentRadiusY * currentRadiusY)

            val r = lerp(darkCyan.red.toDouble(), lightAqua.red.toDouble(), colorFraction).toInt()
            val g = lerp(darkCyan.green.toDouble(), lightAqua.green.toDouble(), colorFraction).toInt()
            val b = lerp(darkCyan.blue.toDouble(), lightAqua.blue.toDouble(), colorFraction).toInt()

            val dustOptions = Particle.DustOptions(Color.fromRGB(r, g, b), 1.0F)
            player.world.spawnParticle(Particle.REDSTONE, particleLocation, 0, dustOptions)
        }

        count++
    }
    private fun lerp(start: Double, end: Double, fraction: Double): Double {
        return start + (end - start) * fraction
    }
}