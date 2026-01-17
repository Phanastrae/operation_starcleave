package phanastrae.operation_starcleave.client.compat.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;
import phanastrae.operation_starcleave.recipe.ItemStarbleachingRecipe;
import phanastrae.operation_starcleave.recipe.OperationStarcleaveRecipeTypes;

@EmiEntrypoint
public class OperationStarcleaveEMIPlugin implements EmiPlugin {
    public static final ResourceLocation ITEM_STARBLEACHING_ICON = OperationStarcleave.id("textures/gui/emi_widgets.png");
    public static final EmiRenderable ITEM_STARBLEACHING_TAB_ICON = EmiStack.of(OperationStarcleaveItems.STARBLEACH_BOTTLE);
    public static final EmiRecipeCategory ITEM_STARBLEACHING = new EmiRecipeCategory(
            OperationStarcleave.id("item_starbleaching"),
            ITEM_STARBLEACHING_TAB_ICON,
            new EmiTexture(ITEM_STARBLEACHING_ICON, 0, 0, 16, 16)
    );

    @Override
    public void register(EmiRegistry emiRegistry) {
        emiRegistry.addCategory(ITEM_STARBLEACHING);

        emiRegistry.addWorkstation(ITEM_STARBLEACHING, EmiStack.of(Items.CAULDRON));
        emiRegistry.addWorkstation(ITEM_STARBLEACHING, EmiStack.of(OperationStarcleaveItems.STARBLEACH_BOTTLE));
        emiRegistry.addWorkstation(ITEM_STARBLEACHING, EmiStack.of(OperationStarcleaveItems.STARBLEACH_BUCKET));

        RecipeManager manager = emiRegistry.getRecipeManager();

        for (RecipeHolder<ItemStarbleachingRecipe> recipeHolder : manager.getAllRecipesFor(OperationStarcleaveRecipeTypes.ITEM_STARBLEACHING)) {
            emiRegistry.addRecipe(new ItemStarbleachingEMIRecipe(recipeHolder.id(), recipeHolder.value()));
        }
    }
}
