package phanastrae.operation_starcleave.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;
import phanastrae.operation_starcleave.fluid.OperationStarcleaveFluids;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static net.minecraft.world.item.Rarity.EPIC;
import static net.minecraft.world.item.Rarity.RARE;

public class OperationStarcleaveItems {
    private static final Map<ResourceLocation, Item> UNREGISTERED_ITEMS = new HashMap<>();

    public static final Item NETHERITE_PUMPKIN = registerBlock(new NetheritePumpkinItem(OperationStarcleaveBlocks.NETHERITE_PUMPKIN, properties().rarity(Rarity.UNCOMMON).fireResistant()));

    public static final Item STELLAR_SEDIMENT = registerBlockItem(OperationStarcleaveBlocks.STELLAR_SEDIMENT);
    public static final Item STELLAR_PATH = registerBlockItem(OperationStarcleaveBlocks.STELLAR_PATH);
    public static final Item STELLAR_FARMLAND = registerBlockItem(OperationStarcleaveBlocks.STELLAR_FARMLAND);

    public static final Item BISREED_ROOT = register("bisreed_root", new ItemNameBlockItem(OperationStarcleaveBlocks.BISREEDS, properties()));

    public static final Item STELLAR_MULCH = registerBlockItem(OperationStarcleaveBlocks.STELLAR_MULCH);
    public static final Item MULCHBORNE_TUFT = registerBlockItem(OperationStarcleaveBlocks.MULCHBORNE_TUFT);

    public static final Item HOLY_MOSS = registerBlockItem(OperationStarcleaveBlocks.HOLY_MOSS);
    public static final Item SHORT_HOLY_MOSS = registerBlockItem(OperationStarcleaveBlocks.SHORT_HOLY_MOSS);

    public static final Item STARDUST_BLOCK = registerBlockItem(OperationStarcleaveBlocks.STARDUST_BLOCK);
    public static final Item STARDUST_CLUSTER = registerBlockItem(OperationStarcleaveBlocks.STARDUST_CLUSTER);

    public static final Item STARDUST_BRICKS = registerBlockItem(OperationStarcleaveBlocks.STARDUST_BRICKS);
    public static final Item STARDUST_BRICK_STAIRS = registerBlockItem(OperationStarcleaveBlocks.STARDUST_BRICK_STAIRS);
    public static final Item STARDUST_BRICK_SLAB = registerBlockItem(OperationStarcleaveBlocks.STARDUST_BRICK_SLAB);
    public static final Item STARDUST_BRICK_WALL = registerBlockItem(OperationStarcleaveBlocks.STARDUST_BRICK_WALL);

    public static final Item STARBLEACHED_LOG = registerBlockItem(OperationStarcleaveBlocks.STARBLEACHED_LOG);
    public static final Item STARBLEACHED_WOOD = registerBlockItem(OperationStarcleaveBlocks.STARBLEACHED_WOOD);

    public static final Item STARBLEACHED_LEAVES = registerBlockItem(OperationStarcleaveBlocks.STARBLEACHED_LEAVES);

    public static final Item STARBLEACHED_TILES = registerBlockItem(OperationStarcleaveBlocks.STARBLEACHED_TILES);
    public static final Item STARBLEACHED_TILE_STAIRS = registerBlockItem(OperationStarcleaveBlocks.STARBLEACHED_TILE_STAIRS);
    public static final Item STARBLEACHED_TILE_SLAB = registerBlockItem(OperationStarcleaveBlocks.STARBLEACHED_TILE_SLAB);
    public static final Item STARBLEACHED_TILE_WALL = registerBlockItem(OperationStarcleaveBlocks.STARBLEACHED_TILE_WALL);

    public static final Item CHISELED_STARBLEACHED_TILES = registerBlockItem(OperationStarcleaveBlocks.CHISELED_STARBLEACHED_TILES);

