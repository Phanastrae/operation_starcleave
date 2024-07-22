package phanastrae.operation_starcleave.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;
import phanastrae.operation_starcleave.recipe.input.ItemStarbleachingRecipeInput;

public class ItemStarbleachingRecipe implements Recipe<ItemStarbleachingRecipeInput> {
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
    public boolean matches(ItemStarbleachingRecipeInput input, World world) {
        ItemStack stack = input.getStackInSlot(0);
        return ingredient.test(stack);
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return this.result;
    }

    @Override
    public ItemStack craft(ItemStarbleachingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
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

    public interface RecipeFactory<T extends ItemStarbleachingRecipe> {
        T create(Ingredient ingredient, float starbleachCost, ItemStack result, boolean isFillingRecipe);
    }

    public static class Serializer<T extends ItemStarbleachingRecipe> implements RecipeSerializer<T> {
        final ItemStarbleachingRecipe.RecipeFactory<T> recipeFactory;
        private final MapCodec<T> codec;
        private final PacketCodec<RegistryByteBuf, T> packetCodec;

        protected Serializer(ItemStarbleachingRecipe.RecipeFactory<T> recipeFactory) {
            this.recipeFactory = recipeFactory;
            this.codec = RecordCodecBuilder.mapCodec(
                    instance -> instance.group(
                            Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient),
                            Codec.FLOAT.optionalFieldOf("starbleach_cost", 1F).forGetter(recipe -> recipe.starbleachCost),
                            ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                            Codec.BOOL.optionalFieldOf("is_filling_recipe", false).forGetter(recipe -> recipe.isFillingRecipe)
                    ).apply(instance, recipeFactory::create)
            );
            this.packetCodec = PacketCodec.tuple(
                    Ingredient.PACKET_CODEC,
                    recipe -> recipe.ingredient,
                    PacketCodecs.FLOAT,
                    recipe -> recipe.starbleachCost,
                    ItemStack.PACKET_CODEC,
                    recipe -> recipe.result,
                    PacketCodecs.BOOL,
                    recipe -> recipe.isFillingRecipe,
                    recipeFactory::create
            );
        }

        @Override
        public MapCodec<T> codec() {
            return this.codec;
        }

        @Override
        public PacketCodec<RegistryByteBuf, T> packetCodec() {
            return this.packetCodec;
        }
    }
}
