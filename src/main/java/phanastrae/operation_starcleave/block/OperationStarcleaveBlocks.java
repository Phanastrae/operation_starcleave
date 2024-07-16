package phanastrae.operation_starcleave.block;

import net.minecraft.block.*;
import net.minecraft.block.enums.Instrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ColorCode;
import phanastrae.operation_starcleave.OperationStarcleave;

import java.util.function.ToIntFunction;

import static net.minecraft.block.AbstractBlock.OffsetType.XYZ;
import static net.minecraft.block.Blocks.CAULDRON;
import static net.minecraft.block.Blocks.DEAD_BUSH;
import static net.minecraft.block.MapColor.*;
import static net.minecraft.block.enums.Instrument.BASEDRUM;
import static net.minecraft.block.enums.Instrument.GUITAR;
import static net.minecraft.block.piston.PistonBehavior.DESTROY;
import static net.minecraft.sound.BlockSoundGroup.*;

public class OperationStarcleaveBlocks {
    protected static final AbstractBlock.ContextPredicate ALWAYS = (blockState, blockView, blockPos) -> true;
    protected static final AbstractBlock.ContextPredicate NEVER = (blockState, blockView, blockPos) -> false;
    protected static final AbstractBlock.TypedContextPredicate<EntityType<?>> SPAWN_ALWAYS = (blockState, blockView, blockPos, entityType) -> true;
    protected static final AbstractBlock.TypedContextPredicate<EntityType<?>> SPAWN_NEVER = (blockState, blockView, blockPos, entityType) -> true;


    public static final Block NETHERITE_PUMPKIN = new NetheritePumpkinBlock(settings(BLACK, NETHERITE).requiresTool().strength(10.0F, 1200.0F).allowsSpawning(SPAWN_ALWAYS).pistonBehavior(DESTROY));

    public static final Block STELLAR_SEDIMENT = new StellarSedimentBlock(settings(PURPLE, SAND, BASEDRUM, 1.15f, 2f, 2).allowsSpawning(SPAWN_NEVER));
    public static final Block STELLAR_MULCH = new StellarMulchBlock(settings(PURPLE, MUD, BASEDRUM, 1.15f, 2f, 4).allowsSpawning(SPAWN_NEVER).ticksRandomly());
    public static final Block STELLAR_FARMLAND = new StellarFarmlandBlock(settings(PURPLE, SAND, BASEDRUM, 1.25f, 2f).luminance(b -> b.get(FarmlandBlock.MOISTURE) == 7 ? 15 : 2).allowsSpawning(SPAWN_NEVER).blockVision(ALWAYS).suffocates(ALWAYS).ticksRandomly());
    public static final Block MULCHBORNE_TUFT = new MulchborneTuftBlock(settings(MAGENTA, GRASS).luminance(constant(6)).offset(XYZ).pistonBehavior(DESTROY).replaceable().noCollision().breakInstantly());
    public static final Block HOLY_MOSS = new HolyMossBlock(settings(PALE_YELLOW, MOSS_BLOCK, BASEDRUM, 1.15f, 2f, 13).allowsSpawning(SPAWN_NEVER).ticksRandomly());
    public static final Block SHORT_HOLY_MOSS = new ShortHolyMossBlock(settings(PALE_YELLOW, MOSS_BLOCK).luminance(constant(13)).offset(XYZ).pistonBehavior(DESTROY).replaceable().noCollision().breakInstantly());
    public static final Block STARDUST_BLOCK = new ColoredFallingBlock(new ColorCode(0xEF9FCFFF), settings(PALE_PURPLE, SAND, BASEDRUM, 0.2f, 0.1f, 9).allowsSpawning(SPAWN_NEVER));
    public static final Block STARDUST_CLUSTER = new StardustClusterBlock(settings().luminance(constant(15)).replaceable().breakInstantly().dropsNothing().nonOpaque());
    public static final Block STARBLEACHED_LOG = new PillarBlock(settings(GRAY, STONE, BASEDRUM, 2f, 6f, 8, true).allowsSpawning(SPAWN_NEVER));
    public static final Block STARBLEACHED_WOOD = new PillarBlock(copyShallow(STARBLEACHED_LOG));
    public static final Block STARBLEACHED_LEAVES = new StarbleachedLeavesBlock(settings(GRAY, STONE, BASEDRUM, 0.25f, 2f, 11, true).allowsSpawning(SPAWN_NEVER).suffocates(NEVER).blockVision(NEVER).solidBlock(NEVER).nonOpaque());
    public static final Block STARBLEACHED_TILES = new Block(settings(GRAY, STONE, BASEDRUM, 1.5f, 6f, 8, true).allowsSpawning(SPAWN_NEVER));
    public static final Block STARBLEACHED_TILE_STAIRS = stairsOf(STARBLEACHED_TILES);
    public static final Block STARBLEACHED_TILE_SLAB = slabOf(STARBLEACHED_TILES);
    public static final Block STARBLEACHED_TILE_WALL = wallOf(STARBLEACHED_TILES);
    public static final Block CHISELED_STARBLEACHED_TILES = new Block(settings(GRAY, STONE, BASEDRUM, 1.5f, 6f, 8, true).allowsSpawning(SPAWN_NEVER));
    public static final Block IMBUED_STARBLEACHED_TILES = new ImbuedStarbleachedTilesBlock(settings(CYAN, STONE, BASEDRUM, 1.25f, 6f, 15, true).allowsSpawning(SPAWN_NEVER));
    public static final Block STARBLEACHED_PEARL_BLOCK = new StarbleachedPearlBlock(settings(CYAN, GLASS, BASEDRUM, 1.3f, 6f, 12, true).allowsSpawning(SPAWN_NEVER));
    public static final Block STARBLEACH_CAULDRON = new StarbleachCauldronBlock(copyShallow(CAULDRON).luminance(constant(13)));
    public static final Block STELLAR_TILES = new Block(settings(PURPLE, DEEPSLATE, BASEDRUM, 1.75f, 6f, 2).allowsSpawning(SPAWN_NEVER));
    public static final Block STELLAR_TILE_SLAB = slabOf(STELLAR_TILES);
    public static final Block STELLAR_REPULSOR = new StellarRepulsorBlock(settings(PALE_YELLOW, WOOL, BASEDRUM, 1.75f, 6f, 13).allowsSpawning(SPAWN_NEVER));
    public static final Block BLESSED_CLOTH_BLOCK = new BlessedClothBlock(settings(PALE_YELLOW, WOOL, GUITAR).strength(0.8F));
    public static final Block BLESSED_CLOTH_CARPET = new BlessedClothCarpetBlock(settings(PALE_YELLOW, WOOL).strength(0.1F));
    public static final Block BLESSED_CLOTH_CURTAIN = new PaneBlock(settings(PALE_YELLOW, WOOL, GUITAR).strength(0.8F).nonOpaque());
    public static final Block BLESSED_BED = new BlessedBedBlock(settings(PALE_YELLOW, WOOD).strength(0.2F).pistonBehavior(DESTROY).nonOpaque());
    public static final Block PHLOGISTIC_FIRE = new PhlogisticFireBlock(settings(LIME, WOOL).luminance(constant(15)).pistonBehavior(DESTROY).replaceable().noCollision().breakInstantly());
    public static final Block PETRICHORIC_PLASMA = new PetrichoricPlasmaBlock(settings(LIME, INTENTIONALLY_EMPTY).strength(100F).luminance(constant(15)).pistonBehavior(DESTROY).emissiveLighting(Blocks::always).dropsNothing().nonOpaque().noCollision());
    public static final Block PETRICHORIC_VAPOR = new PetrichoricVaporBlock(settings(LIME, INTENTIONALLY_EMPTY).strength(100F).luminance(constant(15)).pistonBehavior(DESTROY).emissiveLighting(Blocks::always).dropsNothing().nonOpaque().noCollision());

