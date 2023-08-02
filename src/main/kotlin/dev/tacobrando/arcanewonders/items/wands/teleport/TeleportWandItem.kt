package dev.tacobrando.arcanewonders.items.wands.teleport

import dev.tacobrando.arcanewonders.entities.PortalEntity
import dev.tacobrando.arcanewonders.items.ArcaneWondersItem
import dev.tacobrando.arcanewonders.items.ItemTypes
import dev.tacobrando.arcanewonders.recipes.WandRecipe
import org.bukkit.ChatColor.*
import org.bukkit.Location
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player

class TeleportWandItem : ArcaneWondersItem(ItemTypes.WAND_TELEPORT) {
    companion object {
        val activePortals: MutableMap<Player, MutableList<PortalEntity>> = mutableMapOf()
    }
    init {
        setDisplayName("${WHITE}Wand of Teleportation")
        meta.lore = listOf("A magical wand of teleportation!")
        meta.addEnchant(Enchantment.DURABILITY, 1, true)
        item.itemMeta = meta
        setNbtTag("teleport_wand", true)
    }
    fun registerRecipe() = WandRecipe.register(item, "teleport_wand_vertical")

    fun createTeleportPortal(player: Player, clickedLocation: Location) {
        val portal = PortalEntity(player, clickedLocation)
        val portalList = activePortals.getOrDefault(player, mutableListOf())
        portalList.add(portal)
        activePortals[player] = portalList

        val bedSpawnLocation = player.bedSpawnLocation
        val secondPortalLocation = bedSpawnLocation?.add(0.0, 1.0, 0.0) ?: player.world.spawnLocation.add(0.0, 1.0, 0.0)
        val secondPortal = PortalEntity(player, secondPortalLocation)
        portalList.add(secondPortal)
    }

}