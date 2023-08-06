package dev.tacobrando.arcanewonders.listeners

import dev.tacobrando.arcanewonders.ArcaneWondersPlugin
import dev.tacobrando.arcanewonders.items.wands.teleport.TeleportWandItem
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import kotlin.math.cos
import kotlin.math.sin

object WandEventListener : Listener {
    private val lastInteractedBlockFace: MutableMap<Player, BlockFace> = mutableMapOf()
    private val selectedTeleportMode: MutableMap<Player, Material> = mutableMapOf()

    private fun createTeleportModeSelectionInventory(): Inventory = Bukkit.createInventory(null, 9, "Select Teleport Mode").apply {
        setItem(0, ItemStack(Material.COMPASS).apply {
            val meta = itemMeta
            meta?.setDisplayName("Home")
            itemMeta = meta
        })
        setItem(1, ItemStack(Material.CLOCK).apply {
            val meta = itemMeta
            meta?.setDisplayName("Random")
            itemMeta = meta
        })
    }

    private fun getTargetLocation(player: Player, blockFace: BlockFace?): Location {
        val clickedBlock = player.getTargetBlockExact(4)
        return when {
            clickedBlock != null && blockFace == BlockFace.UP -> clickedBlock.location.add(0.5, 2.0, 0.5)
            clickedBlock != null -> clickedBlock.location.add(0.5, 0.0, 0.5)
            else -> player.eyeLocation.add(player.location.direction.multiply(4))
        }
    }
    private fun getRandomLandLocation(player: Player): Location {
        var randomLocation: Location
        val maxRetries = 5  // Set number of retries to 10
        var retries = 0

        do {
            val distanceSteps = (20..100).random() * 10  // Increased minimum distance
            val angle = (0..360).random().toDouble()
            val randomX = player.location.x + distanceSteps * cos(Math.toRadians(angle))
            val randomZ = player.location.z + distanceSteps * sin(Math.toRadians(angle))
            val newY = player.world.getHighestBlockYAt(randomX.toInt(), randomZ.toInt()).toDouble() + 1
            randomLocation = Location(player.world, randomX, newY, randomZ)
            retries++
        } while (!isSafeSpawnLocation(randomLocation) && retries < maxRetries)

        if (retries == maxRetries) {
            return player.location
        }

        return randomLocation
    }

    private fun isSafeSpawnLocation(location: Location): Boolean {
        val blockType = location.block.type
        val blockBelowType = location.add(0.0, -1.0, 0.0).block.type
        val blockAboveType = location.add(0.0, 1.0, 0.0).block.type

        return blockType == Material.AIR && blockAboveType == Material.AIR && blockBelowType != Material.WATER && blockBelowType != Material.LAVA
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player: Player = event.player
        val item: ItemStack? = event.item
        val teleportWand = TeleportWandItem()

        // Check if the item is null
        if (item == null) return

        // Check if the item has the custom NBT tag set
        val itemMeta = item.itemMeta
        val key = NamespacedKey(ArcaneWondersPlugin.instance, "teleport_wand")
        val hasTag = itemMeta?.persistentDataContainer?.get(key, PersistentDataType.BOOLEAN) ?: false
        if (!hasTag) return

        // Now, check if the item is of the same type as the wand and has the correct display name
        if (itemMeta != null) {
            if (item.type != teleportWand.item.type || itemMeta.displayName != teleportWand.item.itemMeta?.displayName) return
        }


        if (item.type == teleportWand.item.type && itemMeta?.displayName == teleportWand.item.itemMeta?.displayName) {
            if (event.action == Action.LEFT_CLICK_AIR || event.action == Action.LEFT_CLICK_BLOCK) {
                player.openInventory(createTeleportModeSelectionInventory())
            } else if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {
                lastInteractedBlockFace[player] = event.blockFace
                selectedTeleportMode[player]?.let { mode ->
                    handleModeSelection(player, mode)
                } ?: player.sendMessage("${org.bukkit.ChatColor.RED}Teleport mode not set")
            }
        }
    }



    private fun handleModeSelection(player: Player, mode: Material) {
        val wandItem = TeleportWandItem()
        val targetLocation = getTargetLocation(player, lastInteractedBlockFace[player])
        when (mode) {
            Material.COMPASS -> {
                wandItem.createTeleportPortal(player, targetLocation)
            }
            Material.CLOCK -> {
                val randomLocation = getRandomLandLocation(player)
                wandItem.createTeleportPortal(player, targetLocation, randomLocation)
            }
            else -> {}
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (event.view.title != "Select Teleport Mode") return
        event.isCancelled = true
        val player = event.whoClicked as Player
        val clickedItem = event.currentItem

        when (clickedItem?.type) {
            Material.COMPASS -> {
                selectedTeleportMode[player] = Material.COMPASS  // Store the selected mode
                player.closeInventory()
            }
            Material.CLOCK -> {
                selectedTeleportMode[player] = Material.CLOCK  // Store the selected mode
                player.closeInventory()
            }
            else -> {}
        }
    }
}
