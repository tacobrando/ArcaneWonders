package dev.tacobrando.arcanewonders.listeners

import dev.tacobrando.arcanewonders.items.ArcaneWondersItem
import dev.tacobrando.arcanewonders.items.ItemTypes
import dev.tacobrando.arcanewonders.items.wands.teleport.TeleportWandItem
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

object WandEventListener : Listener {
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player: Player = event.player
        val item: ItemStack? = event.item
        player.isInvulnerable = true

        if (item != null &&
            (
                    event.action == Action.RIGHT_CLICK_AIR ||
                            event.action == Action.RIGHT_CLICK_BLOCK
                    )
        ) {
            val wandItem: ArcaneWondersItem
            when (ArcaneWondersItem(ItemTypes.WAND_TELEPORT).itemType) {
                ItemTypes.WAND_TELEPORT -> {
                    wandItem = TeleportWandItem()
                }
            }
            val clickedBlock = event.clickedBlock
            val clickedLocation = if (clickedBlock != null) {
                if(event.blockFace == BlockFace.UP) {
                    clickedBlock.location.add(0.5, 2.0, 0.5)
                } else {
                    clickedBlock.location.add(0.5, 0.0, 0.5)
                }
            } else {
                player.eyeLocation.add(player.location.direction.multiply(4))
            }
            wandItem.createTeleportPortal(player, clickedLocation)
        }
    }

}

