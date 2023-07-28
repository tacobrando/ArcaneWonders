import dev.tacobrando.arcanewonders.items.MagicWandItem
import org.bukkit.Material

//package dev.tacobrando.arcanewonders.structures.Portal

//import dev.tacobrando.arcanewonders.items.MagicWandItem
//import dev.tacobrando.arcanewonders.items.Portal
//import org.bukkit.*
//import org.bukkit.block.BlockFace
//import org.bukkit.block.data.Orientable
//import org.bukkit.entity.Player
//
//object Portal {
//
//}
//fun createNetherPortal(player: Player, location: Location, isExitPortal: Boolean = true) {
//    val dustOptions = Particle.DustOptions(Color.fromRGB(128, 0, 128), 1f)
//    val direction = MagicWandItem.getPlayerCardinalFacing(player)
//    var baseLocation: Location = location.clone()
//
//    when (direction) {
//        BlockFace.NORTH -> baseLocation = baseLocation.add(0.0, 0.0, -2.0)
//        BlockFace.SOUTH -> baseLocation = baseLocation.add(0.0, 0.0, 2.0)
//        BlockFace.WEST -> baseLocation = baseLocation.add(-2.0, 0.0, 0.0)
//        BlockFace.EAST -> baseLocation = baseLocation.add(2.0, 0.0, 0.0)
//        else -> {}
//    }
//    val portal = Portal(location = baseLocation)
//    MagicWandItem.createdPortals.add(portal)
//    MagicWandItem.portalCreationTimes[baseLocation] = System.currentTimeMillis()
//    if (isExitPortal) {
//        MagicWandItem.createdExitPortals.add(baseLocation)
//        MagicWandItem.exitPortalsDisplay[player] = baseLocation.clone()
//    } else {
//        MagicWandItem.createdPortals.add(portal)
//    }
//
//    for (y in -1..1) {
//        val location = baseLocation.clone().add(0.0, y.toDouble(), 0.0)
//        if (MagicWandItem.isSoftBlock(location.block.type)) {
//            when (direction) {
//                BlockFace.NORTH, BlockFace.SOUTH -> {
//                    location.block.type = Material.NETHER_PORTAL
//                }
//                BlockFace.WEST, BlockFace.EAST -> {
//                    location.block.blockData = Material.NETHER_PORTAL.createBlockData().also { (it as Orientable).axis = Axis.Z }
//                }
//
//                else -> {}
//            }
//
//            // Spawn particles around each block of the portal
//            MagicWandItem.spawnFirework(location)
////                for (x in -1..1) {
////                    for (z in -1..1) {
////                        location.world?.spawnParticle(Particle.END_ROD, location.add(x.toDouble(), 0.0, z.toDouble()), 50    )
////                    }
////                }
//        }
//    }


//fun isSoftBlock(material: Material): Boolean {
//    return material in listOf(Material.GRASS, Material.TALL_GRASS, Material.SNOW, Material.AIR)
//}
//
//fun removePortal(portal: Portal) {
//    val location = portal.location
//    for (y in -1..1) {
//        val portalLocation = location.clone().add(0.0, y.toDouble(), 0.0)
//        if (portalLocation.block.type == Material.NETHER_PORTAL) {
//            portalLocation.block.type = Material.AIR
//        }
//    }
//    MagicWandItem.spawnEffects(location)
//    MagicWandItem.portalCreationTimes.remove(location)
//    MagicWandItem.createdPortals.remove(portal)
//}
