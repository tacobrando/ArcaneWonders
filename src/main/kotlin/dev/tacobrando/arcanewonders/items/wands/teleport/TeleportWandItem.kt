package dev.tacobrando.arcanewonders.items.wands.teleport

import PortalEntity
import dev.tacobrando.arcanewonders.ArcaneWonders
import dev.tacobrando.arcanewonders.items.ArcaneWondersItem
import dev.tacobrando.arcanewonders.recipes.WandRecipe
import dev.tacobrando.arcanewonders.vfx.Effects
import org.bukkit.*
import org.bukkit.ChatColor.*
import org.bukkit.Material.*
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

open class TeleportWandItem: ArcaneWondersItem(BLAZE_ROD) {
    companion object {
        val activePortals: MutableMap<Player, MutableList<PortalEntity>> = mutableMapOf()
    }
    init {
        setDisplayName("${WHITE}Wand of Teleportation")
        meta.lore = listOf("A magical wand of teleportation!")
        meta.addEnchant(Enchantment.DURABILITY, 1, true)
        item.itemMeta = meta
        setNbtTag("teleportWand", "true")
    }

    fun registerRecipe() = WandRecipe.register(item, "teleport_wand_vertical")


    open fun createTeleportPortal(player: Player) {
        val portal = PortalEntity(player)
        val portalList = activePortals.getOrDefault(player, mutableListOf())
        portalList.add(portal)
        activePortals[player] = portalList

        val bedSpawnLocation = player.bedSpawnLocation
        val secondPortalLocation = bedSpawnLocation?.add(0.0, 1.0, 0.0) ?: player.world.spawnLocation.add(0.0, 1.0, 0.0)
        val secondPortal = PortalEntity(player, secondPortalLocation)
        portalList.add(secondPortal)
    }

}