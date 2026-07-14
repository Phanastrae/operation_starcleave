package phanastrae.operation_starcleave.client.compat.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;
import phanastrae.operation_starcleave.recipe.ItemStarbleachingRecipe;
import phanastrae.operation_starcleave.recipe.OperationStarcleaveRecipeTypes;

import java.util.Collection;
import java.util.Comparator;

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

        Collection<RecipeHolder<ItemStarbleachingRecipe>> recipes = manager.getAllRecipesFor(OperationStarcleaveRecipeTypes.ITEM_STARBLEACHING)
                .stream().sorted(this::compare).toList();
        for (RecipeHolder<ItemStarbleachingRecipe> recipeHolder : recipes) {
            emiRegistry.addRecipe(new ItemStarbleachingEMIRecipe(recipeHolder.id(), recipeHolder.value()));
        }
    }

    protected int compare(RecipeHolder<ItemStarbleachingRecipe> holder1, RecipeHolder<ItemStarbleachingRecipe> holder2) {
        DefaultedRegistry<Item> itemRegistry = BuiltInRegistries.ITEM;

        ItemStarbleachingRecipe recipe1 = holder1.value();
        ItemStarbleachingRecipe recipe2 = holder2.value();

        // put filling recipes first
        int isFillingComparison = Boolean.compare(!recipe1.getIsFillingRecipe(), !recipe2.getIsFillingRecipe());
        if (isFillingComparison != 0) {
            return isFillingComparison;
        }

        Item item1 = recipe1.getOutputStack().getItem();
        Item item2 = recipe2.getOutputStack().getItem();

        ResourceLocation key1 = itemRegistry.getKey(item1);
        ResourceLocation key2 = itemRegistry.getKey(item2);

        // sort by namespace of output item, putting minecraft first, starcleave second, and anything else third
        int priorityNamespaceComparison = Comparator.<ResourceLocation>comparingInt(location -> {
            String namespace = location.getNamespace();
            if (namespace.equals("minecraft")) {
                return 2;
            } else if (namespace.equals(OperationStarcleave.MOD_ID)) {
                return 1;
            } else {
                return 0;
            }
        }).compare(key1, key2);
        if (priorityNamespaceComparison != 0) {
            return priorityNamespaceComparison;
        }

        // sort by namespace of output item
        int namespaceComparison = Comparator.comparing(ResourceLocation::getNamespace, String::compareTo).compare(key1, key2);
        if (namespaceComparison != 0) {
            return namespaceComparison;
        }

        // sort by registry id
        int idComparison = Integer.compare(
                itemRegistry.getId(item1),
                itemRegistry.getId(item2)
        );
        if (idComparison != 0) {
            return idComparison;
        }

        // sort by recipe id
        return key1.compareTo(key2);
    }
}
