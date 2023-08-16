package dev.tacobrando.arcanewonders

import dev.tacobrando.arcanewonders.items.wands.teleport.TeleportWandItem
import dev.tacobrando.arcanewonders.listeners.WandEventListener
import dev.tacobrando.arcanewonders.player.PlayerPortalTracker
import org.bukkit.plugin.java.JavaPlugin

class ArcaneWondersPlugin : JavaPlugin() {
    companion object {
        lateinit var instance: ArcaneWondersPlugin
    }

    init {
        instance = this
    }
    override fun onEnable() {
        initialize()
    }

    override fun onDisable() {
        logger.info("Magic Fading...")
    }


    private fun initialize() {
        registerItems()
        registerListeners()
        PlayerPortalTracker()

        logger.info("Magic Restored!")
    }

    private fun registerItems() {
        val teleportWandItem = TeleportWandItem()
        teleportWandItem.registerRecipe()
    }
    private fun registerListeners() {
        server.pluginManager.registerEvents(WandEventListener, instance)
    }
}
