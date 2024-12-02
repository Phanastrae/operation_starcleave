package phanastrae.operation_starcleave.recipe;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import phanastrae.operation_starcleave.OperationStarcleave;

public class OperationStarcleaveRecipeSerializers {

    public static RecipeSerializer<ItemStarbleachingRecipe> ITEM_STARBLEACHING = new ItemStarbleachingRecipe.Serializer<>(ItemStarbleachingRecipe::new);

    public static void init() {
        register(ITEM_STARBLEACHING, "item_starbleaching");
    }

    static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(S serializer, String name) {
        return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, OperationStarcleave.id(name), serializer);
    }
}
