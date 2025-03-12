package phanastrae.operation_starcleave.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;

import java.util.function.BiConsumer;

import static net.minecraft.world.item.Rarity.EPIC;
import static net.minecraft.world.item.Rarity.RARE;

public class OperationStarcleaveItems {

    public static final Item NETHERITE_PUMPKIN = new NetheritePumpkinItem(OperationStarcleaveBlocks.NETHERITE_PUMPKIN, settings().rarity(Rarity.UNCOMMON).fireResistant());

    public static final Item STELLAR_SEDIMENT = blockItem(OperationStarcleaveBlocks.STELLAR_SEDIMENT);
    public static final Item STELLAR_FARMLAND = blockItem(OperationStarcleaveBlocks.STELLAR_FARMLAND);
    public static final Item BISREED_ROOT = new ItemNameBlockItem(OperationStarcleaveBlocks.BISREEDS, settings());
    public static final Item BISMUTH_FLAKE = new Item(settings());
    public static final Item STARFLAKED_BISMUTH = new Item(settings());

    public static final Item STELLAR_MULCH = blockItem(OperationStarcleaveBlocks.STELLAR_MULCH);
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

    public static final Item NUCLEOSYNTHESEED = blockItem(OperationStarcleaveBlocks.NUCLEOSYNTHESEED);
    public static final Item NUCLEIC_FISSUREROOT = blockItem(OperationStarcleaveBlocks.NUCLEIC_FISSUREROOT);
    public static final Item NUCLEIC_FISSURELEAVES = blockItem(OperationStarcleaveBlocks.NUCLEIC_FISSURELEAVES);
    public static final Item COAGULATED_PLASMA = blockItem(OperationStarcleaveBlocks.COAGULATED_PLASMA);
    public static final Item PLASMA_ICE = blockItem(OperationStarcleaveBlocks.PLASMA_ICE);

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
    public static final Item BISMUTH_PEGASUS_ARMOR = new AnimalArmorItem(
            OperationStarcleaveArmorMaterials.BISMUTH_ENTRY,
            AnimalArmorItem.BodyType.EQUESTRIAN,
            false,
            settings().stacksTo(1)
    );
    public static final Item NUCLEAR_STORMCLOUD_BOTTLE = new NuclearStormcloudBottleItem(settings().rarity(EPIC));

    public static final Item FIRMAMENT_REJUVENATOR = new FirmamentRejuvenatorItem(settings(8, RARE));
    public static final Item FIRMAMENT_MANIPULATOR = new FirmamentManipulatorItem(settings(1, EPIC));
    public static final Item STARCLEAVER_GOLEM_SPAWN_EGG = spawnEggItem(OperationStarcleaveEntityTypes.STARCLEAVER_GOLEM, 0x292725, 0x61EDDF);
    public static final Item SUBCAELIC_TORPEDO_SPAWN_EGG = spawnEggItem(OperationStarcleaveEntityTypes.SUBCAELIC_TORPEDO, 0xDFDFDF, 0x1FAF7F);
    public static final Item SUBCAELIC_DUX_SPAWN_EGG = spawnEggItem(OperationStarcleaveEntityTypes.SUBCAELIC_DUX, 0xDFEF9F, 0x6FFFDF);

