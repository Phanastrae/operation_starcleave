package phanastrae.operation_starcleave.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.ItemLike;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.services.XPlatInterface;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

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
                STELLARUBBLE_MIX,
                STELLAR_PATH,
                STELLAR_FARMLAND,

                STELLAR_BRICKS,
                STELLAR_BRICK_STAIRS,
                STELLAR_BRICK_SLAB,
                STELLAR_BRICK_WALL,

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
                TALL_HOLY_MOSS,

                HOLY_STRANDS,
                BLESSED_CLOTH,

                BLESSED_CLOTH_BLOCK,
                BLESSED_CLOTH_STAIRS,
                BLESSED_CLOTH_SLAB,
                BLESSED_CLOTH_CARPET,

                BLESSED_CLOTH_PADDING,
                BLESSED_CLOTH_PADDING_STAIRS,
                BLESSED_CLOTH_PADDING_SLAB,
                BLESSED_CLOTH_CARPET_PADDING,

                BLESSED_CLOTH_CURTAIN,

                BLESSED_BED,

                GREAT_TREES_CARE,
                RED_MOURNER,
                ANGELCLAW,
                ELDROSE,
                WITCHGLARE,
                BLUE_DREAMER,
                DRAGONS_MAW,

                STARCLOVERS,

                STARCLOVER_BUSH,
                STARFLOWER,

                STARDUST_BLOCK,
                STARDUST_CLUSTER,

                STARDUST_BRICKS,
                STARDUST_BRICK_STAIRS,
                STARDUST_BRICK_SLAB,
                STARDUST_BRICK_WALL,

                FELLCRUST,
                FELLCRUST_STAIRS,
                FELLCRUST_SLAB,
                FELLCRUST_WALL,

                CHISELED_FELLCRUST,

                SMOOTH_FELLCRUST,
                SMOOTH_FELLCRUST_STAIRS,
                SMOOTH_FELLCRUST_SLAB,
                SMOOTH_FELLCRUST_WALL,

                CHISELED_SMOOTH_FELLCRUST,

                SMOOTH_FELLCRUST_BRICKS,
                SMOOTH_FELLCRUST_BRICK_STAIRS,
                SMOOTH_FELLCRUST_BRICK_SLAB,
                SMOOTH_FELLCRUST_BRICK_WALL,

                SMOOTH_FELLCRUST_PILLAR,

                CUT_FELLCRUST,
                CUT_FELLCRUST_STAIRS,
                CUT_FELLCRUST_SLAB,
                CUT_FELLCRUST_WALL,

                COBBLED_FELLCRUST,
                COBBLED_FELLCRUST_STAIRS,
                COBBLED_FELLCRUST_SLAB,
                COBBLED_FELLCRUST_WALL,

                POLISHED_FELLCRUST,
                POLISHED_FELLCRUST_STAIRS,
                POLISHED_FELLCRUST_SLAB,

                POLISHED_FELLCRUST_BRICKS,
                POLISHED_FELLCRUST_BRICK_STAIRS,
                POLISHED_FELLCRUST_BRICK_SLAB,
                POLISHED_FELLCRUST_BRICK_WALL,

                CUT_POLISHED_FELLCRUST,
                CUT_POLISHED_FELLCRUST_STAIRS,
                CUT_POLISHED_FELLCRUST_SLAB,

                ASTERUBBLE_PIECES,
                ASTERUBBLE,
                ASTERUBBLE_STAIRS,
                ASTERUBBLE_SLAB,
                ASTERUBBLE_WALL,

                SKYSHELL,

                SKYSHELL_BLOCK,
                SKYSHELL_STAIRS,
                SKYSHELL_SLAB,
                SKYSHELL_WALL,

                STARBLEACHED_LOG,
                STARBLEACHED_WOOD,

                STARBLEACHED_LEAVES,
                STARBLEACHED_LEAF_LITTER,
                STARBLEACHED_LEAF_BUNCH,
                STARBLEACHED_LEAF_BUNCH_BLOCK,

                STARBLEACHED_TILES,
                STARBLEACHED_TILE_STAIRS,
                STARBLEACHED_TILE_SLAB,
                STARBLEACHED_TILE_WALL,

                STARBLEACHED_PRESSURE_PLATE,
                STARBLEACHED_BUTTON,

                CHISELED_STARBLEACHED_TILES,
                IMBUED_STARBLEACHED_TILES,

                STARTOUCHED_LOG,
                STARTOUCHED_WOOD,

                STARTOUCHED_PLANKS,
                STARTOUCHED_STAIRS,
                STARTOUCHED_SLAB,

                STARTOUCHED_FENCE,
                STARTOUCHED_FENCE_GATE,

                STARTOUCHED_DOOR,
                STARTOUCHED_TRAPDOOR,

                STARTOUCHED_PRESSURE_PLATE,
                STARTOUCHED_BUTTON,

                STARTOUCHED_SIGN,
                STARTOUCHED_HANGING_SIGN,

                STARTOUCHED_TORCH,

                STARBLEACHED_SAPLING,

                CELESTIAL_OPAL_BLOCK,
                CELESTIAL_OPAL_STAIRS,
                CELESTIAL_OPAL_SLAB,
                CELESTIAL_OPAL_WALL,

                BUDDING_CELESTIAL_OPAL,

                SMALL_CELESTIAL_OPAL_BUD,
                MEDIUM_CELESTIAL_OPAL_BUD,
                LARGE_CELESTIAL_OPAL_BUD,
                CELESTIAL_OPAL_CLUSTER,

                CELESTIAL_OPAL_SHARD,

                POLISHED_CELESTIAL_OPAL_BLOCK,
                POLISHED_CELESTIAL_OPAL_STAIRS,
                POLISHED_CELESTIAL_OPAL_SLAB,

                POLISHED_CELESTIAL_OPAL_BRICKS,
                POLISHED_CELESTIAL_OPAL_BRICK_STAIRS,
                POLISHED_CELESTIAL_OPAL_BRICK_SLAB,
                POLISHED_CELESTIAL_OPAL_BRICK_WALL,

                POLISHED_CELESTIAL_OPAL_PILLAR,

                BUBBLEGLOOM,
                BUBBLEGLOOM_STAIRS,
                BUBBLEGLOOM_SLAB,
                BUBBLEGLOOM_WALL,

                POLISHED_BUBBLEGLOOM,
                POLISHED_BUBBLEGLOOM_STAIRS,
                POLISHED_BUBBLEGLOOM_SLAB,
                POLISHED_BUBBLEGLOOM_WALL,

                CUT_POLISHED_BUBBLEGLOOM,
                CUT_POLISHED_BUBBLEGLOOM_STAIRS,
                CUT_POLISHED_BUBBLEGLOOM_SLAB,
                CUT_POLISHED_BUBBLEGLOOM_WALL,

                POLISHED_BUBBLEGLOOM_PILLAR,

                NUCLEOSYNTHESEED,
                NUCLEIC_FISSUREROOT,
                NUCLEIC_FISSURERIND,
                STRIPED_NUCLEIC_FISSUREROOT,
                STRIPED_NUCLEIC_FISSURERIND,
                NUCLEIC_FISSURELEAVES,

                OURANIC_CHIP,

                OURANIC_CHIP_BLOCK,
                OURANIC_CHIP_STAIRS,
                OURANIC_CHIP_SLAB,
                OURANIC_CHIP_WALL,

                CHISELED_OURANIC_CHIP_BLOCK,

                OURANIC_BRICKS,
                OURANIC_BRICK_STAIRS,
                OURANIC_BRICK_SLAB,
                OURANIC_BRICK_WALL,

                OURANIC_PILLAR,

                STARBLEACHED_PEARL,
                STARFRUIT,

                FIRMAMENT_REJUVENATOR,

                BISMUTH_PEGASUS_ARMOR,
                BISMUTH_BLASTER,
                BISBLAST_CANISTER,

                STARBLEACHED_PEARL_BLOCK,
                STELLAR_REPULSOR,

                HOLY_LEAF_PLATFORM,

                HOLLOWED_SAC,
                PHLOGISTON_SAC,
                SUBCAELIC_PHLOGLIGHT,

                MUCKY_SINGUTS,
                MUCKY_SINGUT_COIL,
                MUCKY_SINGUT_BLOCK,

                CLEANSED_SINGUTS,
                CLEANSED_SINGUT_COIL,
                CLEANSED_SINGUT_BLOCK,

                COAGULATED_PLASMA,
                PLASMA_ICE,
                STARBLEACH_BUCKET,
                PETRICHORIC_PLASMA_BUCKET,
                LIMESLAGGED_BUCKET,


                NUCLEAR_STORMCLOUD_BOTTLE,
                FIRMAMENT_MANIPULATOR,

                STARCLEAVER_GOLEM_SPAWN_EGG,
                SUBCAELIC_TORPEDO_SPAWN_EGG,
                SUBCAELIC_DUX_SPAWN_EGG,
                SINEATER_SPAWN_EGG,
                TRACTORBLOOM_SPAWN_EGG,
                HAMMERTAIL_GOLEM_SPAWN_EGG,
                PREECHER_SPAWN_EGG
        );

        // add starcleave enchantments to tab
        helper.forTabRun(OPERATION_STARCLEAVE_RESOURCE_KEY, ((itemDisplayParameters, output) -> {
            itemDisplayParameters.holders().lookup(Registries.ENCHANTMENT).ifPresent(lookup -> {
                lookup.listElements()
                        .filter(enchantmentReference -> enchantmentReference.key().location().getNamespace().equals(OperationStarcleave.MOD_ID))
                        .flatMap(
                                enchantmentRef -> IntStream.rangeClosed(enchantmentRef.value().getMinLevel(), enchantmentRef.value().getMaxLevel())
                                        .mapToObj(level -> EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantmentRef, level)))
                        )
                        .forEach(output::accept);
            });
        }));

        // Building Blocks
        helper.addAfter(Items.WARPED_BUTTON, BUILDING_BLOCKS,
                STARTOUCHED_LOG,
                STARTOUCHED_WOOD,

                STARTOUCHED_PLANKS,
                STARTOUCHED_STAIRS,
                STARTOUCHED_SLAB,

                STARTOUCHED_FENCE,
                STARTOUCHED_FENCE_GATE,

                STARTOUCHED_DOOR,
                STARTOUCHED_TRAPDOOR,

                STARTOUCHED_PRESSURE_PLATE,
                STARTOUCHED_BUTTON,

                STARBLEACHED_LOG,
                STARBLEACHED_WOOD,

                STARBLEACHED_TILES,
                STARBLEACHED_TILE_STAIRS,
                STARBLEACHED_TILE_SLAB,
                STARBLEACHED_TILE_WALL,

                STARBLEACHED_PRESSURE_PLATE,
                STARBLEACHED_BUTTON,

                CHISELED_STARBLEACHED_TILES,
                IMBUED_STARBLEACHED_TILES,

                NUCLEIC_FISSUREROOT,
                NUCLEIC_FISSURERIND,
                STRIPED_NUCLEIC_FISSUREROOT,
                STRIPED_NUCLEIC_FISSURERIND
        );
        helper.addAfter(Items.PURPUR_SLAB, BUILDING_BLOCKS,
                STELLAR_BRICKS,
                STELLAR_BRICK_STAIRS,
                STELLAR_BRICK_SLAB,
                STELLAR_BRICK_WALL,

                STARDUST_BRICKS,
                STARDUST_BRICK_STAIRS,
                STARDUST_BRICK_SLAB,
                STARDUST_BRICK_WALL,

                STARDUST_CLUSTER,

                FELLCRUST,
                FELLCRUST_STAIRS,
                FELLCRUST_SLAB,
                FELLCRUST_WALL,

                CHISELED_FELLCRUST,

                SMOOTH_FELLCRUST,
                SMOOTH_FELLCRUST_STAIRS,
                SMOOTH_FELLCRUST_SLAB,
                SMOOTH_FELLCRUST_WALL,

                CHISELED_SMOOTH_FELLCRUST,

                SMOOTH_FELLCRUST_BRICKS,
                SMOOTH_FELLCRUST_BRICK_STAIRS,
                SMOOTH_FELLCRUST_BRICK_SLAB,
                SMOOTH_FELLCRUST_BRICK_WALL,

                SMOOTH_FELLCRUST_PILLAR,

                CUT_FELLCRUST,
                CUT_FELLCRUST_STAIRS,
                CUT_FELLCRUST_SLAB,
                CUT_FELLCRUST_WALL,

                COBBLED_FELLCRUST,
                COBBLED_FELLCRUST_STAIRS,
                COBBLED_FELLCRUST_SLAB,
                COBBLED_FELLCRUST_WALL,

                POLISHED_FELLCRUST,
                POLISHED_FELLCRUST_STAIRS,
                POLISHED_FELLCRUST_SLAB,

                POLISHED_FELLCRUST_BRICKS,
                POLISHED_FELLCRUST_BRICK_STAIRS,
                POLISHED_FELLCRUST_BRICK_SLAB,
                POLISHED_FELLCRUST_BRICK_WALL,

                CUT_POLISHED_FELLCRUST,
                CUT_POLISHED_FELLCRUST_STAIRS,
                CUT_POLISHED_FELLCRUST_SLAB,

                ASTERUBBLE,
                ASTERUBBLE_STAIRS,
                ASTERUBBLE_SLAB,
                ASTERUBBLE_WALL,

                SKYSHELL_BLOCK,
                SKYSHELL_STAIRS,
                SKYSHELL_SLAB,
                SKYSHELL_WALL,

                STELLAR_TILES,
                STELLAR_TILE_SLAB,

                BUBBLEGLOOM,
                BUBBLEGLOOM_STAIRS,
                BUBBLEGLOOM_SLAB,
                BUBBLEGLOOM_WALL,

                POLISHED_BUBBLEGLOOM,
                POLISHED_BUBBLEGLOOM_STAIRS,
                POLISHED_BUBBLEGLOOM_SLAB,
                POLISHED_BUBBLEGLOOM_WALL,

                CUT_POLISHED_BUBBLEGLOOM,
                CUT_POLISHED_BUBBLEGLOOM_STAIRS,
                CUT_POLISHED_BUBBLEGLOOM_SLAB,
                CUT_POLISHED_BUBBLEGLOOM_WALL,

                POLISHED_BUBBLEGLOOM_PILLAR,

                BLESSED_CLOTH_BLOCK,
                BLESSED_CLOTH_STAIRS,
                BLESSED_CLOTH_SLAB,
                BLESSED_CLOTH_CARPET,

                BLESSED_CLOTH_PADDING,
                BLESSED_CLOTH_PADDING_STAIRS,
                BLESSED_CLOTH_PADDING_SLAB,
                BLESSED_CLOTH_CARPET_PADDING,

                BLESSED_CLOTH_CURTAIN,

                STARBLEACHED_LEAF_BUNCH_BLOCK
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
                STARFLAKED_BISMUTH_TRAPDOOR,

                CELESTIAL_OPAL_BLOCK,
                CELESTIAL_OPAL_STAIRS,
                CELESTIAL_OPAL_SLAB,
                CELESTIAL_OPAL_WALL,

                POLISHED_CELESTIAL_OPAL_BLOCK,
                POLISHED_CELESTIAL_OPAL_STAIRS,
                POLISHED_CELESTIAL_OPAL_SLAB,

                POLISHED_CELESTIAL_OPAL_BRICKS,
                POLISHED_CELESTIAL_OPAL_BRICK_STAIRS,
                POLISHED_CELESTIAL_OPAL_BRICK_SLAB,
                POLISHED_CELESTIAL_OPAL_BRICK_WALL,

                POLISHED_CELESTIAL_OPAL_PILLAR,

                OURANIC_CHIP_BLOCK,
                OURANIC_CHIP_STAIRS,
                OURANIC_CHIP_SLAB,
                OURANIC_CHIP_WALL,

                CHISELED_OURANIC_CHIP_BLOCK,

                OURANIC_BRICKS,
                OURANIC_BRICK_STAIRS,
                OURANIC_BRICK_SLAB,
                OURANIC_BRICK_WALL,

                OURANIC_PILLAR,

                MUCKY_SINGUT_BLOCK,
                MUCKY_SINGUT_COIL,
                CLEANSED_SINGUT_BLOCK,
                CLEANSED_SINGUT_COIL
        );

        // Natural
        helper.addAfter(Items.END_STONE, NATURAL_BLOCKS,
                STELLAR_SEDIMENT,
                STELLARUBBLE_MIX,
                STELLAR_PATH,
                STELLAR_FARMLAND,
                STELLAR_MULCH,
                HOLY_MOSS,
                STARDUST_BLOCK,
                ASTERUBBLE,
                FELLCRUST,
                BUBBLEGLOOM,

                COAGULATED_PLASMA,
                PLASMA_ICE

        );
        helper.addAfter(Items.AMETHYST_CLUSTER, NATURAL_BLOCKS,
                CELESTIAL_OPAL_BLOCK,
                BUDDING_CELESTIAL_OPAL,

                SMALL_CELESTIAL_OPAL_BUD,
                MEDIUM_CELESTIAL_OPAL_BUD,
                LARGE_CELESTIAL_OPAL_BUD,
                CELESTIAL_OPAL_CLUSTER
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
                STARBLEACHED_SAPLING,
                NUCLEOSYNTHESEED
        );
        helper.addAfter(Items.WITHER_ROSE, NATURAL_BLOCKS,
                GREAT_TREES_CARE,
                RED_MOURNER,
                ANGELCLAW,
                ELDROSE,
                WITCHGLARE,
                BLUE_DREAMER,
                DRAGONS_MAW
        );
        helper.addAfter(Items.PINK_PETALS, NATURAL_BLOCKS,
                STARCLOVERS
        );
        helper.addBefore(Items.SPORE_BLOSSOM, NATURAL_BLOCKS,
                STARBLEACHED_LEAF_LITTER
        );
        helper.addAfter(Items.PITCHER_PLANT, NATURAL_BLOCKS,
                STARCLOVER_BUSH,
                STARFLOWER
        );
        helper.addAfter(Items.HANGING_ROOTS, NATURAL_BLOCKS,
                MULCHBORNE_TUFT,
                SHORT_HOLY_MOSS,
                TALL_HOLY_MOSS
        );
        helper.addAfter(Items.NETHER_WART, NATURAL_BLOCKS,
                BISREED_ROOT
        );
        helper.addAfter(Items.PEARLESCENT_FROGLIGHT, NATURAL_BLOCKS,
                SUBCAELIC_PHLOGLIGHT
        );

        // Functional
        helper.addAfter(Items.SOUL_TORCH, FUNCTIONAL_BLOCKS,
                STARTOUCHED_TORCH
        );
        helper.addAfter(Items.PEARLESCENT_FROGLIGHT, FUNCTIONAL_BLOCKS,
                SUBCAELIC_PHLOGLIGHT
        );
        helper.addAfter(Items.WARPED_HANGING_SIGN, FUNCTIONAL_BLOCKS,
                STARTOUCHED_SIGN,
                STARTOUCHED_HANGING_SIGN
        );
        helper.addAfter(Items.DRAGON_HEAD, FUNCTIONAL_BLOCKS,
                NETHERITE_PUMPKIN
        );
        helper.add(FUNCTIONAL_BLOCKS,
                STARDUST_CLUSTER,
                STARBLEACHED_LEAVES,

                BLESSED_CLOTH_BLOCK,
                BLESSED_CLOTH_CARPET,

                BLESSED_CLOTH_PADDING,
                BLESSED_CLOTH_CARPET_PADDING
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
                STARBLEACH_BUCKET,
                PETRICHORIC_PLASMA_BUCKET,
                LIMESLAGGED_BUCKET
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
        helper.add(COMBAT,
                BISMUTH_BLASTER,
                BISBLAST_CANISTER
        );

        // Food and Drink
        helper.addAfter(Items.CHORUS_FRUIT, FOOD_AND_DRINKS,
                STARFRUIT
        );
        helper.addBefore(Items.COD, FOOD_AND_DRINKS,
                MUCKY_SINGUTS,
                CLEANSED_SINGUTS
        );
        helper.add(FOOD_AND_DRINKS,
                STARBLEACH_BOTTLE,
                SPLASH_STARBLEACH_BOTTLE
        );

        // Ingredients
        helper.addAfter(Items.NETHERITE_INGOT, INGREDIENTS,
                BISMUTH_FLAKE,
                STARFLAKED_BISMUTH,

                OURANIC_CHIP
        );
        helper.addAfter(Items.AMETHYST_SHARD, INGREDIENTS,
                CELESTIAL_OPAL_SHARD
        );
        helper.addAfter(Items.EXPERIENCE_BOTTLE, INGREDIENTS,
                STARBLEACH_BOTTLE,
                HOLY_STRANDS,
                BLESSED_CLOTH,
                ASTERUBBLE_PIECES,
                SKYSHELL,
                STARBLEACHED_LEAF_BUNCH
        );

        // Spawn Eggs
        helper.add(SPAWN_EGGS,
                STARCLEAVER_GOLEM_SPAWN_EGG,
                SUBCAELIC_TORPEDO_SPAWN_EGG,
                SUBCAELIC_DUX_SPAWN_EGG,
                SINEATER_SPAWN_EGG,
                TRACTORBLOOM_SPAWN_EGG,
                HAMMERTAIL_GOLEM_SPAWN_EGG,
                PREECHER_SPAWN_EGG
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
