package phanastrae.operation_starcleave.item;

import it.unimi.dsi.fastutil.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;
import phanastrae.operation_starcleave.fluid.OperationStarcleaveFluids;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static net.minecraft.world.item.Rarity.EPIC;
import static net.minecraft.world.item.Rarity.RARE;

public class OperationStarcleaveItems {

    public static final List<Pair<ResourceLocation, Item>> UNREGISTERED_ITEMS = new ArrayList<>();

    public static final Item NETHERITE_PUMPKIN = new NetheritePumpkinItem(OperationStarcleaveBlocks.NETHERITE_PUMPKIN, properties().rarity(Rarity.UNCOMMON).fireResistant());

    public static final Item STELLAR_SEDIMENT = blockItem(OperationStarcleaveBlocks.STELLAR_SEDIMENT);
    public static final Item STELLAR_FARMLAND = blockItem(OperationStarcleaveBlocks.STELLAR_FARMLAND);

    public static final Item BISREED_ROOT = new ItemNameBlockItem(OperationStarcleaveBlocks.BISREEDS, properties());

    public static final Item STELLAR_MULCH = blockItem(OperationStarcleaveBlocks.STELLAR_MULCH);
    public static final Item MULCHBORNE_TUFT = blockItem(OperationStarcleaveBlocks.MULCHBORNE_TUFT);

    public static final Item HOLY_MOSS = blockItem(OperationStarcleaveBlocks.HOLY_MOSS);
    public static final Item SHORT_HOLY_MOSS = blockItem(OperationStarcleaveBlocks.SHORT_HOLY_MOSS);

    public static final Item STARDUST_BLOCK = blockItem(OperationStarcleaveBlocks.STARDUST_BLOCK);
    public static final Item STARDUST_CLUSTER = blockItem(OperationStarcleaveBlocks.STARDUST_CLUSTER);

    public static final Item STARDUST_BRICKS = blockItem(OperationStarcleaveBlocks.STARDUST_BRICKS);
    public static final Item STARDUST_BRICK_STAIRS = blockItem(OperationStarcleaveBlocks.STARDUST_BRICK_STAIRS);
    public static final Item STARDUST_BRICK_SLAB = blockItem(OperationStarcleaveBlocks.STARDUST_BRICK_SLAB);
    public static final Item STARDUST_BRICK_WALL = blockItem(OperationStarcleaveBlocks.STARDUST_BRICK_WALL);

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

    public static final Item NUCLEOSYNTHESEED = blockItem(OperationStarcleaveBlocks.NUCLEOSYNTHESEED);
    public static final Item NUCLEIC_FISSUREROOT = blockItem(OperationStarcleaveBlocks.NUCLEIC_FISSUREROOT);
    public static final Item NUCLEIC_FISSURERIND = blockItem(OperationStarcleaveBlocks.NUCLEIC_FISSURERIND);
    public static final Item STRIPED_NUCLEIC_FISSUREROOT = blockItem(OperationStarcleaveBlocks.STRIPED_NUCLEIC_FISSUREROOT);
    public static final Item STRIPED_NUCLEIC_FISSURERIND = blockItem(OperationStarcleaveBlocks.STRIPED_NUCLEIC_FISSURERIND);
    public static final Item NUCLEIC_FISSURELEAVES = blockItem(OperationStarcleaveBlocks.NUCLEIC_FISSURELEAVES);

    public static final Item COAGULATED_PLASMA = blockItem(OperationStarcleaveBlocks.COAGULATED_PLASMA);
    public static final Item PLASMA_ICE = blockItem(OperationStarcleaveBlocks.PLASMA_ICE);


    public static final Item STARCLEAVER_GOLEM_BUCKET = register("starcleaver_golem_bucket", new StarcleaverGolemBucketItem(properties().stacksTo(1)));

    public static final Item STARBLEACH_BOTTLE = register("starbleach_bottle", new StarbleachBottleItem(properties().stacksTo(16).craftRemainder(Items.GLASS_BOTTLE).food(StarbleachBottleItem.STARBLEACH_BOTTLE)));
    public static final Item SPLASH_STARBLEACH_BOTTLE = register("splash_starbleach_bottle", new SplashStarbleachBottleItem(properties().stacksTo(16)));

    public static final Item BISMUTH_FLAKE = register("bismuth_flake", new Item(properties()));
    public static final Item STARFLAKED_BISMUTH = register("starflaked_bismuth", new Item(properties()));

