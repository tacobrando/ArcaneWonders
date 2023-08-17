package dev.tacobrando.arcanewonders.listeners

import dev.tacobrando.arcanewonders.ArcaneWondersPlugin
import dev.tacobrando.arcanewonders.items.wands.teleport.TeleportMode
import dev.tacobrando.arcanewonders.items.wands.teleport.TeleportWandItem
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin
import dev.tacobrando.arcanewonders.utilities.UtilityFunctions.setDisplayName
import dev.tacobrando.arcanewonders.utilities.UtilityFunctions.colorize

object WandEventListener : Listener {
    private val lastInteractedBlockFace: MutableMap<Player, BlockFace> = mutableMapOf()
    private val selectedTeleportMode: MutableMap<Player, TeleportMode> = mutableMapOf()
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player: Player = event.player
        val item: ItemStack? = event.item
        val teleportWand = TeleportWandItem()

        if (item == null) return

        val itemMeta = item.itemMeta
        val key = NamespacedKey(ArcaneWondersPlugin.instance, "teleport_wand")
        val hasTag = itemMeta?.persistentDataContainer?.get(key, PersistentDataType.BOOLEAN) ?: false
        if (!hasTag) return

        if (itemMeta != null) {
            if (item.type != teleportWand.item.type || itemMeta.displayName != teleportWand.item.itemMeta?.displayName) return
        }

