package dev.tacobrando.arcanewonders

import dev.tacobrando.arcanewonders.items.wands.teleport.TeleportWandItem
import dev.tacobrando.arcanewonders.listeners.WandEventListener
import dev.tacobrando.arcanewonders.player.PlayerPortalTracker
import org.bukkit.plugin.java.JavaPlugin

class ArcaneWonders : JavaPlugin() {
    companion object {
        lateinit var instance: ArcaneWonders
    }

    init {
        instance = this
    }
    override fun onEnable() {
        initialize()
    }

    override fun onDisable() {}


    private fun initialize() {
        registerItems()
        registerListeners()
        PlayerPortalTracker()

        logger.info("ArcaneWonders Initialized")
    }

    private fun registerItems() {
        val teleportWandItem = TeleportWandItem()
        teleportWandItem.registerRecipe()
    }
    private fun registerListeners() {
        server.pluginManager.registerEvents(WandEventListener, instance)
    }
}
