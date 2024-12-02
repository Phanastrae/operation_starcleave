package phanastrae.operation_starcleave.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;

import static net.minecraft.world.item.Rarity.EPIC;
import static net.minecraft.world.item.Rarity.RARE;

public class OperationStarcleaveItems {

    public static final Item NETHERITE_PUMPKIN = new NetheritePumpkinItem(OperationStarcleaveBlocks.NETHERITE_PUMPKIN, settings().rarity(Rarity.UNCOMMON).fireResistant());

    public static final Item STELLAR_SEDIMENT = blockItem(OperationStarcleaveBlocks.STELLAR_SEDIMENT);
    public static final Item STELLAR_MULCH = blockItem(OperationStarcleaveBlocks.STELLAR_MULCH);
    public static final Item STELLAR_FARMLAND = blockItem(OperationStarcleaveBlocks.STELLAR_FARMLAND);
    public static final Item MULCHBORNE_TUFT = blockItem(OperationStarcleaveBlocks.MULCHBORNE_TUFT);
    public static final Item HOLY_MOSS = blockItem(OperationStarcleaveBlocks.HOLY_MOSS);
    public static final Item SHORT_HOLY_MOSS = blockItem(OperationStarcleaveBlocks.SHORT_HOLY_MOSS);
    public static final Item STARDUST_BLOCK = blockItem(OperationStarcleaveBlocks.STARDUST_BLOCK);
    public static final Item STARDUST_CLUSTER = blockItem(OperationStarcleaveBlocks.STARDUST_CLUSTER);
    public static final Item STARBLEACHED_LOG = blockItem(OperationStarcleaveBlocks.STARBLEACHED_LOG);
    public static final Item STARBLEACHED_WOOD = blockItem(OperationStarcleaveBlocks.STARBLEACHED_WOOD);
    public static final Item STARBLEACHED_LEAVES = blockItem(OperationStarcleaveBlocks.STARBLEACHED_LEAVES);
    public static final Item STARBLEACHED_TILES = blockItem(OperationStarcleaveBlocks.STARBLEACHED_TILES);
    public static final Item STARBLEACHED_TILE_STAIRS = blockItem(OperationStarcleaveBlocks.STARBLEACHED_TILE_STAIRS);
    public static final Item STARBLEACHED_TILE_SLAB = blockItem(OperationStarcleaveBlocks.STARBLEACHED_TILE_SLAB);
    public static final Item STARBLEACHED_TILE_WALL = blockItem(OperationStarcleaveBlocks.STARBLEACHED_TILE_WALL);
    public static final Item CHISELED_STARBLEACHED_TILES = blockItem(OperationStarcleaveBlocks.CHISELED_STARBLEACHED_TILES);
    public static final Item IMBUED_STARBLEACHED_TILES = blockItem(OperationStarcleaveBlocks.IMBUED_STARBLEACHED_TILES);
    public static final Item STARBLEACHED_PEARL_BLOCK = blockItem(OperationStarcleaveBlocks.STARBLEACHED_PEARL_BLOCK);
    public static final Item STELLAR_TILES = blockItem(OperationStarcleaveBlocks.STELLAR_TILES);
    public static final Item STELLAR_TILE_SLAB = blockItem(OperationStarcleaveBlocks.STELLAR_TILE_SLAB);
    public static final Item STELLAR_REPULSOR = blockItem(OperationStarcleaveBlocks.STELLAR_REPULSOR);
    public static final Item BLESSED_CLOTH_BLOCK = blockItem(OperationStarcleaveBlocks.BLESSED_CLOTH_BLOCK);
    public static final Item BLESSED_CLOTH_CARPET = blockItem(OperationStarcleaveBlocks.BLESSED_CLOTH_CARPET);
    public static final Item BLESSED_CLOTH_CURTAIN = blockItem(OperationStarcleaveBlocks.BLESSED_CLOTH_CURTAIN);

    public static final Item BLESSED_BED = new BedItem(OperationStarcleaveBlocks.BLESSED_BED, settings(1, RARE));

