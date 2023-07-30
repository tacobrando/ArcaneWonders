package dev.tacobrando.arcanewonders.listeners

import dev.tacobrando.arcanewonders.ArcaneWonders
import dev.tacobrando.arcanewonders.items.wands.teleport.TeleportWandItem
import org.bukkit.Bukkit
import org.bukkit.ChatColor.*
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action.*
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack

object WandEventListener: Listener {
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player: Player = event.player
        val item: ItemStack? = event.item
        player.isInvulnerable = true

        if (item != null &&
            item.itemMeta?.displayName == "${WHITE}Wand of Teleportation" &&
            (event.action == RIGHT_CLICK_AIR || event.action == RIGHT_CLICK_BLOCK)
        ) {
            val teleportWand = TeleportWandItem()
            teleportWand.createTeleportPortal(player)
        }
    }

}