    public static final Item IMBUED_STARBLEACHED_TILES = registerBlockItem(OperationStarcleaveBlocks.IMBUED_STARBLEACHED_TILES);

    public static final Item STARBLEACHED_PEARL_BLOCK = registerBlockItem(OperationStarcleaveBlocks.STARBLEACHED_PEARL_BLOCK);

    public static final Item STELLAR_TILES = registerBlockItem(OperationStarcleaveBlocks.STELLAR_TILES);
    public static final Item STELLAR_TILE_SLAB = registerBlockItem(OperationStarcleaveBlocks.STELLAR_TILE_SLAB);

    public static final Item STELLAR_REPULSOR = registerBlockItem((OperationStarcleaveBlocks.STELLAR_REPULSOR));

    public static final Item BLESSED_CLOTH_BLOCK = registerBlockItem(OperationStarcleaveBlocks.BLESSED_CLOTH_BLOCK);
    public static final Item BLESSED_CLOTH_CARPET = registerBlockItem(OperationStarcleaveBlocks.BLESSED_CLOTH_CARPET);
    public static final Item BLESSED_CLOTH_CURTAIN = registerBlockItem(OperationStarcleaveBlocks.BLESSED_CLOTH_CURTAIN);

    public static final Item BLESSED_BED = registerBlock(new BedItem(OperationStarcleaveBlocks.BLESSED_BED, properties().stacksTo(1).rarity(RARE)));

    public static final Item SUBCAELIC_PHLOGLIGHT = registerBlockItem(OperationStarcleaveBlocks.SUBCAELIC_PHLOGLIGHT);

    public static final Item NUCLEOSYNTHESEED = registerBlockItem(OperationStarcleaveBlocks.NUCLEOSYNTHESEED);
    public static final Item NUCLEIC_FISSUREROOT = registerBlockItem(OperationStarcleaveBlocks.NUCLEIC_FISSUREROOT);
    public static final Item NUCLEIC_FISSURERIND = registerBlockItem(OperationStarcleaveBlocks.NUCLEIC_FISSURERIND);
    public static final Item STRIPED_NUCLEIC_FISSUREROOT = registerBlockItem(OperationStarcleaveBlocks.STRIPED_NUCLEIC_FISSUREROOT);
    public static final Item STRIPED_NUCLEIC_FISSURERIND = registerBlockItem(OperationStarcleaveBlocks.STRIPED_NUCLEIC_FISSURERIND);
    public static final Item NUCLEIC_FISSURELEAVES = registerBlockItem(OperationStarcleaveBlocks.NUCLEIC_FISSURELEAVES);

    public static final Item COAGULATED_PLASMA = registerBlockItem((OperationStarcleaveBlocks.COAGULATED_PLASMA));
    public static final Item PLASMA_ICE = registerBlockItem((OperationStarcleaveBlocks.PLASMA_ICE));

    public static final Item STARFLAKED_BISMUTH_BLOCK = registerBlockItem(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_BLOCK);
    public static final Item STARFLAKED_BISMUTH_SLAB = registerBlockItem(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_SLAB);
    public static final Item CHISELED_STARFLAKED_BISMUTH_BLOCK = registerBlockItem(OperationStarcleaveBlocks.CHISELED_STARFLAKED_BISMUTH_BLOCK);

    public static final Item STARFLAKED_BISMUTH_PILLAR = registerBlockItem(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_PILLAR);

    public static final Item STARFLAKED_BISMUTH_BRICKS = registerBlockItem(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_BRICKS);
    public static final Item STARFLAKED_BISMUTH_BRICK_STAIRS = registerBlockItem(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_BRICK_STAIRS);
    public static final Item STARFLAKED_BISMUTH_BRICK_SLAB = registerBlockItem(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_BRICK_SLAB);
    public static final Item STARFLAKED_BISMUTH_BRICK_WALL = registerBlockItem(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_BRICK_WALL);
    public static final Item CHISELED_STARFLAKED_BISMUTH_BRICKS = registerBlockItem(OperationStarcleaveBlocks.CHISELED_STARFLAKED_BISMUTH_BRICKS);

