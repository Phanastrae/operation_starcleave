package phanastrae.operation_starcleave.recipe;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import phanastrae.operation_starcleave.OperationStarcleave;

public class OperationStarcleaveRecipeTypes {

    public static final RecipeType<ItemStarbleachingRecipe> ITEM_STARBLEACHING = create("item_starbleaching");

    public static void init() {
        register(ITEM_STARBLEACHING);
    }

    static <T extends RecipeType<?>> void register(T recipeType) {
        Registry.register(BuiltInRegistries.RECIPE_TYPE, OperationStarcleave.id(recipeType.toString()), recipeType);
    }

    static <T extends Recipe<?>> RecipeType<T> create(String id) {
        return new RecipeType<T>() {
            @Override
            public String toString() {
                return id;
            }
        };
    };
}
