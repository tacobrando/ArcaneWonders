package dev.tacobrando.arcanewonders.items

import org.bukkit.Material

enum class ItemTypes {
    WAND_TELEPORT,
    WAND_FIRE;

    fun toMaterial(): Material {
        return when (this) {
            WAND_TELEPORT -> Material.STICK
            WAND_FIRE -> Material.BLAZE_ROD
            // TODO()
        }
    }
//    fun getDisplayName(): String {
//        return when (this) {
//            WAND_TELEPORT -> "${ChatColor.WHITE}Wand of Teleportation"
//            WAND_FIRE -> "${Color.ORANGE}Wand of Fire"
//            // TODO()
//        }
//    }
}
