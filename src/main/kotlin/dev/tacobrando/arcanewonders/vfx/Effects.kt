package dev.tacobrando.arcanewonders.vfx

import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.Location
import org.bukkit.entity.Firework
import org.bukkit.inventory.meta.FireworkMeta

object Effects {
    fun spawnFireworks(location: Location, effectType: FireworkEffect.Type, count: Int) {
        for(i in 1..count) {
            val firework: Firework = location.world!!.spawn(location, Firework::class.java)
            val fireworkMeta: FireworkMeta = firework.fireworkMeta

            fireworkMeta.addEffect(FireworkEffect.builder().with(effectType).withColor(Color.PURPLE).withFade(Color.BLUE).withFlicker().build())

            firework.fireworkMeta = fireworkMeta
            firework.detonate()
        }
    }
}