    public static final Item STARCLEAVER_GOLEM_BUCKET = new StarcleaverGolemBucketItem(settings(1));
    public static final Item STARBLEACH_BOTTLE = new StarbleachBottleItem(settings(16).craftRemainder(Items.GLASS_BOTTLE).food(StarbleachBottleItem.STARBLEACH_BOTTLE));
    public static final Item SPLASH_STARBLEACH_BOTTLE = new SplashStarbleachBottleItem(settings(16));
    public static final Item STARBLEACHED_PEARL = new StarbleachedPearlItem(settings(16));
    public static final Item STARFRUIT = new StarfruitItem(settings().food(StarfruitItem.STARFRUIT));
    public static final Item HOLY_STRANDS = new Item(settings());
    public static final Item BLESSED_CLOTH = new Item(settings());
    public static final Item HOLLOWED_SAC = new HollowedSacItem(settings());
    public static final Item PHLOGISTON_SAC = new PhlogistonSacItem(settings());
    public static final Item FIRMAMENT_REJUVENATOR = new FirmamentRejuvenatorItem(settings(8, RARE));
    public static final Item FIRMAMENT_MANIPULATOR = new FirmamentManipulatorItem(settings(1, EPIC));
    public static final Item STARCLEAVER_GOLEM_SPAWN_EGG = spawnEggItem(OperationStarcleaveEntityTypes.STARCLEAVER_GOLEM, 0x292725, 0x61EDDF);
    public static final Item SUBCAELIC_TORPEDO_SPAWN_EGG = spawnEggItem(OperationStarcleaveEntityTypes.SUBCAELIC_TORPEDO, 0xDFDFDF, 0x1FAF7F);
    public static final Item SUBCAELIC_DUX_SPAWN_EGG = spawnEggItem(OperationStarcleaveEntityTypes.SUBCAELIC_DUX, 0xDFEF9F, 0x6FFFDF);

