package dev.tacobrando.arcanewonders.listeners

import dev.tacobrando.arcanewonders.items.MagicWandItem
import dev.tacobrando.arcanewonders.items.wands.teleport.TeleportWandItem
import org.bukkit.ChatColor.*
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action.*
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

object WandEventListener: Listener {
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player: Player = event.player
        val item: ItemStack? = event.item
        val bedSpawn = player.bedSpawnLocation
        val worldSpawn = player.world.spawnLocation

        player.isInvulnerable = true

        val spawnLocation: Location = if (bedSpawn != null) {
            bedSpawn
        } else {
            worldSpawn
        }

        if (
            item != null &&
            item.itemMeta?.displayName == "${WHITE}Wand of Teleportation" &&
            (
                    event.action ==
                            RIGHT_CLICK_AIR ||
                            event.action == RIGHT_CLICK_BLOCK
                    )
        ) {
            val currentLocation = player.location.clone()
            val teleportWand = TeleportWandItem()
            teleportWand.teleportToSpawn(player, currentLocation, spawnLocation)
        }
    }
}
