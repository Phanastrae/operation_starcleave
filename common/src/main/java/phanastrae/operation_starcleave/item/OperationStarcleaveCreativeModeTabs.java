package phanastrae.operation_starcleave.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.services.XPlatInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

import static phanastrae.operation_starcleave.item.OperationStarcleaveItems.*;

public class OperationStarcleaveCreativeModeTabs {
    public static final ResourceKey<CreativeModeTab> BUILDING_BLOCKS = createKey("building_blocks");
    public static final ResourceKey<CreativeModeTab> COLORED_BLOCKS = createKey("colored_blocks");
    public static final ResourceKey<CreativeModeTab> NATURAL_BLOCKS = createKey("natural_blocks");
    public static final ResourceKey<CreativeModeTab> FUNCTIONAL_BLOCKS = createKey("functional_blocks");
    public static final ResourceKey<CreativeModeTab> REDSTONE_BLOCKS = createKey("redstone_blocks");
    public static final ResourceKey<CreativeModeTab> TOOLS_AND_UTILITIES = createKey("tools_and_utilities");
    public static final ResourceKey<CreativeModeTab> COMBAT = createKey("combat");
    public static final ResourceKey<CreativeModeTab> FOOD_AND_DRINKS = createKey("food_and_drinks");
    public static final ResourceKey<CreativeModeTab> INGREDIENTS = createKey("ingredients");
    public static final ResourceKey<CreativeModeTab> SPAWN_EGGS = createKey("spawn_eggs");
    public static final ResourceKey<CreativeModeTab> OP_BLOCKS = createKey("op_blocks");