    public static final CreativeModeTab OPERATION_STARCLEAVE_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(NETHERITE_PUMPKIN))
            .title(Component.translatable("itemGroup.operation_starcleave.group"))
            .build();

    public static void init() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, OperationStarcleave.id("operation_starcleave"), OPERATION_STARCLEAVE_GROUP);

        regWithIG(NETHERITE_PUMPKIN, "netherite_pumpkin");
        regWithIG(STARCLEAVER_GOLEM_BUCKET, "starcleaver_golem_bucket");
        regWithIG(STELLAR_SEDIMENT, "stellar_sediment");
        regWithIG(STELLAR_MULCH, "stellar_mulch");
        regWithIG(STELLAR_FARMLAND, "stellar_farmland");
        regWithIG(MULCHBORNE_TUFT, "mulchborne_tuft");
        regWithIG(HOLY_MOSS, "holy_moss");
        regWithIG(SHORT_HOLY_MOSS, "short_holy_moss");
        regWithIG(STARDUST_BLOCK, "stardust_block");
        regWithIG(STARDUST_CLUSTER, "stardust_cluster");
        regWithIG(STARBLEACHED_LOG, "starbleached_log");
        regWithIG(STARBLEACHED_WOOD, "starbleached_wood");
        regWithIG(STARBLEACHED_LEAVES, "starbleached_leaves");
        regWithIG(STARBLEACHED_TILES, "starbleached_tiles");
        regWithIG(STARBLEACHED_TILE_SLAB, "starbleached_tile_slab");
        regWithIG(STARBLEACHED_TILE_STAIRS, "starbleached_tile_stairs");
        regWithIG(STARBLEACHED_TILE_WALL, "starbleached_tile_wall");
        regWithIG(CHISELED_STARBLEACHED_TILES, "chiseled_starbleached_tiles");
        regWithIG(IMBUED_STARBLEACHED_TILES, "imbued_starbleached_tiles");
        regWithIG(STARBLEACHED_PEARL_BLOCK, "starbleached_pearl_block");
        regWithIG(STELLAR_TILES, "stellar_tiles");
        regWithIG(STELLAR_TILE_SLAB, "stellar_tile_slab");
        regWithIG(STELLAR_REPULSOR, "stellar_repulsor");
        regWithIG(BLESSED_CLOTH_BLOCK, "blessed_cloth_block");
        regWithIG(BLESSED_CLOTH_CARPET, "blessed_cloth_carpet");
        regWithIG(BLESSED_CLOTH_CURTAIN, "blessed_cloth_curtain");
        regWithIG(BLESSED_BED, "blessed_bed");

        Item.BY_BLOCK.put(OperationStarcleaveBlocks.STARBLEACH_CAULDRON, Items.CAULDRON);

        regWithIG(STARBLEACH_BOTTLE, "starbleach_bottle");
        regWithIG(SPLASH_STARBLEACH_BOTTLE, "splash_starbleach_bottle");
        regWithIG(STARBLEACHED_PEARL, "starbleached_pearl");
        regWithIG(STARFRUIT, "starfruit");
        regWithIG(HOLY_STRANDS, "holy_strands");
        regWithIG(BLESSED_CLOTH, "blessed_cloth");
        regWithIG(HOLLOWED_SAC, "hollowed_sac");
        regWithIG(PHLOGISTON_SAC, "phlogiston_sac");
        regWithIG(FIRMAMENT_REJUVENATOR, "firmament_rejuvenator");
        regWithIG(FIRMAMENT_MANIPULATOR, "firmament_manipulator");

        regWithIG(STARCLEAVER_GOLEM_SPAWN_EGG, "starcleaver_golem_spawn_egg");
        regWithIG(SUBCAELIC_TORPEDO_SPAWN_EGG, "subcaelic_torpedo_spawn_egg");
        regWithIG(SUBCAELIC_DUX_SPAWN_EGG, "subcaelic_dux_spawn_egg");

        addToVanillaItemGroups();
    }

    public static void addToVanillaItemGroups() {
        // Building Blocks
        addItemsToGroupAfter(CreativeModeTabs.BUILDING_BLOCKS, Items.WARPED_BUTTON,
                STARBLEACHED_LOG,
                STARBLEACHED_WOOD);
        addItemsToGroup(CreativeModeTabs.BUILDING_BLOCKS,
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
        addItemToGroupAfter(CreativeModeTabs.NATURAL_BLOCKS, Items.WARPED_STEM,
                STARBLEACHED_LOG);
        addItemToGroupAfter(CreativeModeTabs.NATURAL_BLOCKS, Items.FLOWERING_AZALEA_LEAVES,
                STARBLEACHED_LEAVES);
        addItemsToGroupAfter(CreativeModeTabs.NATURAL_BLOCKS, Items.END_STONE,
                HOLY_MOSS,
                STELLAR_SEDIMENT,
                STELLAR_MULCH,
                STELLAR_FARMLAND,
                STARDUST_BLOCK);
        addItemsToGroupAfter(CreativeModeTabs.NATURAL_BLOCKS, Items.HANGING_ROOTS,
                MULCHBORNE_TUFT,
                SHORT_HOLY_MOSS);

        // Functional
        addItemToGroupAfter(CreativeModeTabs.FUNCTIONAL_BLOCKS, Items.DRAGON_HEAD,
                NETHERITE_PUMPKIN);
        addItemsToGroup(CreativeModeTabs.FUNCTIONAL_BLOCKS,
                STARDUST_CLUSTER,
                STARBLEACHED_LEAVES,
                BLESSED_CLOTH_BLOCK,
                BLESSED_CLOTH_CARPET);
        addItemToGroupAfter(CreativeModeTabs.FUNCTIONAL_BLOCKS, Items.PINK_BED,
                BLESSED_BED);

        // Redstone
        addItemsToGroup(CreativeModeTabs.REDSTONE_BLOCKS,
                STARBLEACHED_PEARL_BLOCK,
                STELLAR_REPULSOR);

        // Tools
        addItemsToGroup(CreativeModeTabs.TOOLS_AND_UTILITIES,
                STARBLEACHED_PEARL,
                FIRMAMENT_REJUVENATOR,
                STARCLEAVER_GOLEM_BUCKET,
                HOLLOWED_SAC,
                PHLOGISTON_SAC);

        // Combat
        addItemToGroupBefore(CreativeModeTabs.COMBAT, Items.TURTLE_HELMET,
                NETHERITE_PUMPKIN);

        // Food and Drink
        addItemToGroupAfter(CreativeModeTabs.FOOD_AND_DRINKS, Items.CHORUS_FRUIT,
                STARFRUIT);
        addItemsToGroup(CreativeModeTabs.FOOD_AND_DRINKS,
                STARBLEACH_BOTTLE,
                SPLASH_STARBLEACH_BOTTLE);

        // Ingredients
        addItemsToGroupAfter(CreativeModeTabs.INGREDIENTS, Items.EXPERIENCE_BOTTLE,
                STARBLEACH_BOTTLE,
                HOLY_STRANDS,
                BLESSED_CLOTH);

        // Spawn Eggs
        addItemsToGroup(CreativeModeTabs.SPAWN_EGGS,
                STARCLEAVER_GOLEM_SPAWN_EGG,
                SUBCAELIC_TORPEDO_SPAWN_EGG,
                SUBCAELIC_DUX_SPAWN_EGG);

        // Operator
        // Different method is used here to ensure items are only added if the operator tab is present
        CreativeModeTab operatorGroup = BuiltInRegistries.CREATIVE_MODE_TAB.get(CreativeModeTabs.OP_BLOCKS);
        if(operatorGroup != null) {
            ItemGroupEvents.MODIFY_ENTRIES_ALL.register((group, entries) -> {
                if (operatorGroup.equals(group)) {
                    entries.accept(FIRMAMENT_MANIPULATOR);
                }
            });
        }
    }

    // Register the item and add it to the Operation: Starcleave item group
    public static <T extends Item> void regWithIG(T item, String name) {
        register(item, name);
        addItemToGroup(ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), OperationStarcleave.id("operation_starcleave")), item);
    }

    public static <T extends Item> void register(T item, String name) {
        Registry.register(BuiltInRegistries.ITEM, OperationStarcleave.id(name), item);
    }

    public static <T extends Item> void addItemToGroup(ResourceKey<CreativeModeTab> group, T newItem) {
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.accept(newItem));
    }

    @SafeVarargs
    public static <T extends Item> void addItemsToGroup(ResourceKey<CreativeModeTab> group, T... newItems) {
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> {
            for(T item : newItems) {
                entries.accept(item);
            }
        });
    }

    public static <T extends Item> void addItemToGroupBefore(ResourceKey<CreativeModeTab> group, T beforeFirst, T newItem) {
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.addBefore(beforeFirst, newItem));
    }

    @SafeVarargs
    public static <T extends Item> void addItemsToGroupBefore(ResourceKey<CreativeModeTab> group, T beforeFirst, T... newItems) {
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.addBefore(beforeFirst, newItems));
    }

    public static <T extends Item> void addItemToGroupAfter(ResourceKey<CreativeModeTab> group, T afterLast, T newItem) {
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.addAfter(afterLast, newItem));
    }

    @SafeVarargs
    public static <T extends Item> void addItemsToGroupAfter(ResourceKey<CreativeModeTab> group, T afterLast, T... newItems) {
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.addAfter(afterLast, newItems));
    }

    protected static Item.Properties settings() {
        return new Item.Properties();
    }

    protected static Item.Properties settings(int maxCount) {
        return new Item.Properties().stacksTo(maxCount);
    }

    protected static Item.Properties settings(int maxCount, Rarity rarity) {
        return new Item.Properties().stacksTo(maxCount).rarity(rarity);
    }

    protected static BlockItem blockItem(Block block) {
        return new BlockItem(block, settings());
    }

    protected static SpawnEggItem spawnEggItem(EntityType<? extends Mob> entityType, int primaryColor, int secondaryColor) {
        return new SpawnEggItem(entityType, primaryColor, secondaryColor, settings());
    }
}
