package phanastrae.operation_starcleave.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;

public class OperationStarcleaveItems {

    public static final Item NETHERITE_PUMPKIN = new NetheritePumpkinItem(OperationStarcleaveBlocks.NETHERITE_PUMPKIN, new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof());

    public static final Item STELLAR_SEDIMENT = new BlockItem(OperationStarcleaveBlocks.STELLAR_SEDIMENT, new FabricItemSettings());
    public static final Item STELLAR_FARMLAND = new BlockItem(OperationStarcleaveBlocks.STELLAR_FARMLAND, new FabricItemSettings());
    public static final Item HOLY_MOSS = new BlockItem(OperationStarcleaveBlocks.HOLY_MOSS, new FabricItemSettings());
    public static final Item SHORT_HOLY_MOSS = new BlockItem(OperationStarcleaveBlocks.SHORT_HOLY_MOSS, new FabricItemSettings());
    public static final Item STARDUST_BLOCK = new BlockItem(OperationStarcleaveBlocks.STARDUST_BLOCK, new FabricItemSettings());
    public static final Item STARDUST_CLUSTER = new BlockItem(OperationStarcleaveBlocks.STARDUST_CLUSTER, new FabricItemSettings());
    public static final Item STARBLEACHED_LOG = new BlockItem(OperationStarcleaveBlocks.STARBLEACHED_LOG, new FabricItemSettings());
    public static final Item STARBLEACHED_WOOD = new BlockItem(OperationStarcleaveBlocks.STARBLEACHED_WOOD, new FabricItemSettings());
    public static final Item STARBLEACHED_LEAVES = new BlockItem(OperationStarcleaveBlocks.STARBLEACHED_LEAVES, new FabricItemSettings());
    public static final Item STARBLEACHED_TILES = new BlockItem(OperationStarcleaveBlocks.STARBLEACHED_TILES, new FabricItemSettings());
    public static final Item STARBLEACHED_TILE_STAIRS = new BlockItem(OperationStarcleaveBlocks.STARBLEACHED_TILE_STAIRS, new FabricItemSettings());
    public static final Item STARBLEACHED_TILE_SLAB = new BlockItem(OperationStarcleaveBlocks.STARBLEACHED_TILE_SLAB, new FabricItemSettings());
    public static final Item STARBLEACHED_TILE_WALL = new BlockItem(OperationStarcleaveBlocks.STARBLEACHED_TILE_WALL, new FabricItemSettings());
    public static final Item CHISELED_STARBLEACHED_TILES = new BlockItem(OperationStarcleaveBlocks.CHISELED_STARBLEACHED_TILES, new FabricItemSettings());
    public static final Item IMBUED_STARBLEACHED_TILES = new BlockItem(OperationStarcleaveBlocks.IMBUED_STARBLEACHED_TILES, new FabricItemSettings());
    public static final Item STARBLEACHED_PEARL_BLOCK = new BlockItem(OperationStarcleaveBlocks.STARBLEACHED_PEARL_BLOCK, new FabricItemSettings());
    public static final Item STELLAR_TILES = new BlockItem(OperationStarcleaveBlocks.STELLAR_TILES, new FabricItemSettings());
    public static final Item STELLAR_TILE_SLAB = new BlockItem(OperationStarcleaveBlocks.STELLAR_TILE_SLAB, new FabricItemSettings());
    public static final Item STELLAR_REPULSOR = new BlockItem(OperationStarcleaveBlocks.STELLAR_REPULSOR, new FabricItemSettings());
    public static final Item BLESSED_CLOTH_BLOCK = new BlockItem(OperationStarcleaveBlocks.BLESSED_CLOTH_BLOCK, new FabricItemSettings());
    public static final Item BLESSED_CLOTH_CARPET = new BlockItem(OperationStarcleaveBlocks.BLESSED_CLOTH_CARPET, new FabricItemSettings());
    public static final Item BLESSED_CLOTH_CURTAIN = new BlockItem(OperationStarcleaveBlocks.BLESSED_CLOTH_CURTAIN, new FabricItemSettings());

    public static final Item BLESSED_BED = new BedItem(OperationStarcleaveBlocks.BLESSED_BED, new FabricItemSettings().maxCount(1).rarity(Rarity.RARE));

