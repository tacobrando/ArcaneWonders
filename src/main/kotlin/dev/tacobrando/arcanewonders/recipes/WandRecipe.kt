package dev.tacobrando.arcanewonders.recipes

import dev.tacobrando.arcanewonders.ArcaneWonders
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.Material
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ItemStack

object WandRecipe {
    fun register(item: ItemStack, key: String) {
        val verticalKey = NamespacedKey(ArcaneWonders.instance, key)

        if(Bukkit.getRecipe(verticalKey) == null) {
            val verticalRecipe = ShapedRecipe(verticalKey, item)
        
            verticalRecipe.shape("E", "B", "B")
            verticalRecipe.setIngredient('B', Material.BLAZE_ROD)
            verticalRecipe.setIngredient('E', Material.ENDER_PEARL)
            
            Bukkit.addRecipe(verticalRecipe)
        }
    }
}