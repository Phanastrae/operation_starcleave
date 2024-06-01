package phanastrae.operation_starcleave.recipe;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import phanastrae.operation_starcleave.OperationStarcleave;

public class OperationStarcleaveRecipeTypes {

    public static final RecipeType<ItemStarbleachingRecipe> ITEM_STARBLEACHING = create("item_starbleaching");

    public static void init() {
        register(ITEM_STARBLEACHING);
    }

    static <T extends RecipeType<?>> void register(T recipeType) {
        Registry.register(Registries.RECIPE_TYPE, OperationStarcleave.id(recipeType.toString()), recipeType);
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