    public static final Item STARFLAKED_BISMUTH_TILES = registerBlockItem(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_TILES);
    public static final Item STARFLAKED_BISMUTH_TILE_STAIRS = registerBlockItem(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_TILE_STAIRS);
    public static final Item STARFLAKED_BISMUTH_TILE_SLAB = registerBlockItem(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_TILE_SLAB);
    public static final Item STARFLAKED_BISMUTH_TILE_WALL = registerBlockItem(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_TILE_WALL);

    public static final Item STARFLAKED_BISMUTH_MOSAIC = registerBlockItem(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_MOSAIC);
    public static final Item STARFLAKED_BISMUTH_MOSAIC_STAIRS = registerBlockItem(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_MOSAIC_STAIRS);
    public static final Item STARFLAKED_BISMUTH_MOSAIC_SLAB = registerBlockItem(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_MOSAIC_SLAB);
    public static final Item STARFLAKED_BISMUTH_MOSAIC_WALL = registerBlockItem(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_MOSAIC_WALL);

    public static final Item STARFLAKED_BISMUTH_DOOR = registerBlock(new DoubleHighBlockItem(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_DOOR, properties()));
    public static final Item STARFLAKED_BISMUTH_TRAPDOOR = registerBlockItem(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_TRAPDOOR);

    public static final Item CELESTIAL_OPAL_BLOCK = registerBlockItem(OperationStarcleaveBlocks.CELESTIAL_OPAL_BLOCK);
    public static final Item CELESTIAL_OPAL_STAIRS = registerBlockItem(OperationStarcleaveBlocks.CELESTIAL_OPAL_STAIRS);
    public static final Item CELESTIAL_OPAL_SLAB = registerBlockItem(OperationStarcleaveBlocks.CELESTIAL_OPAL_SLAB);
    public static final Item CELESTIAL_OPAL_WALL = registerBlockItem(OperationStarcleaveBlocks.CELESTIAL_OPAL_WALL);

    public static final Item BUDDING_CELESTIAL_OPAL = registerBlockItem(OperationStarcleaveBlocks.BUDDING_CELESTIAL_OPAL);

    public static final Item CELESTIAL_OPAL_CLUSTER = registerBlockItem(OperationStarcleaveBlocks.CELESTIAL_OPAL_CLUSTER);
    public static final Item LARGE_CELESTIAL_OPAL_BUD = registerBlockItem(OperationStarcleaveBlocks.LARGE_CELESTIAL_OPAL_BUD);
    public static final Item MEDIUM_CELESTIAL_OPAL_BUD = registerBlockItem(OperationStarcleaveBlocks.MEDIUM_CELESTIAL_OPAL_BUD);
    public static final Item SMALL_CELESTIAL_OPAL_BUD = registerBlockItem(OperationStarcleaveBlocks.SMALL_CELESTIAL_OPAL_BUD);

    public static final Item POLISHED_CELESTIAL_OPAL_BLOCK = registerBlockItem(OperationStarcleaveBlocks.POLISHED_CELESTIAL_OPAL_BLOCK);
    public static final Item POLISHED_CELESTIAL_OPAL_STAIRS = registerBlockItem(OperationStarcleaveBlocks.POLISHED_CELESTIAL_OPAL_STAIRS);
    public static final Item POLISHED_CELESTIAL_OPAL_SLAB = registerBlockItem(OperationStarcleaveBlocks.POLISHED_CELESTIAL_OPAL_SLAB);