    public static final Item HOLY_STRANDS = register("holy_strands", new Item(properties()));
    public static final Item BLESSED_CLOTH = register("blessed_cloth", new Item(properties()));

    public static final Item BLESSED_BED = register("blessed_bed", new BedItem(OperationStarcleaveBlocks.BLESSED_BED, properties().stacksTo(1).rarity(RARE)));

    public static final Item STARBLEACHED_PEARL = register("starbleached_pearl", new StarbleachedPearlItem(properties().stacksTo(16)));
    public static final Item STARFRUIT = register("starfruit", new StarfruitItem(properties().food(StarfruitItem.STARFRUIT)));

    public static final Item FIRMAMENT_REJUVENATOR = register("firmament_rejuvenator", new FirmamentRejuvenatorItem(properties().stacksTo(8).rarity(RARE)));

    public static final Item BISMUTH_PEGASUS_ARMOR = register("bismuth_pegasus_armor", new AnimalArmorItem(
            OperationStarcleaveArmorMaterials.BISMUTH_ENTRY,
            AnimalArmorItem.BodyType.EQUESTRIAN,
            false,
            properties().stacksTo(1)
    ));

    public static final Item HOLLOWED_SAC = register("hollowed_sac", new HollowedSacItem(properties()));
    public static final Item PHLOGISTON_SAC = register("phlogiston_sac", new PhlogistonSacItem(properties()));

