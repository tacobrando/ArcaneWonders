package dev.tacobrando.arcanewonders.items.wands.teleport

import dev.tacobrando.arcanewonders.ArcaneWonders
import dev.tacobrando.arcanewonders.items.CustomItem
import dev.tacobrando.arcanewonders.recipes.WandRecipe
import dev.tacobrando.arcanewonders.vfx.Effects
import org.bukkit.*
import org.bukkit.ChatColor.*
import org.bukkit.Material.*
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

open class TeleportWandItem: CustomItem(BLAZE_ROD) {
    init {
        setDisplayName("${WHITE}Wand of Teleportation")
        meta.lore = listOf("A magical wand of teleportation!")
        meta.addEnchant(Enchantment.DURABILITY, 1, true)
        item.itemMeta = meta
        setNbtTag("teleportWand", "true")
    }

    fun registerRecipe() = WandRecipe.register(item, "teleport_wand_vertical")

    open fun teleportToSpawn(player: Player, currentSpawn: Location, spawnLocation: Location) {
        object : BukkitRunnable() {
            override fun run() {
                player.isInvulnerable = false
            }
        }.runTaskLater(ArcaneWonders.instance, 10) // 20 ticks = 1 second

        Effects.spawnFireworks(currentSpawn, FireworkEffect.Type.BURST, 2)
        player.teleport(spawnLocation)
        Effects.spawnFireworks(spawnLocation, FireworkEffect.Type.BURST, 2)
    }
}