package dev.tacobrando.arcanewonders

import PlayerPortalTracker
import dev.tacobrando.arcanewonders.items.wands.teleport.TeleportWandItem
import dev.tacobrando.arcanewonders.listeners.WandEventListener
import org.bukkit.plugin.java.JavaPlugin


class ArcaneWonders : JavaPlugin() {
    companion object {
        lateinit var instance: ArcaneWonders
        val teleportWandItem = TeleportWandItem()
    }

    override fun onEnable() {
        initialize()
    }

    override fun onDisable() {}


    private fun initialize() {
        instance = this

        registerItems()
        registerListeners()

        PlayerPortalTracker

        logger.info("ArcaneWonders Initialized")
    }

    private fun registerItems() {
        teleportWandItem.registerRecipe()
    }
    private fun registerListeners() {
        server.pluginManager.registerEvents(WandEventListener, instance)
    }

}