    public static final Item POLISHED_CELESTIAL_OPAL_BRICKS = registerBlockItem(OperationStarcleaveBlocks.POLISHED_CELESTIAL_OPAL_BRICKS);
    public static final Item POLISHED_CELESTIAL_OPAL_BRICK_STAIRS = registerBlockItem(OperationStarcleaveBlocks.POLISHED_CELESTIAL_OPAL_BRICK_STAIRS);
    public static final Item POLISHED_CELESTIAL_OPAL_BRICK_SLAB = registerBlockItem(OperationStarcleaveBlocks.POLISHED_CELESTIAL_OPAL_BRICK_SLAB);
    public static final Item POLISHED_CELESTIAL_OPAL_BRICK_WALL = registerBlockItem(OperationStarcleaveBlocks.POLISHED_CELESTIAL_OPAL_BRICK_WALL);

    public static final Item POLISHED_CELESTIAL_OPAL_PILLAR = registerBlockItem(OperationStarcleaveBlocks.POLISHED_CELESTIAL_OPAL_PILLAR);

    public static final Item CLEANSED_SINGUT_COIL = registerBlockItem(OperationStarcleaveBlocks.CLEANSED_SINGUT_COIL);
    public static final Item CLEANSED_SINGUT_BLOCK = registerBlockItem(OperationStarcleaveBlocks.CLEANSED_SINGUT_BLOCK);
    public static final Item MUCKY_SINGUT_COIL = registerBlockItem(OperationStarcleaveBlocks.MUCKY_SINGUT_COIL);
    public static final Item MUCKY_SINGUT_BLOCK = registerBlockItem(OperationStarcleaveBlocks.MUCKY_SINGUT_BLOCK);


    public static final Item STARCLEAVER_GOLEM_BUCKET = register("starcleaver_golem_bucket", new StarcleaverGolemBucketItem(properties().stacksTo(1)));

    public static final Item STARBLEACH_BOTTLE = register("starbleach_bottle", new StarbleachBottleItem(properties().stacksTo(16).craftRemainder(Items.GLASS_BOTTLE).food(OperationStarcleaveFoods.STARBLEACH_BOTTLE)));
    public static final Item SPLASH_STARBLEACH_BOTTLE = register("splash_starbleach_bottle", new SplashStarbleachBottleItem(properties().stacksTo(16)));

    public static final Item BISMUTH_FLAKE = register("bismuth_flake", new Item(properties()));
    public static final Item STARFLAKED_BISMUTH = register("starflaked_bismuth", new Item(properties()));

    public static final Item HOLY_STRANDS = register("holy_strands", new Item(properties()));
    public static final Item BLESSED_CLOTH = register("blessed_cloth", new Item(properties()));

    public static final Item STARBLEACHED_PEARL = register("starbleached_pearl", new StarbleachedPearlItem(properties().stacksTo(16)));
    public static final Item STARFRUIT = register("starfruit", new StarfruitItem(properties().food(OperationStarcleaveFoods.STARFRUIT)));

    public static final Item FIRMAMENT_REJUVENATOR = register("firmament_rejuvenator", new FirmamentRejuvenatorItem(properties().stacksTo(8).rarity(RARE)));

    public static final Item BISMUTH_PEGASUS_ARMOR = register("bismuth_pegasus_armor", new AnimalArmorItem(
            OperationStarcleaveArmorMaterials.BISMUTH_ENTRY,
            AnimalArmorItem.BodyType.EQUESTRIAN,
            false,
            properties().stacksTo(1)
    ));

    public static final Item HOLLOWED_SAC = register("hollowed_sac", new HollowedSacItem(properties()));
    public static final Item PHLOGISTON_SAC = register("phlogiston_sac", new PhlogistonSacItem(properties()));

    public static final Item CELESTIAL_OPAL_SHARD = register("celestial_opal_shard", new Item(properties()));

    public static final Item MUCKY_SINGUTS = register("mucky_singuts", new Item(properties().food(OperationStarcleaveFoods.MUCKY_SINGUTS)));
    public static final Item CLEANSED_SINGUTS = register("cleansed_singuts", new Item(properties().food(OperationStarcleaveFoods.CLEANSED_SINGUTS)));

