package dev.tacobrando.arcanewonders.entities

import dev.tacobrando.arcanewonders.ArcaneWondersPlugin
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
import kotlin.math.sqrt

class PortalEntity(private val player: Player, customPortalLocation: Location? = null) : BukkitRunnable() {
    companion object {
        const val PORTAL_DURATION = 100
        const val PORTAL_RADIUS_X_START = 0.1
        const val PORTAL_RADIUS_Y_START = 0.1
        const val PORTAL_RADIUS_X_END = 0.5
        const val PORTAL_RADIUS_Y_END = 1.0
        const val PARTICLE_COUNT_OUTLINE = 70
        const val PARTICLE_COUNT_INSIDE = 70
    }

    private var currentRadiusX: Double = PORTAL_RADIUS_X_START
    private var currentRadiusY: Double = PORTAL_RADIUS_Y_START
    private val initialYaw: Double = Math.toRadians(player.location.yaw.toDouble())
    private var count = 0
    private val portalLocation: Location = customPortalLocation ?: calculatePortalSpawnLocation()
    init {
        this.runTaskTimer(ArcaneWondersPlugin.instance, 0L, 1L)
    }
    private fun calculatePortalSpawnLocation(): Location {
        val rayTraceResult = player.rayTraceBlocks(4.0) // Ray trace up to 4 blocks away
        return if (rayTraceResult != null) {
            val hitBlock = rayTraceResult.hitBlock
            val hitBlockFace = rayTraceResult.hitBlockFace

            // Debugging: Print information about the hit block and face
            if (hitBlock != null) {
                println("Hit Block: ${hitBlock.type} at ${hitBlock.location}")
            }
            println("Hit Block Face: $hitBlockFace")

            // If a block was hit, spawn the portal on the face of the block that was hit
            val spawnLocation = when (hitBlockFace) {
                BlockFace.UP -> {
                    // If the top face of a block was hit, check if it's a wall (1x2 vertical surface)
                    if (isWall(hitBlock!!.location)) {
                        // If it's a wall, spawn the portal on the wall from top to bottom
                        var portalLocation = hitBlock.location.add(0.5, 1.0, 0.5)
                        while (!portalLocation.block.type.isAir) {
                            portalLocation = portalLocation.add(0.0, 1.0, 0.0)
                        }
                        portalLocation.subtract(0.0, 1.0, 0.0)
                    } else {
                        // If it's not a wall, spawn the portal on top of the block
                        hitBlock.location.add(0.5, 1.0, 0.5)
                    }
                }
                BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST -> {
                    // If a side face of a block was hit, spawn the portal from top to bottom on the wall
                    var portalLocation = hitBlock?.location!!.add(0.5, 0.5, 0.5)
                    while (!portalLocation.block.type.isAir) {
                        portalLocation = portalLocation.add(0.0, 1.0, 0.0)
                    }
                    portalLocation.subtract(0.0, 1.0, 0.0)
                }
                else -> {
                    // For any other case, spawn the portal on top of the block
                    hitBlock?.location!!.add(0.5, 1.0, 0.5)
                }
            }
            // Move the portal one block higher to avoid overlapping with the wall
            spawnLocation.add(0.0, 1.0, 0.0)
        } else {
            // If no block was hit, spawn the portal at the maximum reach of the player
            val direction = player.location.direction
            val spawnLocation = player.location.add(direction.multiply(4)) // Add the direction the player is looking
            // Adjust the y-coordinate of the spawn location to be the height of the highest non-air block at the new x and z coordinates
            val highestBlockY = spawnLocation.world?.getHighestBlockYAt(spawnLocation.blockX, spawnLocation.blockZ)
            if (highestBlockY != null && highestBlockY > spawnLocation.y) {
                spawnLocation.y = highestBlockY.toDouble()
            }
            spawnLocation
        }
    }

    private fun isWall(location: Location): Boolean {
        // Check if the location is a wall (1x2 vertical surface with no obstructions)
        val block1 = location.block
        val block2 = location.clone().add(0.0, 1.0, 0.0).block
        return block1.type.isSolid && block2.type.isSolid
    }

    fun getPortalLocation(): Location = portalLocation
    fun getCurrentRadiusX(): Double = currentRadiusX

//    fun getCurrentRadiusY(): Double = currentRadiusY
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

            val distance = sqrt(x * x + y * y)
            val colorFraction = 1 - distance / sqrt(currentRadiusX * currentRadiusX + currentRadiusY * currentRadiusY)

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