    public static final Item STARCLEAVER_GOLEM_BUCKET = new StarcleaverGolemBucketItem(new FabricItemSettings().maxCount(1));
    public static final Item STARBLEACH_BOTTLE = new StarbleachBottleItem(new FabricItemSettings().recipeRemainder(Items.GLASS_BOTTLE).food(StarbleachBottleItem.STARBLEACH_BOTTLE).maxCount(16));
    public static final Item SPLASH_STARBLEACH_BOTTLE = new SplashStarbleachBottleItem(new FabricItemSettings().maxCount(16));
    public static final Item STARBLEACHED_PEARL = new StarbleachedPearlItem(new FabricItemSettings().maxCount(16));
    public static final Item STARFRUIT = new StarfruitItem(new FabricItemSettings().food(StarfruitItem.STARFRUIT));
    public static final Item HOLY_STRANDS = new Item(new FabricItemSettings());
    public static final Item BLESSED_CLOTH = new Item(new FabricItemSettings());
    public static final Item HOLLOWED_SAC = new HollowedSacItem(new FabricItemSettings());
    public static final Item PHLOGISTON_SAC = new PhlogistonSacItem(new FabricItemSettings());
    public static final Item FIRMAMENT_REJUVENATOR = new FirmamentRejuvenatorItem(new FabricItemSettings().rarity(Rarity.RARE).maxCount(8));
    public static final Item FIRMAMENT_MANIPULATOR = new FirmamentManipulatorItem(new FabricItemSettings().rarity(Rarity.EPIC).maxCount(1));
    public static final Item STARCLEAVER_GOLEM_SPAWN_EGG = new SpawnEggItem(OperationStarcleaveEntityTypes.STARCLEAVER_GOLEM, 0x292725, 0x61eddf, new Item.Settings());
    public static final Item SUBCAELIC_TORPEDO_SPAWN_EGG = new SpawnEggItem(OperationStarcleaveEntityTypes.SUBCAELIC_TORPEDO, 0xDFDFDF, 0x1FAF7F, new Item.Settings());
    public static final Item SUBCAELIC_DUX_SPAWN_EGG = new SpawnEggItem(OperationStarcleaveEntityTypes.SUBCAELIC_DUX, 0xDFEF9F, 0x6FFFDF, new Item.Settings());

