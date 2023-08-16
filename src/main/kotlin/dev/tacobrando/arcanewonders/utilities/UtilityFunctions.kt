package dev.tacobrando.arcanewonders.utilities

import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta

object UtilityFunctions {
    fun colorize(text: String): String {
        val colors = listOf(
            ChatColor.RED,
            ChatColor.LIGHT_PURPLE, // Lighter purple
            ChatColor.GOLD,
            ChatColor.YELLOW,
            ChatColor.GREEN,
            ChatColor.AQUA,         // Light blue/cyan
            ChatColor.BLUE,
            ChatColor.DARK_PURPLE,
            ChatColor.WHITE,        // White
            ChatColor.DARK_AQUA    // Dark cyan
        )
        return text.mapIndexed { index, char ->
            "${colors[index % colors.size]}$char"
        }.joinToString("")
    }

    fun setDisplayName(meta: ItemMeta, name: String, player: Player? = null): ItemMeta {
        meta.setDisplayName(name)
        if (meta is SkullMeta && player != null) {
            meta.owningPlayer = player
        }
        return meta
    }
}