        if (item.type == teleportWand.item.type && itemMeta?.displayName == teleportWand.item.itemMeta?.displayName) {
            if (event.action == Action.LEFT_CLICK_AIR || event.action == Action.LEFT_CLICK_BLOCK) {
                player.openInventory(createTeleportModeSelectionInventory(player))
            } else if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {
                lastInteractedBlockFace[player] = event.blockFace
                selectedTeleportMode[player]?.let { mode ->
                    handleModeSelection(player, mode)
                } ?: player.sendMessage("${org.bukkit.ChatColor.RED}Teleport mode not set")
            }
        }
    }
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as Player
        val clickedItem = event.currentItem

        if (event.view.title == "Select Player" && clickedItem?.type == Material.PLAYER_HEAD) {
            val skullMeta = clickedItem.itemMeta as? SkullMeta
            val clickedPlayer = skullMeta?.owningPlayer
            if (clickedPlayer != null && clickedPlayer.isOnline) {
                selectedTeleportMode[player] = TeleportMode.PlayerTarget(clickedPlayer as Player)
                player.closeInventory()
            } else {
                player.sendMessage("${org.bukkit.ChatColor.RED}Selected player is not online!")
            }
            return
        }

        if (event.view.title != "Select Teleport Mode") return
        event.isCancelled = true

        when (clickedItem?.type) {
            Material.COMPASS -> {
                selectedTeleportMode[player] = TeleportMode.Home
                player.closeInventory()
            }
            Material.CLOCK -> {
                selectedTeleportMode[player] = TeleportMode.Random
                player.closeInventory()
            }
            Material.PLAYER_HEAD -> {
                player.openInventory(createPlayerHeadsInventory(player.world, player))
            }
            else -> {}
        }
    }

    private fun getAdjustedPlayerLocation(target: Player): Location {
        val direction = target.location.direction
        direction.y = 0.0
        if (direction.lengthSquared() < 1.0) {
            direction.normalize()
        }

        val portalSpawnOffset = direction.clone().multiply(-3.0) // Move 3 blocks away in the opposite horizontal direction
        val portalSpawnLocation = target.location.clone().add(portalSpawnOffset)

        val blockBehindPlayer = target.location.subtract(target.location.direction).block
        val blockAbovePlayer = blockBehindPlayer.getRelative(BlockFace.UP)

        if (blockAbovePlayer.isEmpty && !blockAbovePlayer.isLiquid) {
            return blockAbovePlayer.location
        }

        return portalSpawnLocation
    }



    private fun createTeleportPortal(player: Player, entryLocation: Location, exitLocation: Location) {
        val wandItem = TeleportWandItem()
        wandItem.createTeleportPortal(player, entryLocation, exitLocation)
    }
    private fun getTargetLocation(player: Player, blockFace: BlockFace?): Location {
        val clickedBlock = player.getTargetBlockExact(4)
        return when {
            clickedBlock != null && blockFace == BlockFace.UP -> clickedBlock.location.add(0.5, 2.0, 0.5)
            clickedBlock != null -> clickedBlock.location.add(0.5, 0.0, 0.5)
            else -> player.eyeLocation.add(player.location.direction.multiply(4))
        }
    }
    private fun isSafeSpawnLocation(location: Location): Boolean {
        val blockType = location.block.type
        val blockBelowType = location.add(0.0, -1.0, 0.0).block.type
        val blockAboveType = location.add(0.0, 1.0, 0.0).block.type

        return blockType == Material.AIR && blockAboveType == Material.AIR && blockBelowType.isSolid && blockBelowType != Material.WATER && blockBelowType != Material.LAVA
    }
    private fun handleModeSelection(player: Player, mode: TeleportMode) {
        val targetLocation = getTargetLocation(player, lastInteractedBlockFace[player])
        val exitLocation = when (mode) {
            is TeleportMode.Home -> {
                if (player.world.environment == World.Environment.NETHER || player.world.environment == World.Environment.THE_END) {
                    player.server.worlds.firstOrNull { it.environment == World.Environment.NORMAL }?.spawnLocation
                } else {
                    player.bedSpawnLocation ?: player.world.spawnLocation
                }
            }
            is TeleportMode.Random -> getRandomLandLocation(player)
            is TeleportMode.PlayerTarget -> getAdjustedPlayerLocation(mode.target)
        }
        createTeleportPortal(player, targetLocation, exitLocation!!)
    }

    private fun createTeleportModeSelectionInventory(player: Player): Inventory = Bukkit.createInventory(player, 9, "Select Teleport Mode").apply {
        setItem(3, ItemStack(Material.COMPASS).apply {
            itemMeta = setDisplayName(itemMeta!!, "Home")
        })
        setItem(4, ItemStack(Material.CLOCK).apply {
            itemMeta = setDisplayName(itemMeta!!, colorize("Random"))
        })
        // 0 1 2 3 4 5 6 7 8
        setItem(5, ItemStack(Material.PLAYER_HEAD).apply {
            itemMeta = setDisplayName(itemMeta!!, "${ChatColor.GOLD}Player")
        })
    }
    private fun createPlayerHeadsInventory(world: World, viewer: Player): Inventory {
        val players = world.players
        val invSize = (ceil(players.size / 9.0) * 9).toInt()
        val inv = Bukkit.createInventory(null, invSize, "Select Player")

        for (player in players) {
            if(player != viewer) {
                val playerHead = ItemStack(Material.PLAYER_HEAD).apply {
                    itemMeta = setDisplayName(itemMeta!!, player.name, player)
                }
                inv.addItem(playerHead)
            }
        }
        return inv
    }
    private fun getRandomLandLocation(player: Player): Location {
        val maxRetries = 2
        var retries = 0
        var randomLocation: Location = player.location  // Initialize to player's current location

        while (retries < maxRetries) {
            val distanceSteps = (20..100).random() * 10
            val angleInRadians = (0..(2 * Math.PI).toInt()).random()
            val randomX = player.location.x + distanceSteps * cos(angleInRadians.toDouble())
            val randomZ = player.location.z + distanceSteps * sin(angleInRadians.toDouble())
            val newY = player.world.getHighestBlockYAt(randomX.toInt(), randomZ.toInt()).toDouble() + 1
            randomLocation = Location(player.world, randomX, newY, randomZ)

            if (isSafeSpawnLocation(randomLocation)) {
                return randomLocation
            }

            retries++
        }

        return randomLocation
    }
}
