package dev.tacobrando.arcanewonders.items

import dev.tacobrando.arcanewonders.ArcaneWondersPlugin
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

open class ArcaneWondersItem(itemType: ItemTypes) {
    val item: ItemStack = ItemStack(itemType.toMaterial()) // Use the enum to get the Material
    var meta: ItemMeta = item.itemMeta!!

    fun setDisplayName(name: String) {
        meta.setDisplayName(name)
        item.itemMeta = meta
    }

    fun setNbtTag(itemKey: String, value: Boolean) {
        val key = NamespacedKey(ArcaneWondersPlugin.instance, itemKey)
        meta.persistentDataContainer.set(key, PersistentDataType.BOOLEAN, value)
        item.itemMeta = meta
    }

    fun getNbtTag(itemKey: String): Boolean {
        val key = NamespacedKey(ArcaneWondersPlugin.instance, itemKey)
        return meta.persistentDataContainer.get(key, PersistentDataType.BOOLEAN) ?: false
    }

    fun hasNbtTag(itemStack: ItemStack, itemKey: String): Boolean {
        val itemMeta = itemStack.itemMeta
        val key = NamespacedKey(ArcaneWondersPlugin.instance, itemKey)
        return itemMeta?.persistentDataContainer?.get(key, PersistentDataType.BOOLEAN) ?: false
    }
    fun fetchItem() = item
}