    public static final Item PETRICHORIC_PLASMA_BUCKET = register("petrichoric_plasma_bucket", new PetrichoricPlasmaBucketItem(OperationStarcleaveFluids.PETRICHORIC_PLASMA, properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final Item NUCLEAR_STORMCLOUD_BOTTLE = register("nuclear_stormcloud_bottle", new NuclearStormcloudBottleItem(properties().rarity(EPIC)));
    public static final Item FIRMAMENT_MANIPULATOR = register("firmament_manipulator", new FirmamentManipulatorItem(properties().stacksTo(1).rarity(EPIC)));

    public static final Item STARCLEAVER_GOLEM_SPAWN_EGG = register("starcleaver_golem_spawn_egg", spawnEggItem(OperationStarcleaveEntityTypes.STARCLEAVER_GOLEM, 0x292725, 0x61EDDF));
    public static final Item SUBCAELIC_TORPEDO_SPAWN_EGG = register("subcaelic_torpedo_spawn_egg", spawnEggItem(OperationStarcleaveEntityTypes.SUBCAELIC_TORPEDO, 0xDFDFDF, 0x1FAF7F));
    public static final Item SUBCAELIC_DUX_SPAWN_EGG = register("subcaelic_dux_spawn_egg", spawnEggItem(OperationStarcleaveEntityTypes.SUBCAELIC_DUX, 0xDFEF9F, 0x6FFFDF));

    private static <T extends Item> T register(String id, T item) {
        return register(OperationStarcleave.id(id), item);
    }

    private static <T extends Item> T register(ResourceLocation location, T item) {
        UNREGISTERED_ITEMS.add(Pair.of(location, item));
        return item;
    }

    public static void addToTab(Item item) {
        OperationStarcleaveCreativeModeTabs.addItemToOperationStarcleaveTab(item);
    }

    public static void init(BiConsumer<ResourceLocation, Item> r) {
        UNREGISTERED_ITEMS.forEach(pair -> r.accept(pair.left(), pair.right()));
        UNREGISTERED_ITEMS.clear();

        BiConsumer<String, Item> rwt = (s, i) -> { // register and add to creative mode tab
            r.accept(id(s), i);
            addToTab(i);
        };

        rwt.accept("netherite_pumpkin", NETHERITE_PUMPKIN);
        addToTab(STARCLEAVER_GOLEM_BUCKET);

        addToTab(STARBLEACH_BOTTLE);
        addToTab(SPLASH_STARBLEACH_BOTTLE);

        rwt.accept("stellar_sediment", STELLAR_SEDIMENT);
        rwt.accept("stellar_farmland", STELLAR_FARMLAND);

        rwt.accept("bisreed_root", BISREED_ROOT);
        addToTab(BISMUTH_FLAKE);
        addToTab(STARFLAKED_BISMUTH);

        rwt.accept("stellar_tiles", STELLAR_TILES);
        rwt.accept("stellar_tile_slab", STELLAR_TILE_SLAB);

        rwt.accept("stellar_mulch", STELLAR_MULCH);
        rwt.accept("mulchborne_tuft", MULCHBORNE_TUFT);

        rwt.accept("holy_moss", HOLY_MOSS);
        rwt.accept("short_holy_moss", SHORT_HOLY_MOSS);

        addToTab(HOLY_STRANDS);
        addToTab(BLESSED_CLOTH);

        rwt.accept("blessed_cloth_block", BLESSED_CLOTH_BLOCK);
        rwt.accept("blessed_cloth_carpet", BLESSED_CLOTH_CARPET);
        rwt.accept("blessed_cloth_curtain", BLESSED_CLOTH_CURTAIN);

        addToTab(BLESSED_BED);

        rwt.accept("stardust_block", STARDUST_BLOCK);
        rwt.accept("stardust_cluster", STARDUST_CLUSTER);

        rwt.accept("stardust_bricks", STARDUST_BRICKS);
        rwt.accept("stardust_brick_stairs", STARDUST_BRICK_STAIRS);
        rwt.accept("stardust_brick_slab", STARDUST_BRICK_SLAB);
        rwt.accept("stardust_brick_wall", STARDUST_BRICK_WALL);

        rwt.accept("starbleached_log", STARBLEACHED_LOG);
        rwt.accept("starbleached_wood", STARBLEACHED_WOOD);

        rwt.accept("starbleached_leaves", STARBLEACHED_LEAVES);

        rwt.accept("starbleached_tiles", STARBLEACHED_TILES);
        rwt.accept("starbleached_tile_slab", STARBLEACHED_TILE_SLAB);
        rwt.accept("starbleached_tile_stairs", STARBLEACHED_TILE_STAIRS);
        rwt.accept("starbleached_tile_wall", STARBLEACHED_TILE_WALL);

        rwt.accept("chiseled_starbleached_tiles", CHISELED_STARBLEACHED_TILES);
        rwt.accept("imbued_starbleached_tiles", IMBUED_STARBLEACHED_TILES);

        rwt.accept("nucleosyntheseed", NUCLEOSYNTHESEED);
        rwt.accept("nucleic_fissureroot", NUCLEIC_FISSUREROOT);
        rwt.accept("nucleic_fissurerind", NUCLEIC_FISSURERIND);
        rwt.accept("striped_nucleic_fissureroot", STRIPED_NUCLEIC_FISSUREROOT);
        rwt.accept("striped_nucleic_fissurerind", STRIPED_NUCLEIC_FISSURERIND);
        rwt.accept("nucleic_fissureleaves", NUCLEIC_FISSURELEAVES);

        addToTab(STARBLEACHED_PEARL);
        addToTab(STARFRUIT);

        addToTab(FIRMAMENT_REJUVENATOR);

        addToTab(BISMUTH_PEGASUS_ARMOR);

        rwt.accept("starbleached_pearl_block", STARBLEACHED_PEARL_BLOCK);
        rwt.accept("stellar_repulsor", STELLAR_REPULSOR);

        addToTab(HOLLOWED_SAC);
        addToTab(PHLOGISTON_SAC);

        rwt.accept("coagulated_plasma", COAGULATED_PLASMA);
        rwt.accept("plasma_ice", PLASMA_ICE);
        addToTab(PETRICHORIC_PLASMA_BUCKET);


        addToTab(NUCLEAR_STORMCLOUD_BOTTLE);
        addToTab(FIRMAMENT_MANIPULATOR);

        addToTab(STARCLEAVER_GOLEM_SPAWN_EGG);
        addToTab(SUBCAELIC_TORPEDO_SPAWN_EGG);
        addToTab(SUBCAELIC_DUX_SPAWN_EGG);

        Item.BY_BLOCK.put(OperationStarcleaveBlocks.STARBLEACH_CAULDRON, Items.CAULDRON);
    }

    private static ResourceLocation id(String path) {
        return OperationStarcleave.id(path);
    }

    protected static Item.Properties properties() {
        return new Item.Properties();
    }

    protected static BlockItem blockItem(Block block) {
        return new BlockItem(block, properties());
    }

    protected static SpawnEggItem spawnEggItem(EntityType<? extends Mob> entityType, int primaryColor, int secondaryColor) {
        return new SpawnEggItem(entityType, primaryColor, secondaryColor, properties());
    }
}
