package phanastrae.operation_starcleave.recipe.input;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

public class ItemStarbleachingRecipeInput implements RecipeInput {

    private final ItemStack stack;

    public ItemStarbleachingRecipeInput(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.stack;
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return this.stack.isEmpty();
    }
}
