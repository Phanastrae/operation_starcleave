package phanastrae.operation_starcleave.recipe.input;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public class ItemStarbleachingRecipeInput implements RecipeInput {

    private final ItemStack stack;

    public ItemStarbleachingRecipeInput(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.stack;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return this.stack.isEmpty();
    }
}