    public static void init(BiConsumer<ResourceLocation, Item> r) {
        BiConsumer<String, Item> rwt = (s, i) -> { // register and add to creative mode tab
            r.accept(id(s), i);
            OperationStarcleaveCreativeModeTabs.addItemToOperationStarcleaveTab(i);
        };

        rwt.accept("netherite_pumpkin", NETHERITE_PUMPKIN);
        rwt.accept("starcleaver_golem_bucket", STARCLEAVER_GOLEM_BUCKET);

        rwt.accept("stellar_sediment", STELLAR_SEDIMENT);
        rwt.accept("stellar_farmland", STELLAR_FARMLAND);
        rwt.accept("bisreed_root", BISREED_ROOT);
        rwt.accept("bismuth_flake", BISMUTH_FLAKE);
        rwt.accept("starflaked_bismuth", STARFLAKED_BISMUTH);

        rwt.accept("stellar_mulch", STELLAR_MULCH);
        rwt.accept("mulchborne_tuft", MULCHBORNE_TUFT);

        rwt.accept("holy_moss", HOLY_MOSS);
        rwt.accept("short_holy_moss", SHORT_HOLY_MOSS);

        rwt.accept("stardust_block", STARDUST_BLOCK);
        rwt.accept("stardust_cluster", STARDUST_CLUSTER);

        rwt.accept("starbleached_log", STARBLEACHED_LOG);
        rwt.accept("starbleached_wood", STARBLEACHED_WOOD);
        rwt.accept("starbleached_leaves", STARBLEACHED_LEAVES);
        rwt.accept("starbleached_tiles", STARBLEACHED_TILES);
        rwt.accept("starbleached_tile_slab", STARBLEACHED_TILE_SLAB);
        rwt.accept("starbleached_tile_stairs", STARBLEACHED_TILE_STAIRS);
        rwt.accept("starbleached_tile_wall", STARBLEACHED_TILE_WALL);
        rwt.accept("chiseled_starbleached_tiles", CHISELED_STARBLEACHED_TILES);
        rwt.accept("imbued_starbleached_tiles", IMBUED_STARBLEACHED_TILES);

        rwt.accept("starbleached_pearl_block", STARBLEACHED_PEARL_BLOCK);

        rwt.accept("stellar_tiles", STELLAR_TILES);
        rwt.accept("stellar_tile_slab", STELLAR_TILE_SLAB);

        rwt.accept("stellar_repulsor", STELLAR_REPULSOR);

        rwt.accept("blessed_cloth_block", BLESSED_CLOTH_BLOCK);
        rwt.accept("blessed_cloth_carpet", BLESSED_CLOTH_CARPET);
        rwt.accept("blessed_cloth_curtain", BLESSED_CLOTH_CURTAIN);

        rwt.accept("nucleosyntheseed", NUCLEOSYNTHESEED);
        rwt.accept("nucleic_fissureroot", NUCLEIC_FISSUREROOT);
        rwt.accept("nucleic_fissureleaves", NUCLEIC_FISSURELEAVES);
        rwt.accept("coagulated_plasma", COAGULATED_PLASMA);
        rwt.accept("plasma_ice", PLASMA_ICE);

        rwt.accept("blessed_bed", BLESSED_BED);
        Item.BY_BLOCK.put(OperationStarcleaveBlocks.STARBLEACH_CAULDRON, Items.CAULDRON);

        rwt.accept("starbleach_bottle", STARBLEACH_BOTTLE);
        rwt.accept("splash_starbleach_bottle", SPLASH_STARBLEACH_BOTTLE);

        rwt.accept("starbleached_pearl", STARBLEACHED_PEARL);
        rwt.accept("starfruit", STARFRUIT);

        rwt.accept("holy_strands", HOLY_STRANDS);
        rwt.accept("blessed_cloth", BLESSED_CLOTH);

        rwt.accept("hollowed_sac", HOLLOWED_SAC);
        rwt.accept("phlogiston_sac", PHLOGISTON_SAC);

        rwt.accept("bismuth_pegasus_armor", BISMUTH_PEGASUS_ARMOR);

        rwt.accept("nuclear_stormcloud_bottle",NUCLEAR_STORMCLOUD_BOTTLE);

        rwt.accept("firmament_rejuvenator", FIRMAMENT_REJUVENATOR);

        rwt.accept("firmament_manipulator", FIRMAMENT_MANIPULATOR);

        rwt.accept("starcleaver_golem_spawn_egg", STARCLEAVER_GOLEM_SPAWN_EGG);
        rwt.accept("subcaelic_torpedo_spawn_egg", SUBCAELIC_TORPEDO_SPAWN_EGG);
        rwt.accept("subcaelic_dux_spawn_egg", SUBCAELIC_DUX_SPAWN_EGG);
    }

    private static ResourceLocation id(String path) {
        return OperationStarcleave.id(path);
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
