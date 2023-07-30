package dev.tacobrando.arcanewonders.items

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta

open class ArcaneWondersItem(val material: Material) {
    val item: ItemStack = ItemStack(material)
    var meta: ItemMeta = item.itemMeta!!

    fun setDisplayName(name: String) {
        meta.setDisplayName(name)
        item.itemMeta = meta
    }

    fun setNbtTag(key: String, value: String) {
        val meta = item.itemMeta as Damageable
        meta.setDamage(value.hashCode())
        item.itemMeta = meta
    }

    fun fetchItem():ItemStack {
        return item
    }
}