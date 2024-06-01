package phanastrae.operation_starcleave.recipe;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import phanastrae.operation_starcleave.OperationStarcleave;

public class OperationStarcleaveRecipeSerializers {

    public static RecipeSerializer<ItemStarbleachingRecipe> ITEM_STARBLEACHING = new ItemStarbleachingRecipe.Serializer();

    public static void init() {
        register(ITEM_STARBLEACHING, "item_starbleaching");
    }

    static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(S serializer, String name) {
        return Registry.register(Registries.RECIPE_SERIALIZER, OperationStarcleave.id(name), serializer);
    }
}
