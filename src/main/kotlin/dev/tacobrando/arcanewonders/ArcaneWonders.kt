package dev.tacobrando.arcanewonders

import dev.tacobrando.arcanewonders.items.MagicWandItem
import dev.tacobrando.arcanewonders.items.wands.teleport.TeleportWandItem
import dev.tacobrando.arcanewonders.listeners.WandEventListener
import org.bukkit.plugin.java.JavaPlugin

class ArcaneWonders : JavaPlugin() {
    companion object {
        lateinit var instance: ArcaneWonders
        val teleportWandItem = TeleportWandItem()
    }

    override fun onEnable() {
        instance = this
        registerListeners()
        registerItems()
        logger.info("ArcaneWonders Initialized")
    }

    override fun onDisable() {}

    private fun registerItems() {
        teleportWandItem.registerRecipe()
    }

    private fun registerListeners() {
        server.pluginManager.registerEvents(WandEventListener, instance)
    }
}
