package dev.tacobrando.arcanewonders.recipes

import dev.tacobrando.arcanewonders.ArcaneWondersPlugin
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.Material
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ItemStack

object WandRecipe {
    fun register(item: ItemStack, key: String, shape: List<String>, ingredients: Map<Char, Material>) {
        val recipeKey = NamespacedKey(ArcaneWondersPlugin.instance, key)

        if (Bukkit.getRecipe(recipeKey) == null) {
            val recipe = ShapedRecipe(recipeKey, item)

            recipe.shape(*shape.toTypedArray())
            ingredients.forEach { (char, material) ->
                recipe.setIngredient(char, material)
            }

            Bukkit.addRecipe(recipe)
        }
    }
}
