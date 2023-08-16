package dev.tacobrando.arcanewonders.items.wands.teleport

import dev.tacobrando.arcanewonders.ArcaneWondersPlugin
import dev.tacobrando.arcanewonders.entities.PortalEntity
import dev.tacobrando.arcanewonders.items.ArcaneWondersItem
import dev.tacobrando.arcanewonders.items.ItemTypes
import dev.tacobrando.arcanewonders.recipes.WandRecipe
import org.bukkit.ChatColor.*
import org.bukkit.Location
import org.bukkit.Material.*
import org.bukkit.NamespacedKey
import org.bukkit.Sound.*
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

class TeleportWandItem : ArcaneWondersItem(ItemTypes.WAND_TELEPORT) {
    companion object {
        val activePortals: MutableMap<Player, MutableList<PortalEntity>> = mutableMapOf()
        const val COOLDOWN_KEY = "teleport_wand_cooldown"
        const val COOLDOWN_DURATION = 5000 // Cooldown duration in milliseconds (5 seconds in this example)
    }
    init {
        setDisplayName("${WHITE}Wand of Teleportation")
        meta.lore = listOf("A magical wand of teleportation!")
        meta.addEnchant(Enchantment.DURABILITY, 1, true)
        meta.setCustomModelData(1)
        item.itemMeta = meta
        setNbtTag("teleport_wand", true)
    }
    fun registerRecipe() {
        val shape = listOf("E", "S", "S")
        val ingredients = mapOf(
            'E' to ENDER_PEARL,
            'S' to STICK
        )
        WandRecipe.register(item, "teleport_wand_vertical", shape, ingredients)
    }

    fun createTeleportPortal(player: Player, clickedLocation: Location, spawnLocation: Location? = null) {
        // Check if the item in the player's hand is the "Wand of Teleportation"
        val heldItem = player.inventory.itemInMainHand
        val heldItemMeta = heldItem.itemMeta
        val key = NamespacedKey(ArcaneWondersPlugin.instance, "teleport_wand")
        val hasTag = heldItemMeta?.persistentDataContainer?.get(key, PersistentDataType.BOOLEAN) ?: false

        // Check if the held item has the correct NBT tag, type, and display name
        if (hasTag && heldItem.type == this.item.type && heldItemMeta?.displayName == this.item.itemMeta?.displayName) {
            val cooldownTimestamp = heldItemMeta?.persistentDataContainer?.get(NamespacedKey(ArcaneWondersPlugin.instance, COOLDOWN_KEY), PersistentDataType.LONG) ?: 0L
            if (System.currentTimeMillis() < cooldownTimestamp) {
                val remainingTime = (cooldownTimestamp - System.currentTimeMillis()) / 1000
                player.sendMessage("${RED}You can't use the teleport wand right now. Please wait $remainingTime seconds.")
                return
            }

            val portal = PortalEntity(player, clickedLocation)
            val portalList = activePortals.getOrDefault(player, mutableListOf())
            portalList.add(portal)
            activePortals[player] = portalList

            val secondPortalLocation =
                spawnLocation?.add(0.0, 1.0, 0.0) ?:
                player.bedSpawnLocation?.add(0.0, 1.0, 0.0) ?:
                player.world.spawnLocation.add(0.0, 1.0, 0.0)
            val secondPortal = PortalEntity(player, secondPortalLocation)
            portalList.add(secondPortal)

            heldItemMeta?.persistentDataContainer?.set(NamespacedKey(ArcaneWondersPlugin.instance, COOLDOWN_KEY), PersistentDataType.LONG, System.currentTimeMillis() + COOLDOWN_DURATION)
            heldItem.itemMeta = heldItemMeta
            player.playSound(portal.getPortalLocation(), ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
        }
    }
}
