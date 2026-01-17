package phanastrae.operation_starcleave.client.compat.emi;

import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import phanastrae.operation_starcleave.fluid.OperationStarcleaveFluids;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;
import phanastrae.operation_starcleave.recipe.ItemStarbleachingRecipe;
import phanastrae.operation_starcleave.services.XPlatInterface;

public class ItemStarbleachingEMIRecipe extends BasicEmiRecipe {

    public final float starbleachCost;
    public final int minStarbleachCost;
    public final float chanceStarbleachCost;

    public ItemStarbleachingEMIRecipe(ResourceLocation id, ItemStarbleachingRecipe recipe) {
        super(OperationStarcleaveEMIPlugin.ITEM_STARBLEACHING, id, 76, 36);
        this.starbleachCost = recipe.getStarbleachCost();
        this.minStarbleachCost = Mth.floor(this.starbleachCost);
        this.chanceStarbleachCost = this.starbleachCost - this.minStarbleachCost;

        ItemStack outputStack = recipe.getOutputStack();
        this.inputs.add(EmiIngredient.of(recipe.getIngredients().getFirst()));
        this.outputs.add(EmiStack.of(outputStack));

        int starbleachBottleCapacity = XPlatInterface.INSTANCE.getBucketSize() / 4;
        boolean inputIsFluidContainer = outputStack.is(OperationStarcleaveItems.STARBLEACH_BOTTLE) || outputStack.is(OperationStarcleaveItems.STARBLEACH_BUCKET);

        if (this.minStarbleachCost > 0) {
            if (inputIsFluidContainer) {
                this.inputs.add(EmiStack.of(OperationStarcleaveFluids.STARBLEACH, (long) starbleachBottleCapacity * this.minStarbleachCost));
            } else {
                this.inputs.add(EmiStack.of(OperationStarcleaveItems.STARBLEACH_BOTTLE, this.minStarbleachCost));
                this.outputs.add(EmiStack.of(Items.GLASS_BOTTLE, this.minStarbleachCost));
            }
        }
        if (this.chanceStarbleachCost > 0) {
            if (inputIsFluidContainer) {
                this.inputs.add(EmiStack.of(OperationStarcleaveFluids.STARBLEACH, starbleachBottleCapacity).setChance(this.chanceStarbleachCost));
            } else {
                this.inputs.add(EmiStack.of(OperationStarcleaveItems.STARBLEACH_BOTTLE).setChance(this.chanceStarbleachCost));
                this.outputs.add(EmiStack.of(Items.GLASS_BOTTLE).setChance(this.chanceStarbleachCost));
            }
        }
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(inputs.getFirst(), 0, 0);

        widgets.addTexture(EmiTexture.EMPTY_ARROW, 26, 1);

        widgets.addSlot(outputs.getFirst(), 58, 0).recipeContext(this);

        for (int i = 1; i < inputs.size(); i++) {
            widgets.addSlot(inputs.get(i), 18 * (i - 1), 18);
        }
    }
}
