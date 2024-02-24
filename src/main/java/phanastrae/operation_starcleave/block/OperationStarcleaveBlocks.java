package phanastrae.operation_starcleave.block;

import net.minecraft.block.*;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.Instrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ColorCode;
import net.minecraft.util.DyeColor;
import phanastrae.operation_starcleave.OperationStarcleave;

import static net.minecraft.block.Blocks.CAULDRON;

public class OperationStarcleaveBlocks {

    public static final Block NETHERITE_PUMPKIN = new NetheritePumpkinBlock(
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.BLACK)
                    .requiresTool()
                    .strength(10.0F, 1200.0F)
                    .sounds(BlockSoundGroup.NETHERITE)
                    .allowsSpawning(Blocks::always)
                    .pistonBehavior(PistonBehavior.DESTROY)
    );

    public static final Block STELLAR_SEDIMENT = new Block(AbstractBlock.Settings.create().mapColor(MapColor.PURPLE).instrument(Instrument.BASEDRUM).strength(2f, 2f).sounds(BlockSoundGroup.SAND).luminance(b -> 2).allowsSpawning(Blocks::never));
    public static final Block HOLY_MOSS = new HolyMossBlock(AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).instrument(Instrument.BASEDRUM).strength(3f, 2f).sounds(BlockSoundGroup.MOSS_BLOCK).luminance(b -> 13).allowsSpawning(Blocks::never).ticksRandomly());
    public static final Block SHORT_HOLY_MOSS = new ShortHolyMossBlock(AbstractBlock.Settings.create().replaceable().noCollision().breakInstantly().mapColor(MapColor.PALE_YELLOW).sounds(BlockSoundGroup.MOSS_BLOCK).luminance(b -> 13).offset(AbstractBlock.OffsetType.XYZ).pistonBehavior(PistonBehavior.DESTROY));
    public static final Block STARDUST_BLOCK = new ColoredFallingBlock(new ColorCode(0xEF9FCFFF), AbstractBlock.Settings.create().mapColor(MapColor.PALE_PURPLE).instrument(Instrument.BASEDRUM).strength(0.4f, 0.1f).sounds(BlockSoundGroup.SAND).luminance(b -> 9).allowsSpawning(Blocks::never));
    public static final Block STARDUST_CLUSTER = new StardustClusterBlock(AbstractBlock.Settings.create().replaceable().breakInstantly().dropsNothing().nonOpaque().luminance(b -> 15));
    public static final Block STARBLEACHED_LOG = new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(4f, 6f).sounds(BlockSoundGroup.STONE).luminance(b -> 8).allowsSpawning(Blocks::never));
    public static final Block STARBLEACHED_WOOD = new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(4f, 6f).sounds(BlockSoundGroup.STONE).luminance(b -> 8).allowsSpawning(Blocks::never));
    public static final Block STARBLEACHED_LEAVES = new StarbleachedLeavesBlock(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(0.4f, 2f).sounds(BlockSoundGroup.STONE).luminance(b -> 11).allowsSpawning(Blocks::never).nonOpaque().suffocates(Blocks::never).blockVision(Blocks::never).solidBlock(Blocks::never));
    public static final Block STARBLEACHED_TILES = new Block(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(4f, 6f).sounds(BlockSoundGroup.STONE).luminance(b -> 8).allowsSpawning(Blocks::never));
    public static final Block STARBLEACHED_TILE_STAIRS = new StairsBlock(STARBLEACHED_TILES.getDefaultState(), AbstractBlock.Settings.copyShallow(STARBLEACHED_TILES));
    public static final Block STARBLEACHED_TILE_SLAB = new SlabBlock(AbstractBlock.Settings.copyShallow(STARBLEACHED_TILES));
    public static final Block STARBLEACHED_TILE_WALL = new WallBlock(AbstractBlock.Settings.copyShallow(STARBLEACHED_TILES).solid());
    public static final Block CHISELED_STARBLEACHED_TILES = new Block(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(4f, 6f).sounds(BlockSoundGroup.STONE).luminance(b -> 8).allowsSpawning(Blocks::never));
    public static final Block IMBUED_STARBLEACHED_TILES = new ImbuedStarbleachedTilesBlock(AbstractBlock.Settings.create().mapColor(MapColor.CYAN).instrument(Instrument.BASEDRUM).requiresTool().strength(3f, 6f).sounds(BlockSoundGroup.STONE).luminance(b -> 15).allowsSpawning(Blocks::never));
    public static final Block STARBLEACHED_PEARL_BLOCK = new StarbleachedPearlBlock(AbstractBlock.Settings.create().mapColor(MapColor.CYAN).instrument(Instrument.BASEDRUM).requiresTool().strength(2f, 6f).sounds(BlockSoundGroup.GLASS).luminance(b -> 12).allowsSpawning(Blocks::never));
    public static final Block STARBLEACH_CAULDRON = new StarbleachCauldronBlock(AbstractBlock.Settings.copyShallow(CAULDRON).luminance(state -> 13));
    public static final Block STELLAR_TILES = new Block(AbstractBlock.Settings.create().mapColor(MapColor.PURPLE).instrument(Instrument.BASEDRUM).strength(3f, 6f).sounds(BlockSoundGroup.DEEPSLATE).luminance(b -> 2).allowsSpawning(Blocks::never));
    public static final Block STELLAR_TILE_SLAB = new SlabBlock(AbstractBlock.Settings.copyShallow(STELLAR_TILES));
    public static final Block STELLAR_REPULSOR = new StellarRepulsorBlock(AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).instrument(Instrument.BASEDRUM).strength(3f, 6f).sounds(BlockSoundGroup.WOOL).luminance(b -> 13).allowsSpawning(Blocks::never));
    public static final Block BLESSED_CLOTH_BLOCK = new BlessedClothBlock(AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).instrument(Instrument.GUITAR).strength(0.8F).sounds(BlockSoundGroup.WOOL));
    public static final Block BLESSED_CLOTH_CARPET = new BlessedClothCarpetBlock(AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).strength(0.1F).sounds(BlockSoundGroup.WOOL));
    public static final Block BLESSED_CLOTH_CURTAIN = new PaneBlock(AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).instrument(Instrument.GUITAR).strength(0.8F).sounds(BlockSoundGroup.WOOL).nonOpaque());
    public static final Block BLESSED_BED = new BlessedBedBlock(AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).sounds(BlockSoundGroup.WOOD).strength(0.2F).nonOpaque().pistonBehavior(PistonBehavior.DESTROY));

    public static void init() {
        register(NETHERITE_PUMPKIN, "netherite_pumpkin");
        register(STELLAR_SEDIMENT, "stellar_sediment");
        register(HOLY_MOSS, "holy_moss");
        register(SHORT_HOLY_MOSS, "short_holy_moss");
        register(STARDUST_BLOCK, "stardust_block");
        register(STARDUST_CLUSTER, "stardust_cluster");
        register(STARBLEACHED_LOG, "starbleached_log");
        register(STARBLEACHED_WOOD, "starbleached_wood");
        register(STARBLEACHED_LEAVES, "starbleached_leaves");
        register(STARBLEACHED_TILES, "starbleached_tiles");
        register(STARBLEACHED_TILE_STAIRS, "starbleached_tile_stairs");
        register(STARBLEACHED_TILE_SLAB, "starbleached_tile_slab");
        register(STARBLEACHED_TILE_WALL, "starbleached_tile_wall");
        register(CHISELED_STARBLEACHED_TILES, "chiseled_starbleached_tiles");
        register(IMBUED_STARBLEACHED_TILES, "imbued_starbleached_tiles");
        register(STARBLEACHED_PEARL_BLOCK, "starbleached_pearl_block");
        register(STARBLEACH_CAULDRON, "starbleach_cauldron");
        register(STELLAR_TILES, "stellar_tiles");
        register(STELLAR_TILE_SLAB, "stellar_tile_slab");
        register(STELLAR_REPULSOR, "stellar_repulsor");
        register(BLESSED_CLOTH_BLOCK, "blessed_cloth_block");
        register(BLESSED_CLOTH_CARPET, "blessed_cloth_carpet");
        register(BLESSED_CLOTH_CURTAIN, "blessed_cloth_curtain");
        register(BLESSED_BED, "blessed_bed");
    }

    public static <T extends Block> void register(T item, String name) {
        Registry.register(Registries.BLOCK, OperationStarcleave.id(name), item);
    }
}
