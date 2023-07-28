package dev.tacobrando.arcanewonders.items

import dev.tacobrando.arcanewonders.ArcaneWonders
import dev.tacobrando.arcanewonders.vfx.Effects
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.FireworkEffect.Type.*
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Firework
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.meta.FireworkMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable


object MagicWandItem {

    private fun getMagicWand(): ItemStack {
        val magicWand = ItemStack(Material.BLAZE_ROD)
        val meta: ItemMeta = magicWand.itemMeta!!

        meta.setDisplayName("${ChatColor.GOLD}Magic Wand")
        meta.lore = listOf("A magical wand! The sky is the limit!")
        meta.addEnchant(Enchantment.DURABILITY, 1, true)

        magicWand.itemMeta = meta

        return magicWand
    }

    fun registerRecipe(plugin: JavaPlugin) {
        val verticalKey = NamespacedKey(plugin, "magic_wand_vertical")
        val verticalRecipe = ShapedRecipe(verticalKey, getMagicWand())

        verticalRecipe.shape("E", "B", "B")

        verticalRecipe.setIngredient('B', Material.BLAZE_ROD)
        verticalRecipe.setIngredient('E', Material.ENDER_PEARL)

        Bukkit.addRecipe(verticalRecipe)
    }

    // Teleport player to spawn/bed
    fun teleportToSpawn(player: Player, currentSpawn: Location, spawnLocation: Location) {
        object : BukkitRunnable() {
            override fun run() {
                player.isInvulnerable = false
            }
        }.runTaskLater(ArcaneWonders.instance, 10) // 20 ticks = 1 second

        Effects.spawnFireworks(currentSpawn, BURST, 2)
        player.teleport(spawnLocation)
        Effects.spawnFireworks(spawnLocation, BURST, 2)
    }

}