    public static final CreativeModeTab OPERATION_STARCLEAVE_TAB = XPlatInterface.INSTANCE.createCreativeModeTabBuilder()
            .icon(OperationStarcleaveItems.NETHERITE_PUMPKIN::getDefaultInstance)
            .title(Component.translatable("itemGroup.operation_starcleave.group"))
            .build();
    public static final ResourceKey<CreativeModeTab> OPERATION_STARCLEAVE_RESOURCE_KEY = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), OperationStarcleave.id("operation_starcleave"));

    private static final List<ItemStack> QUEUED_TAB_ITEMS = new ArrayList<>();

    public static void init(BiConsumer<ResourceLocation, CreativeModeTab> r) {
        r.accept(id("operation_starcleave"), OPERATION_STARCLEAVE_TAB);
    }

    public static void addItemToOperationStarcleaveTab(ItemLike item) {
        addItemToOperationStarcleaveTab(new ItemStack(item));
    }

    public static void addItemToOperationStarcleaveTab(ItemStack itemStack) {
        QUEUED_TAB_ITEMS.add(itemStack);
    }

    public static void setupEntries(Helper helper) {
        addQueuedItems(helper);

        // Building Blocks
        helper.addAfter(Items.WARPED_BUTTON, BUILDING_BLOCKS,
                STARBLEACHED_LOG,
                STARBLEACHED_WOOD);
        helper.add(BUILDING_BLOCKS,
                STARBLEACHED_TILES,
                STARBLEACHED_TILE_STAIRS,
                STARBLEACHED_TILE_SLAB,
                STARBLEACHED_TILE_WALL,
                CHISELED_STARBLEACHED_TILES,
                IMBUED_STARBLEACHED_TILES,
                STELLAR_TILES,
                STELLAR_TILE_SLAB,
                STARDUST_CLUSTER,
                BLESSED_CLOTH_BLOCK,
                BLESSED_CLOTH_CARPET,
                BLESSED_CLOTH_CURTAIN);

        // Natural
        helper.addAfter(Items.WARPED_STEM, NATURAL_BLOCKS,
                STARBLEACHED_LOG);
        helper.addAfter(Items.FLOWERING_AZALEA_LEAVES, NATURAL_BLOCKS,
                STARBLEACHED_LEAVES);
        helper.addAfter(Items.END_STONE, NATURAL_BLOCKS,
                HOLY_MOSS,
                STELLAR_SEDIMENT,
                STELLAR_MULCH,
                STELLAR_FARMLAND,
                STARDUST_BLOCK);
        helper.addAfter(Items.HANGING_ROOTS, NATURAL_BLOCKS,
                MULCHBORNE_TUFT,
                SHORT_HOLY_MOSS);

        // Functional
        helper.addAfter(Items.DRAGON_HEAD, FUNCTIONAL_BLOCKS,
                NETHERITE_PUMPKIN);
        helper.add(FUNCTIONAL_BLOCKS,
                STARDUST_CLUSTER,
                STARBLEACHED_LEAVES,
                BLESSED_CLOTH_BLOCK,
                BLESSED_CLOTH_CARPET);
        helper.addAfter(Items.PINK_BED, FUNCTIONAL_BLOCKS,
                BLESSED_BED);

        // Redstone
        helper.add(REDSTONE_BLOCKS,
                STARBLEACHED_PEARL_BLOCK,
                STELLAR_REPULSOR);

        // Tools
        helper.add(TOOLS_AND_UTILITIES,
                STARBLEACHED_PEARL,
                FIRMAMENT_REJUVENATOR,
                STARCLEAVER_GOLEM_BUCKET,
                HOLLOWED_SAC,
                PHLOGISTON_SAC);

        // Combat
        // TODO setup addBefore
        /*
        helper.addBefore(COMBAT, Items.TURTLE_HELMET,
                NETHERITE_PUMPKIN);
        */

        // Food and Drink
        helper.addAfter(Items.CHORUS_FRUIT, FOOD_AND_DRINKS,
                STARFRUIT);
        helper.add(FOOD_AND_DRINKS,
                STARBLEACH_BOTTLE,
                SPLASH_STARBLEACH_BOTTLE);

        // Ingredients
        helper.addAfter(Items.EXPERIENCE_BOTTLE, INGREDIENTS,
                STARBLEACH_BOTTLE,
                HOLY_STRANDS,
                BLESSED_CLOTH);

        // Spawn Eggs
        helper.add(SPAWN_EGGS,
                STARCLEAVER_GOLEM_SPAWN_EGG,
                SUBCAELIC_TORPEDO_SPAWN_EGG,
                SUBCAELIC_DUX_SPAWN_EGG);

        // Operator
        if(helper.operatorTabEnabled()) {
            helper.add(OP_BLOCKS,
                    FIRMAMENT_MANIPULATOR);
        }
    }

    private static void addQueuedItems(Helper helper) {
        helper.add(OPERATION_STARCLEAVE_RESOURCE_KEY, QUEUED_TAB_ITEMS);
    }

    private static ResourceLocation id(String path) {
        return OperationStarcleave.id(path);
    }

    private static ResourceKey<CreativeModeTab> createKey(String name) {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation.withDefaultNamespace(name));
    }

    public static abstract class Helper {
        public abstract void add(ResourceKey<CreativeModeTab> groupKey, ItemLike item);

        public abstract void add(ResourceKey<CreativeModeTab> groupKey, ItemLike... items);

        public abstract void add(ResourceKey<CreativeModeTab> groupKey, ItemStack item);

        public abstract void add(ResourceKey<CreativeModeTab> groupKey, Collection<ItemStack> items);

        public abstract void addAfter(ItemLike after, ResourceKey<CreativeModeTab> groupKey, ItemLike item);

        public abstract void addAfter(ItemStack after, ResourceKey<CreativeModeTab> groupKey, ItemStack item);

        public abstract void addAfter(ItemLike after, ResourceKey<CreativeModeTab> groupKey, ItemLike... items);

        public abstract void forTabRun(ResourceKey<CreativeModeTab> groupKey, BiConsumer<CreativeModeTab.ItemDisplayParameters, CreativeModeTab.Output> biConsumer);

        public abstract boolean operatorTabEnabled();
    }
}
