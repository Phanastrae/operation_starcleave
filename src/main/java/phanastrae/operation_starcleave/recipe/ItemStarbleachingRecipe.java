package phanastrae.operation_starcleave.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;

public class ItemStarbleachingRecipe implements Recipe<Inventory> {
    protected final Ingredient ingredient;
    protected final float starbleachCost;
    protected final ItemStack result;
    protected final boolean isFillingRecipe;

    public ItemStarbleachingRecipe(Ingredient ingredient, float starbleachCost, ItemStack result, boolean isFillingRecipe) {
        this.ingredient = ingredient;
        this.starbleachCost = starbleachCost;
        this.result = result;
        this.isFillingRecipe = isFillingRecipe;
    }

    @Override
    public RecipeType<?> getType() {
        return OperationStarcleaveRecipeTypes.ITEM_STARBLEACHING;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return OperationStarcleaveRecipeSerializers.ITEM_STARBLEACHING;
    }

    @Override
    public ItemStack createIcon() {
        return OperationStarcleaveItems.STARBLEACH_BOTTLE.getDefaultStack();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        if(inventory.size() < 1) return false;
        ItemStack stack = inventory.getStack(0);
        return ingredient.test(stack);
    }

    @Override
    public ItemStack getResult(DynamicRegistryManager registryManager) {
        return this.result;
    }

    @Override
    public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
        // do not use
        return ItemStack.EMPTY;
    }

    public ItemStack getOutputStack() {
        return this.result.copy();
    }

    public boolean getIsFillingRecipe() {
        return this.isFillingRecipe;
    }

    public int getRequiredStarbleachToAttemptCraft() {
        if(this.starbleachCost <= 0) return 0;
        return (int)Math.ceil(this.starbleachCost);
    }

    public float getStarbleachCost() {
        return this.starbleachCost;
    }

    public static int getConsumedStarbleach(Random random, float cost) {
        if(cost <= 0) return 0;

        // return randomly either floor(cost) or ceil(cost), with expectation equal to cost

        int minConsumed = (int)Math.floor(cost);
        float probabilityForBonusUnit = cost - minConsumed;
        if(probabilityForBonusUnit == 0) {
            // if starbleachCost is an integer just return it directly
            return minConsumed;
        }

        boolean consumeAdditionalUnit = random.nextFloat() < probabilityForBonusUnit;
        if(consumeAdditionalUnit) {
            return minConsumed + 1;
        } else {
            return minConsumed;
        }
    }

    public static class Serializer implements RecipeSerializer<ItemStarbleachingRecipe> {
        private static final Codec<ItemStarbleachingRecipe> CODEC = createCodec();

        static Codec<ItemStarbleachingRecipe> createCodec() {
            return RecordCodecBuilder.create(
                    instance -> instance.group(
                            Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient),
                            Codecs.createStrictOptionalFieldCodec(Codec.FLOAT, "starbleach_cost", 1F).forGetter(recipe -> recipe.starbleachCost),
                            ItemStack.CUTTING_RECIPE_RESULT_CODEC.forGetter(recipe -> recipe.result),
                            Codecs.createStrictOptionalFieldCodec(Codec.BOOL, "is_filling_recipe", false).forGetter(recipe -> recipe.isFillingRecipe)
                    ).apply(instance, ItemStarbleachingRecipe::new)
            );
        }

        @Override
        public Codec<ItemStarbleachingRecipe> codec() {
            return CODEC;
        }

        @Override
        public void write(PacketByteBuf buf, ItemStarbleachingRecipe recipe) {
            recipe.ingredient.write(buf);
            buf.writeFloat(recipe.starbleachCost);
            buf.writeItemStack(recipe.result);
            buf.writeBoolean(recipe.isFillingRecipe);
        }

        public ItemStarbleachingRecipe read(PacketByteBuf buf) {
            Ingredient ingredient = Ingredient.fromPacket(buf);
            float starbleachCost = buf.readFloat();
            ItemStack output = buf.readItemStack();
            boolean isFillingRecipe = buf.readBoolean();
            return new ItemStarbleachingRecipe(ingredient, starbleachCost, output, isFillingRecipe);
        }
    }
}
