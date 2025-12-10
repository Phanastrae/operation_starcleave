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

import java.util.Collection;
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

    public static final ResourceKey<CreativeModeTab> OPERATION_STARCLEAVE_RESOURCE_KEY = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), OperationStarcleave.id("operation_starcleave"));

    public static final CreativeModeTab OPERATION_STARCLEAVE_TAB = XPlatInterface.INSTANCE.createCreativeModeTabBuilder()
            .icon(OperationStarcleaveItems.NETHERITE_PUMPKIN::getDefaultInstance)
            .title(Component.translatable("itemGroup.operation_starcleave.group"))
            .build();

    public static void init(BiConsumer<ResourceLocation, CreativeModeTab> r) {
        r.accept(id("operation_starcleave"), OPERATION_STARCLEAVE_TAB);
    }

    public static void setupEntries(Helper helper) {
        // Operation: Starcleave Tab
        helper.add(OPERATION_STARCLEAVE_RESOURCE_KEY,
                NETHERITE_PUMPKIN,
                STARCLEAVER_GOLEM_BUCKET,

                STARBLEACH_BOTTLE,
                SPLASH_STARBLEACH_BOTTLE,

                STELLAR_SEDIMENT,
                STELLAR_PATH,
                STELLAR_FARMLAND,

                BISREED_ROOT,
                BISMUTH_FLAKE,
                STARFLAKED_BISMUTH,

                STARFLAKED_BISMUTH_BLOCK,
                STARFLAKED_BISMUTH_SLAB,
                CHISELED_STARFLAKED_BISMUTH_BLOCK,

                STARFLAKED_BISMUTH_PILLAR,

                STARFLAKED_BISMUTH_BRICKS,
                STARFLAKED_BISMUTH_BRICK_STAIRS,
                STARFLAKED_BISMUTH_BRICK_SLAB,
                STARFLAKED_BISMUTH_BRICK_WALL,
                CHISELED_STARFLAKED_BISMUTH_BRICKS,

                STARFLAKED_BISMUTH_TILES,
                STARFLAKED_BISMUTH_TILE_STAIRS,
                STARFLAKED_BISMUTH_TILE_SLAB,
                STARFLAKED_BISMUTH_TILE_WALL,

                STARFLAKED_BISMUTH_MOSAIC,
                STARFLAKED_BISMUTH_MOSAIC_STAIRS,
                STARFLAKED_BISMUTH_MOSAIC_SLAB,
                STARFLAKED_BISMUTH_MOSAIC_WALL,

                STARFLAKED_BISMUTH_DOOR,
                STARFLAKED_BISMUTH_TRAPDOOR,

                STELLAR_TILES,
                STELLAR_TILE_SLAB,

                STELLAR_MULCH,
                MULCHBORNE_TUFT,

                HOLY_MOSS,
                SHORT_HOLY_MOSS,

                HOLY_STRANDS,
                BLESSED_CLOTH,

                BLESSED_CLOTH_BLOCK,
                BLESSED_CLOTH_CARPET,
                BLESSED_CLOTH_CURTAIN,

                BLESSED_BED,

                STARDUST_BLOCK,
                STARDUST_CLUSTER,

                STARDUST_BRICKS,
                STARDUST_BRICK_STAIRS,
                STARDUST_BRICK_SLAB,
                STARDUST_BRICK_WALL,

                STARBLEACHED_LOG,
                STARBLEACHED_WOOD,

                STARBLEACHED_LEAVES,

                STARBLEACHED_TILES,
                STARBLEACHED_TILE_STAIRS,
                STARBLEACHED_TILE_SLAB,
                STARBLEACHED_TILE_WALL,

                CHISELED_STARBLEACHED_TILES,
                IMBUED_STARBLEACHED_TILES,

                NUCLEOSYNTHESEED,
                NUCLEIC_FISSUREROOT,
                NUCLEIC_FISSURERIND,
                STRIPED_NUCLEIC_FISSUREROOT,
                STRIPED_NUCLEIC_FISSURERIND,
                NUCLEIC_FISSURELEAVES,

                STARBLEACHED_PEARL,
                STARFRUIT,

                FIRMAMENT_REJUVENATOR,

                BISMUTH_PEGASUS_ARMOR,

                STARBLEACHED_PEARL_BLOCK,
                STELLAR_REPULSOR,

                HOLLOWED_SAC,
                PHLOGISTON_SAC,
                SUBCAELIC_PHLOGLIGHT,

                COAGULATED_PLASMA,
                PLASMA_ICE,
                PETRICHORIC_PLASMA_BUCKET,


                NUCLEAR_STORMCLOUD_BOTTLE,
                FIRMAMENT_MANIPULATOR,

                STARCLEAVER_GOLEM_SPAWN_EGG,
                SUBCAELIC_TORPEDO_SPAWN_EGG,
                SUBCAELIC_DUX_SPAWN_EGG
        );

        // Building Blocks
        helper.addAfter(Items.WARPED_BUTTON, BUILDING_BLOCKS,
                STARBLEACHED_LOG,
                STARBLEACHED_WOOD,

                STARBLEACHED_TILES,
                STARBLEACHED_TILE_STAIRS,
                STARBLEACHED_TILE_SLAB,
                STARBLEACHED_TILE_WALL,

                CHISELED_STARBLEACHED_TILES,
                IMBUED_STARBLEACHED_TILES,

                NUCLEIC_FISSUREROOT,
                NUCLEIC_FISSURERIND,
                STRIPED_NUCLEIC_FISSUREROOT,
                STRIPED_NUCLEIC_FISSURERIND
        );
        helper.addAfter(Items.PURPUR_SLAB, BUILDING_BLOCKS,
                STARDUST_BRICKS,
                STARDUST_BRICK_STAIRS,
                STARDUST_BRICK_SLAB,
                STARDUST_BRICK_WALL,

                STARDUST_CLUSTER,

                STELLAR_TILES,
                STELLAR_TILE_SLAB,
                BLESSED_CLOTH_BLOCK,
                BLESSED_CLOTH_CARPET,
                BLESSED_CLOTH_CURTAIN
        );
        helper.addAfter(Items.WAXED_OXIDIZED_COPPER_BULB, BUILDING_BLOCKS,
                STARFLAKED_BISMUTH_BLOCK,
                STARFLAKED_BISMUTH_SLAB,
                CHISELED_STARFLAKED_BISMUTH_BLOCK,

                STARFLAKED_BISMUTH_PILLAR,

                STARFLAKED_BISMUTH_BRICKS,
                STARFLAKED_BISMUTH_BRICK_STAIRS,
                STARFLAKED_BISMUTH_BRICK_SLAB,
                STARFLAKED_BISMUTH_BRICK_WALL,
                CHISELED_STARFLAKED_BISMUTH_BRICKS,

                STARFLAKED_BISMUTH_TILES,
                STARFLAKED_BISMUTH_TILE_STAIRS,
                STARFLAKED_BISMUTH_TILE_SLAB,
                STARFLAKED_BISMUTH_TILE_WALL,

                STARFLAKED_BISMUTH_MOSAIC,
                STARFLAKED_BISMUTH_MOSAIC_STAIRS,
                STARFLAKED_BISMUTH_MOSAIC_SLAB,
                STARFLAKED_BISMUTH_MOSAIC_WALL,

                STARFLAKED_BISMUTH_DOOR,
                STARFLAKED_BISMUTH_TRAPDOOR
        );

        // Natural
        helper.addAfter(Items.END_STONE, NATURAL_BLOCKS,
                STELLAR_SEDIMENT,
                STELLAR_PATH,
                STELLAR_FARMLAND,
                STELLAR_MULCH,
                HOLY_MOSS,
                STARDUST_BLOCK,

                COAGULATED_PLASMA,
                PLASMA_ICE

        );
        helper.addAfter(Items.WARPED_STEM, NATURAL_BLOCKS,
                STARBLEACHED_LOG,
                NUCLEIC_FISSUREROOT
        );
        helper.addAfter(Items.FLOWERING_AZALEA_LEAVES, NATURAL_BLOCKS,
                STARBLEACHED_LEAVES,
                NUCLEIC_FISSURELEAVES
        );
        helper.addAfter(Items.FLOWERING_AZALEA, NATURAL_BLOCKS,
                NUCLEOSYNTHESEED
        );
        helper.addAfter(Items.HANGING_ROOTS, NATURAL_BLOCKS,
                MULCHBORNE_TUFT,
                SHORT_HOLY_MOSS
        );
        helper.addAfter(Items.NETHER_WART, NATURAL_BLOCKS,
                BISREED_ROOT
        );
        helper.addAfter(Items.PEARLESCENT_FROGLIGHT, NATURAL_BLOCKS,
                SUBCAELIC_PHLOGLIGHT
        );

        // Functional
        helper.addAfter(Items.PEARLESCENT_FROGLIGHT, FUNCTIONAL_BLOCKS,
                SUBCAELIC_PHLOGLIGHT
        );
        helper.addAfter(Items.DRAGON_HEAD, FUNCTIONAL_BLOCKS,
                NETHERITE_PUMPKIN
        );
        helper.add(FUNCTIONAL_BLOCKS,
                STARDUST_CLUSTER,
                STARBLEACHED_LEAVES,
                BLESSED_CLOTH_BLOCK,
                BLESSED_CLOTH_CARPET
        );
        helper.addAfter(Items.PINK_BED, FUNCTIONAL_BLOCKS,
                BLESSED_BED
        );
        helper.add(FUNCTIONAL_BLOCKS,
                NUCLEOSYNTHESEED
        );

        // Redstone
        helper.add(REDSTONE_BLOCKS,
                STARBLEACHED_PEARL_BLOCK,
                STELLAR_REPULSOR
        );

        // Tools
        helper.addAfter(Items.BUCKET, TOOLS_AND_UTILITIES,
                STARCLEAVER_GOLEM_BUCKET
        );
        helper.addAfter(Items.LAVA_BUCKET, TOOLS_AND_UTILITIES,
                PETRICHORIC_PLASMA_BUCKET
        );
        helper.add(TOOLS_AND_UTILITIES,
                STARBLEACHED_PEARL,
                HOLLOWED_SAC,
                PHLOGISTON_SAC,
                FIRMAMENT_REJUVENATOR,

                NUCLEAR_STORMCLOUD_BOTTLE,
                FIRMAMENT_MANIPULATOR
        );

        // Combat
        helper.addBefore(Items.TURTLE_HELMET, COMBAT,
                NETHERITE_PUMPKIN
        );
        helper.addAfter(Items.DIAMOND_HORSE_ARMOR, COMBAT,
                BISMUTH_PEGASUS_ARMOR
        );

        // Food and Drink
        helper.addAfter(Items.CHORUS_FRUIT, FOOD_AND_DRINKS,
                STARFRUIT
        );
        helper.add(FOOD_AND_DRINKS,
                STARBLEACH_BOTTLE,
                SPLASH_STARBLEACH_BOTTLE
        );

        // Ingredients
        helper.addAfter(Items.NETHERITE_INGOT, INGREDIENTS,
                BISMUTH_FLAKE,
                STARFLAKED_BISMUTH
        );
        helper.addAfter(Items.EXPERIENCE_BOTTLE, INGREDIENTS,
                STARBLEACH_BOTTLE,
                HOLY_STRANDS,
                BLESSED_CLOTH
        );

        // Spawn Eggs
        helper.add(SPAWN_EGGS,
                STARCLEAVER_GOLEM_SPAWN_EGG,
                SUBCAELIC_TORPEDO_SPAWN_EGG,
                SUBCAELIC_DUX_SPAWN_EGG
        );

        // Operator
        if (helper.operatorTabEnabled()) {
            helper.add(OP_BLOCKS,
                    FIRMAMENT_MANIPULATOR,
                    NUCLEAR_STORMCLOUD_BOTTLE
            );
        }
    }

    public static ResourceLocation id(String path) {
        return OperationStarcleave.id(path);
    }

    public static ResourceKey<CreativeModeTab> createKey(String name) {
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

        public abstract void addBefore(ItemLike before, ResourceKey<CreativeModeTab> groupKey, ItemLike item);

        public abstract void addBefore(ItemStack before, ResourceKey<CreativeModeTab> groupKey, ItemStack item);

        public abstract void addBefore(ItemLike before, ResourceKey<CreativeModeTab> groupKey, ItemLike... items);

        public abstract void forTabRun(ResourceKey<CreativeModeTab> groupKey, BiConsumer<CreativeModeTab.ItemDisplayParameters, CreativeModeTab.Output> biConsumer);

        public abstract boolean operatorTabEnabled();
    }
}
