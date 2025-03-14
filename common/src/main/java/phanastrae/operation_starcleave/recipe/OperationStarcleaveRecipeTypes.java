package phanastrae.operation_starcleave.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import phanastrae.operation_starcleave.OperationStarcleave;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class OperationStarcleaveRecipeTypes {

    public static final RecipeType<ItemStarbleachingRecipe> ITEM_STARBLEACHING = create("item_starbleaching");

    public static void init(BiConsumer<ResourceLocation, RecipeType<?>> r) {
        Consumer<RecipeType<?>> rwi = (rt) -> { // register with id from recipe type
            r.accept(id(rt.toString()), rt);
        };

        rwi.accept(ITEM_STARBLEACHING);
    }

    private static ResourceLocation id(String path) {
        return OperationStarcleave.id(path);
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