    public static final Item OURANIC_CHIP = register("ouranic_chip", new Item(properties()));

    public static final Item STARBLEACH_BUCKET = register("starbleach_bucket", new StarbleachBucketItem(OperationStarcleaveFluids.STARBLEACH, properties().craftRemainder(Items.BUCKET).stacksTo(1).food(OperationStarcleaveFoods.STARBLEACH_BUCKET)));
    public static final Item PETRICHORIC_PLASMA_BUCKET = register("petrichoric_plasma_bucket", new PetrichoricPlasmaBucketItem(OperationStarcleaveFluids.PETRICHORIC_PLASMA, properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final Item LIMESLAGGED_BUCKET = register("limeslagged_bucket", new Item(properties().stacksTo(16)));

    public static final Item NUCLEAR_STORMCLOUD_BOTTLE = register("nuclear_stormcloud_bottle", new NuclearStormcloudBottleItem(properties().rarity(EPIC)));
    public static final Item FIRMAMENT_MANIPULATOR = register("firmament_manipulator", new FirmamentManipulatorItem(properties().stacksTo(1).rarity(EPIC)));

    public static final Item STARCLEAVER_GOLEM_SPAWN_EGG = register("starcleaver_golem_spawn_egg", spawnEggItem(OperationStarcleaveEntityTypes.STARCLEAVER_GOLEM, 0x292725, 0x61EDDF));
    public static final Item SUBCAELIC_TORPEDO_SPAWN_EGG = register("subcaelic_torpedo_spawn_egg", spawnEggItem(OperationStarcleaveEntityTypes.SUBCAELIC_TORPEDO, 0xDFDFDF, 0x1FAF7F));
    public static final Item SUBCAELIC_DUX_SPAWN_EGG = register("subcaelic_dux_spawn_egg", spawnEggItem(OperationStarcleaveEntityTypes.SUBCAELIC_DUX, 0xDFEF9F, 0x6FFFDF));
    public static final Item SINEATER_SPAWN_EGG = register("sineater_spawn_egg", spawnEggItem(OperationStarcleaveEntityTypes.SINEATER, 0x852869, 0xFAE861));
    public static final Item TRACTORBLOOM_SPAWN_EGG = register("tractorbloom_spawn_egg", spawnEggItem(OperationStarcleaveEntityTypes.TRACTORBLOOM, 0xDE2377, 0x36F5DF));

    private static Item registerBlockItem(Block block) {
        return registerBlock(new BlockItem(block, properties()));
    }

    private static Item registerBlock(BlockItem item) {
        return registerBlock(item.getBlock(), item);
    }

    private static Item registerBlock(Block block, Item item) {
        ResourceLocation location;
        Map<Block, ResourceLocation> map = OperationStarcleaveBlocks.UNREGISTERED_BLOCKS.inverse();
        if (map.containsKey(block)) {
            location = map.get(block);
        } else {
            location = BuiltInRegistries.BLOCK.getKey(block);
        }
        return register(location, item);
    }

    private static Item register(String id, Item item) {
        return register(OperationStarcleave.id(id), item);
    }

    private static Item register(ResourceLocation location, Item item) {
        UNREGISTERED_ITEMS.put(location, item);
        return item;
    }

    public static void init(BiConsumer<ResourceLocation, Item> r) {
        UNREGISTERED_ITEMS.forEach(r);
        UNREGISTERED_ITEMS.clear();

        Item.BY_BLOCK.put(OperationStarcleaveBlocks.STARBLEACH_CAULDRON, Items.CAULDRON);
    }

    protected static Item.Properties properties() {
        return new Item.Properties();
    }

    protected static SpawnEggItem spawnEggItem(EntityType<? extends Mob> entityType, int primaryColor, int secondaryColor) {
        return new SpawnEggItem(entityType, primaryColor, secondaryColor, properties());
    }
}
