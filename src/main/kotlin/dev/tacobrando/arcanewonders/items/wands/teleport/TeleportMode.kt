package dev.tacobrando.arcanewonders.items.wands.teleport

sealed class TeleportMode {
    data object Home : TeleportMode()
    data object Random : TeleportMode()
    data class PlayerTarget(val target: org.bukkit.entity.Player) : TeleportMode()
}