package phanastrae.operation_starcleave.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
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
    public ItemStack getToastSymbol() {
        return OperationStarcleaveItems.STARBLEACH_BOTTLE.getDefaultInstance();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public boolean matches(ItemStarbleachingRecipeInput input, Level world) {
        ItemStack stack = input.getItem(0);
        return ingredient.test(stack);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registriesLookup) {
        return this.result;
    }

    @Override
    public ItemStack assemble(ItemStarbleachingRecipeInput input, HolderLookup.Provider lookup) {
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

    public static int getConsumedStarbleach(RandomSource random, float cost) {
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
        private final StreamCodec<RegistryFriendlyByteBuf, T> packetCodec;

        protected Serializer(ItemStarbleachingRecipe.RecipeFactory<T> recipeFactory) {
            this.recipeFactory = recipeFactory;
            this.codec = RecordCodecBuilder.mapCodec(
                    instance -> instance.group(
                            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient),
                            Codec.FLOAT.optionalFieldOf("starbleach_cost", 1F).forGetter(recipe -> recipe.starbleachCost),
                            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                            Codec.BOOL.optionalFieldOf("is_filling_recipe", false).forGetter(recipe -> recipe.isFillingRecipe)
                    ).apply(instance, recipeFactory::create)
            );
            this.packetCodec = StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC,
                    recipe -> recipe.ingredient,
                    ByteBufCodecs.FLOAT,
                    recipe -> recipe.starbleachCost,
                    ItemStack.STREAM_CODEC,
                    recipe -> recipe.result,
                    ByteBufCodecs.BOOL,
                    recipe -> recipe.isFillingRecipe,
                    recipeFactory::create
            );
        }

        @Override
        public MapCodec<T> codec() {
            return this.codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
            return this.packetCodec;
        }
    }
}
