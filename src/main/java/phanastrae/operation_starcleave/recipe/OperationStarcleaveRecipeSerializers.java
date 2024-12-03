package phanastrae.operation_starcleave.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import phanastrae.operation_starcleave.OperationStarcleave;

import java.util.function.BiConsumer;

public class OperationStarcleaveRecipeSerializers {

    public static RecipeSerializer<ItemStarbleachingRecipe> ITEM_STARBLEACHING = new ItemStarbleachingRecipe.Serializer<>(ItemStarbleachingRecipe::new);

    public static void init(BiConsumer<ResourceLocation, RecipeSerializer<?>> r) {
        r.accept(id("item_starbleaching"), ITEM_STARBLEACHING);
    }

    private static ResourceLocation id(String path) {
        return OperationStarcleave.id(path);
    }
}