    public static final ItemGroup OPERATION_STARCLEAVE_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(NETHERITE_PUMPKIN))
            .displayName(Text.translatable("itemGroup.operation_starcleave.group"))
            .build();

    public static void init() {
        Registry.register(Registries.ITEM_GROUP, OperationStarcleave.id("operation_starcleave"), OPERATION_STARCLEAVE_GROUP);

        registerWithIG(NETHERITE_PUMPKIN, "netherite_pumpkin");
        registerWithIG(STARCLEAVER_GOLEM_BUCKET, "starcleaver_golem_bucket");
        registerWithIG(STELLAR_SEDIMENT, "stellar_sediment");
        registerWithIG(STELLAR_FARMLAND, "stellar_farmland");
        registerWithIG(HOLY_MOSS, "holy_moss");
        registerWithIG(SHORT_HOLY_MOSS, "short_holy_moss");
        registerWithIG(STARDUST_BLOCK, "stardust_block");
        registerWithIG(STARDUST_CLUSTER, "stardust_cluster");
        registerWithIG(STARBLEACHED_LOG, "starbleached_log");
        registerWithIG(STARBLEACHED_WOOD, "starbleached_wood");
        registerWithIG(STARBLEACHED_LEAVES, "starbleached_leaves");
        registerWithIG(STARBLEACHED_TILES, "starbleached_tiles");
        registerWithIG(STARBLEACHED_TILE_SLAB, "starbleached_tile_slab");
        registerWithIG(STARBLEACHED_TILE_STAIRS, "starbleached_tile_stairs");
        registerWithIG(STARBLEACHED_TILE_WALL, "starbleached_tile_wall");
        registerWithIG(CHISELED_STARBLEACHED_TILES, "chiseled_starbleached_tiles");
        registerWithIG(IMBUED_STARBLEACHED_TILES, "imbued_starbleached_tiles");
        registerWithIG(STARBLEACHED_PEARL_BLOCK, "starbleached_pearl_block");
        registerWithIG(STELLAR_TILES, "stellar_tiles");
        registerWithIG(STELLAR_TILE_SLAB, "stellar_tile_slab");
        registerWithIG(STELLAR_REPULSOR, "stellar_repulsor");
        registerWithIG(BLESSED_CLOTH_BLOCK, "blessed_cloth_block");
        registerWithIG(BLESSED_CLOTH_CARPET, "blessed_cloth_carpet");
        registerWithIG(BLESSED_CLOTH_CURTAIN, "blessed_cloth_curtain");
        registerWithIG(BLESSED_BED, "blessed_bed");

        Item.BLOCK_ITEMS.put(OperationStarcleaveBlocks.STARBLEACH_CAULDRON, Items.CAULDRON);

        registerWithIG(STARBLEACH_BOTTLE, "starbleach_bottle");
        registerWithIG(SPLASH_STARBLEACH_BOTTLE, "splash_starbleach_bottle");
        registerWithIG(STARBLEACHED_PEARL, "starbleached_pearl");
        registerWithIG(STARFRUIT, "starfruit");
        registerWithIG(HOLY_STRANDS, "holy_strands");
        registerWithIG(BLESSED_CLOTH, "blessed_cloth");
        registerWithIG(HOLLOWED_SAC, "hollowed_sac");
        registerWithIG(PHLOGISTON_SAC, "phlogiston_sac");
        registerWithIG(FIRMAMENT_REJUVENATOR, "firmament_rejuvenator");
        registerWithIG(FIRMAMENT_MANIPULATOR, "firmament_manipulator");

        registerWithIG(STARCLEAVER_GOLEM_SPAWN_EGG, "starcleaver_golem_spawn_egg");
        registerWithIG(SUBCAELIC_TORPEDO_SPAWN_EGG, "subcaelic_torpedo_spawn_egg");
        registerWithIG(SUBCAELIC_DUX_SPAWN_EGG, "subcaelic_dux_spawn_egg");

        addToVanillaItemGroups();
    }

    public static void addToVanillaItemGroups() {
        addItemToGroupAfter(STARBLEACHED_LOG, ItemGroups.BUILDING_BLOCKS, Items.WARPED_BUTTON);
        addItemToGroupAfter(STARBLEACHED_WOOD, ItemGroups.BUILDING_BLOCKS, STARBLEACHED_LOG);
        addItemToGroup(STARBLEACHED_TILES, ItemGroups.BUILDING_BLOCKS);
        addItemToGroup(STARBLEACHED_TILE_STAIRS, ItemGroups.BUILDING_BLOCKS);
        addItemToGroup(STARBLEACHED_TILE_SLAB, ItemGroups.BUILDING_BLOCKS);
        addItemToGroup(STARBLEACHED_TILE_WALL, ItemGroups.BUILDING_BLOCKS);
        addItemToGroup(CHISELED_STARBLEACHED_TILES, ItemGroups.BUILDING_BLOCKS);
        addItemToGroup(IMBUED_STARBLEACHED_TILES, ItemGroups.BUILDING_BLOCKS);
        addItemToGroup(STELLAR_TILES, ItemGroups.BUILDING_BLOCKS);
        addItemToGroup(STELLAR_TILE_SLAB, ItemGroups.BUILDING_BLOCKS);
        addItemToGroup(STARDUST_CLUSTER, ItemGroups.BUILDING_BLOCKS);
        addItemToGroup(BLESSED_CLOTH_BLOCK, ItemGroups.BUILDING_BLOCKS);
        addItemToGroup(BLESSED_CLOTH_CARPET, ItemGroups.BUILDING_BLOCKS);
        addItemToGroup(BLESSED_CLOTH_CURTAIN, ItemGroups.BUILDING_BLOCKS);

        addItemToGroupAfter(STARBLEACHED_LOG, ItemGroups.NATURAL, Items.WARPED_STEM);
        addItemToGroupAfter(STARBLEACHED_LEAVES, ItemGroups.NATURAL, Items.FLOWERING_AZALEA_LEAVES);
        addItemToGroupAfter(HOLY_MOSS, ItemGroups.NATURAL, Items.END_STONE);
        addItemToGroupAfter(STELLAR_SEDIMENT, ItemGroups.NATURAL, HOLY_MOSS);
        addItemToGroupAfter(STELLAR_FARMLAND, ItemGroups.NATURAL, STELLAR_SEDIMENT);
        addItemToGroupAfter(STARDUST_BLOCK, ItemGroups.NATURAL, STELLAR_FARMLAND);
        addItemToGroupAfter(SHORT_HOLY_MOSS, ItemGroups.NATURAL, Items.HANGING_ROOTS);

        addItemToGroupAfter(NETHERITE_PUMPKIN, ItemGroups.FUNCTIONAL, Items.DRAGON_HEAD);
        addItemToGroup(STARDUST_CLUSTER, ItemGroups.FUNCTIONAL);
        addItemToGroup(STARBLEACHED_LEAVES, ItemGroups.FUNCTIONAL);
        addItemToGroup(BLESSED_CLOTH_BLOCK, ItemGroups.FUNCTIONAL);
        addItemToGroup(BLESSED_CLOTH_CARPET, ItemGroups.FUNCTIONAL);
        addItemToGroupAfter(BLESSED_BED, ItemGroups.FUNCTIONAL, Items.PINK_BED);

        addItemToGroup(STARBLEACHED_PEARL_BLOCK, ItemGroups.REDSTONE);
        addItemToGroup(STELLAR_REPULSOR, ItemGroups.REDSTONE);

        addItemToGroupBefore(NETHERITE_PUMPKIN, ItemGroups.COMBAT, Items.TURTLE_HELMET);

        addItemToGroup(STARBLEACH_BOTTLE, ItemGroups.FOOD_AND_DRINK);
        addItemToGroup(SPLASH_STARBLEACH_BOTTLE, ItemGroups.FOOD_AND_DRINK);
        addItemToGroupAfter(STARFRUIT, ItemGroups.FOOD_AND_DRINK, Items.CHORUS_FRUIT);

        addItemToGroupAfter(STARBLEACH_BOTTLE, ItemGroups.INGREDIENTS, Items.EXPERIENCE_BOTTLE);
        addItemToGroupAfter(HOLY_STRANDS, ItemGroups.INGREDIENTS, STARBLEACH_BOTTLE);
        addItemToGroupAfter(BLESSED_CLOTH, ItemGroups.INGREDIENTS, HOLY_STRANDS);

        addItemToGroup(STARCLEAVER_GOLEM_SPAWN_EGG, ItemGroups.SPAWN_EGGS);
        addItemToGroup(SUBCAELIC_TORPEDO_SPAWN_EGG, ItemGroups.SPAWN_EGGS);
        addItemToGroup(SUBCAELIC_DUX_SPAWN_EGG, ItemGroups.SPAWN_EGGS);

        addItemToGroup(STARBLEACHED_PEARL, ItemGroups.TOOLS);
        addItemToGroup(FIRMAMENT_REJUVENATOR, ItemGroups.TOOLS);
        addItemToGroup(STARCLEAVER_GOLEM_BUCKET, ItemGroups.TOOLS);
        addItemToGroup(HOLLOWED_SAC, ItemGroups.TOOLS);
        addItemToGroup(PHLOGISTON_SAC, ItemGroups.TOOLS);

        // different method used here to ensure the item is only added if operator tab is present
        ItemGroup operatorGroup = Registries.ITEM_GROUP.get(ItemGroups.OPERATOR);
        if(operatorGroup != null) {
            ItemGroupEvents.MODIFY_ENTRIES_ALL.register((group, entries) -> {
                if (operatorGroup.equals(group)) {
                    entries.add(FIRMAMENT_MANIPULATOR);
                }
            });
        }
    }

    public static <T extends Item> void registerWithIG(T item, String name) {
        register(item, name);
        addItemToGroup(item, RegistryKey.of(Registries.ITEM_GROUP.getKey(), OperationStarcleave.id("operation_starcleave")));
    }

    public static <T extends Item> void register(T item, String name) {
        Registry.register(Registries.ITEM, OperationStarcleave.id(name), item);
    }

    public static <T extends Item> void addItemToGroup(T newItem, RegistryKey<ItemGroup> group) {
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.add(newItem));
    }

    public static <T extends Item> void addItemToGroupBefore(T newItem, RegistryKey<ItemGroup> group, T beforeFirst) {
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.addBefore(beforeFirst, newItem));
    }

    public static <T extends Item> void addItemToGroupAfter(T newItem, RegistryKey<ItemGroup> group, T afterLast) {
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.addAfter(afterLast, newItem));
    }
}