    public static void init() {
        register(NETHERITE_PUMPKIN, "netherite_pumpkin");
        register(STELLAR_SEDIMENT, "stellar_sediment");
        register(STELLAR_MULCH, "stellar_mulch");
        register(STELLAR_FARMLAND, "stellar_farmland");
        register(MULCHBORNE_TUFT, "mulchborne_tuft");
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
        register(PHLOGISTIC_FIRE, "phlogistic_fire");
        register(PETRICHORIC_PLASMA, "petrichoric_plasma");
        register(PETRICHORIC_VAPOR, "petrichoric_vapor");
    }

    protected static <T extends Block> void register(T item, String name) {
        Registry.register(Registries.BLOCK, OperationStarcleave.id(name), item);
    }

    protected static ToIntFunction<BlockState> constant(int t) {
        return b -> t;
    }

    protected static AbstractBlock.Settings copyShallow(AbstractBlock settings) {
        return AbstractBlock.Settings.copyShallow(settings);
    }

    protected static AbstractBlock.Settings settings() {
        return AbstractBlock.Settings.create();
    }

    protected static AbstractBlock.Settings settings(MapColor mapColor) {
        return AbstractBlock.Settings.create().mapColor(mapColor);
    }

    protected static AbstractBlock.Settings settings(MapColor mapColor, BlockSoundGroup soundGroup) {
        return AbstractBlock.Settings.create().mapColor(mapColor).sounds(soundGroup);
    }

    protected static AbstractBlock.Settings settings(MapColor mapColor, BlockSoundGroup soundGroup, Instrument instrument) {
        return AbstractBlock.Settings.create().mapColor(mapColor).sounds(soundGroup).instrument(instrument);
    }

    protected static AbstractBlock.Settings settings(MapColor mapColor, BlockSoundGroup soundGroup, Instrument instrument, float hardness, float resistance) {
        return AbstractBlock.Settings.create().mapColor(mapColor).sounds(soundGroup).instrument(instrument).strength(hardness, resistance);
    }

    protected static AbstractBlock.Settings settings(MapColor mapColor, BlockSoundGroup soundGroup, Instrument instrument, float hardness, float resistance, int luminance) {
        return AbstractBlock.Settings.create().mapColor(mapColor).sounds(soundGroup).instrument(instrument).strength(hardness, resistance).luminance(constant(luminance));
    }

    protected static AbstractBlock.Settings settings(MapColor mapColor, BlockSoundGroup soundGroup, Instrument instrument, float hardness, float resistance, int luminance, boolean requiresTool) {
        AbstractBlock.Settings settings = AbstractBlock.Settings.create().mapColor(mapColor).sounds(soundGroup).instrument(instrument).strength(hardness, resistance).luminance(constant(luminance));
        if(requiresTool) {
            settings.requiresTool();
        }
        return settings;
    }

    protected static StairsBlock stairsOf(Block block) {
        return new StairsBlock(block.getDefaultState(), copyShallow(block));
    }

    protected static SlabBlock slabOf(AbstractBlock block) {
        return new SlabBlock(copyShallow(block));
    }

    protected static WallBlock wallOf(AbstractBlock block) {
        return new WallBlock(copyShallow(block).solid());
    }
}
