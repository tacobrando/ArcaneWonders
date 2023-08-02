package dev.tacobrando.arcanewonders.items

import org.bukkit.ChatColor
import org.bukkit.Material

enum class ItemTypes {
    WAND_TELEPORT;

    fun toMaterial(): Material {
        return when (this) {
            WAND_TELEPORT -> Material.BLAZE_ROD
            // Add other mappings for different wand types here if needed
        }
    }

    fun getDisplayName(): String {
        return when (this) {
            WAND_TELEPORT -> "${ChatColor.WHITE}Wand of Teleportation"
            // Add other display names for different wand types here if needed
        }
    }
}
