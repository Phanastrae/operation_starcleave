package phanastrae.operation_starcleave.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import phanastrae.operation_starcleave.OperationStarcleave;

import java.util.function.ToIntFunction;

import static net.minecraft.world.level.block.Blocks.CAULDRON;
import static net.minecraft.world.level.block.SoundType.DEEPSLATE;
import static net.minecraft.world.level.block.SoundType.GRASS;
import static net.minecraft.world.level.block.SoundType.STONE;
import static net.minecraft.world.level.block.SoundType.WOOL;
import static net.minecraft.world.level.block.SoundType.*;
import static net.minecraft.world.level.block.state.BlockBehaviour.OffsetType.XYZ;
import static net.minecraft.world.level.block.state.properties.NoteBlockInstrument.BASEDRUM;
import static net.minecraft.world.level.block.state.properties.NoteBlockInstrument.GUITAR;
import static net.minecraft.world.level.material.MapColor.SAND;
import static net.minecraft.world.level.material.MapColor.*;
import static net.minecraft.world.level.material.PushReaction.DESTROY;

public class OperationStarcleaveBlocks {
    protected static final BlockBehaviour.StatePredicate ALWAYS = (blockState, blockView, blockPos) -> true;
    protected static final BlockBehaviour.StatePredicate NEVER = (blockState, blockView, blockPos) -> false;
    protected static final BlockBehaviour.StateArgumentPredicate<EntityType<?>> SPAWN_ALWAYS = (blockState, blockView, blockPos, entityType) -> true;
    protected static final BlockBehaviour.StateArgumentPredicate<EntityType<?>> SPAWN_NEVER = (blockState, blockView, blockPos, entityType) -> true;


    public static final Block NETHERITE_PUMPKIN = new NetheritePumpkinBlock(settings(COLOR_BLACK, NETHERITE_BLOCK).requiresCorrectToolForDrops().strength(10.0F, 1200.0F).isValidSpawn(SPAWN_ALWAYS).pushReaction(DESTROY));

    public static final Block STELLAR_SEDIMENT = new StellarSedimentBlock(settings(COLOR_PURPLE, SoundType.SAND, BASEDRUM, 1.15f, 2f, 2).isValidSpawn(SPAWN_NEVER));
    public static final Block STELLAR_MULCH = new StellarMulchBlock(settings(COLOR_PURPLE, MUD, BASEDRUM, 1.15f, 2f, 4).isValidSpawn(SPAWN_NEVER).randomTicks());
    public static final Block STELLAR_FARMLAND = new StellarFarmlandBlock(settings(COLOR_PURPLE, SoundType.SAND, BASEDRUM, 1.25f, 2f).lightLevel(b -> b.getValue(FarmBlock.MOISTURE) == 7 ? 15 : 2).isValidSpawn(SPAWN_NEVER).isViewBlocking(ALWAYS).isSuffocating(ALWAYS).randomTicks());
    public static final Block MULCHBORNE_TUFT = new MulchborneTuftBlock(settings(COLOR_MAGENTA, GRASS).lightLevel(constant(6)).offsetType(XYZ).pushReaction(DESTROY).replaceable().noCollission().instabreak());
    public static final Block HOLY_MOSS = new HolyMossBlock(settings(SAND, MOSS, BASEDRUM, 1.15f, 2f, 13).isValidSpawn(SPAWN_NEVER).randomTicks());
    public static final Block SHORT_HOLY_MOSS = new ShortHolyMossBlock(settings(SAND, MOSS).lightLevel(constant(13)).offsetType(XYZ).pushReaction(DESTROY).replaceable().noCollission().instabreak());
    public static final Block STARDUST_BLOCK = new ColoredFallingBlock(new ColorRGBA(0xEF9FCFFF), settings(ICE, SoundType.SAND, BASEDRUM, 0.2f, 0.1f, 9).isValidSpawn(SPAWN_NEVER));
    public static final Block STARDUST_CLUSTER = new StardustClusterBlock(settings().lightLevel(constant(15)).replaceable().instabreak().noLootTable().noOcclusion());
    public static final Block STARBLEACHED_LOG = new RotatedPillarBlock(settings(COLOR_GRAY, STONE, BASEDRUM, 2f, 6f, 8, true).isValidSpawn(SPAWN_NEVER));
    public static final Block STARBLEACHED_WOOD = new RotatedPillarBlock(copyShallow(STARBLEACHED_LOG));
    public static final Block STARBLEACHED_LEAVES = new StarbleachedLeavesBlock(settings(COLOR_GRAY, STONE, BASEDRUM, 0.25f, 2f, 11, true).isValidSpawn(SPAWN_NEVER).isSuffocating(NEVER).isViewBlocking(NEVER).isRedstoneConductor(NEVER).noOcclusion());
    public static final Block STARBLEACHED_TILES = new Block(settings(COLOR_GRAY, STONE, BASEDRUM, 1.5f, 6f, 8, true).isValidSpawn(SPAWN_NEVER));
    public static final Block STARBLEACHED_TILE_STAIRS = stairsOf(STARBLEACHED_TILES);
    public static final Block STARBLEACHED_TILE_SLAB = slabOf(STARBLEACHED_TILES);
    public static final Block STARBLEACHED_TILE_WALL = wallOf(STARBLEACHED_TILES);
    public static final Block CHISELED_STARBLEACHED_TILES = new Block(settings(COLOR_GRAY, STONE, BASEDRUM, 1.5f, 6f, 8, true).isValidSpawn(SPAWN_NEVER));
    public static final Block IMBUED_STARBLEACHED_TILES = new ImbuedStarbleachedTilesBlock(settings(COLOR_CYAN, STONE, BASEDRUM, 1.25f, 6f, 15, true).isValidSpawn(SPAWN_NEVER));
    public static final Block STARBLEACHED_PEARL_BLOCK = new StarbleachedPearlBlock(settings(COLOR_CYAN, GLASS, BASEDRUM, 1.3f, 6f, 12, true).isValidSpawn(SPAWN_NEVER));
    public static final Block STARBLEACH_CAULDRON = new StarbleachCauldronBlock(copyShallow(CAULDRON).lightLevel(constant(13)));
    public static final Block STELLAR_TILES = new Block(settings(COLOR_PURPLE, DEEPSLATE, BASEDRUM, 1.75f, 6f, 2).isValidSpawn(SPAWN_NEVER));
    public static final Block STELLAR_TILE_SLAB = slabOf(STELLAR_TILES);
    public static final Block STELLAR_REPULSOR = new StellarRepulsorBlock(settings(SAND, WOOL, BASEDRUM, 1.75f, 6f, 13).isValidSpawn(SPAWN_NEVER));
    public static final Block BLESSED_CLOTH_BLOCK = new BlessedClothBlock(settings(SAND, WOOL, GUITAR).strength(0.8F));
    public static final Block BLESSED_CLOTH_CARPET = new BlessedClothCarpetBlock(settings(SAND, WOOL).strength(0.1F));
    public static final Block BLESSED_CLOTH_CURTAIN = new IronBarsBlock(settings(SAND, WOOL, GUITAR).strength(0.8F).noOcclusion());
    public static final Block BLESSED_BED = new BlessedBedBlock(settings(SAND, SoundType.WOOD).strength(0.2F).pushReaction(DESTROY).noOcclusion());
    public static final Block PHLOGISTIC_FIRE = new PhlogisticFireBlock(settings(COLOR_LIGHT_GREEN, WOOL).lightLevel(constant(15)).pushReaction(DESTROY).replaceable().noCollission().instabreak());
    public static final Block PETRICHORIC_PLASMA = new PetrichoricPlasmaBlock(settings(COLOR_LIGHT_GREEN, EMPTY).strength(100F).lightLevel(constant(15)).pushReaction(DESTROY).emissiveRendering(Blocks::always).noLootTable().noOcclusion().noCollission());
    public static final Block PETRICHORIC_VAPOR = new PetrichoricVaporBlock(settings(COLOR_LIGHT_GREEN, EMPTY).strength(100F).lightLevel(constant(15)).pushReaction(DESTROY).emissiveRendering(Blocks::always).noLootTable().noOcclusion().noCollission());

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
        Registry.register(BuiltInRegistries.BLOCK, OperationStarcleave.id(name), item);
    }

    protected static ToIntFunction<BlockState> constant(int t) {
        return b -> t;
    }

    protected static BlockBehaviour.Properties copyShallow(BlockBehaviour settings) {
        return BlockBehaviour.Properties.ofLegacyCopy(settings);
    }

    protected static BlockBehaviour.Properties settings() {
        return BlockBehaviour.Properties.of();
    }

    protected static BlockBehaviour.Properties settings(MapColor mapColor) {
        return BlockBehaviour.Properties.of().mapColor(mapColor);
    }

    protected static BlockBehaviour.Properties settings(MapColor mapColor, SoundType soundGroup) {
        return BlockBehaviour.Properties.of().mapColor(mapColor).sound(soundGroup);
    }

    protected static BlockBehaviour.Properties settings(MapColor mapColor, SoundType soundGroup, NoteBlockInstrument instrument) {
        return BlockBehaviour.Properties.of().mapColor(mapColor).sound(soundGroup).instrument(instrument);
    }

    protected static BlockBehaviour.Properties settings(MapColor mapColor, SoundType soundGroup, NoteBlockInstrument instrument, float hardness, float resistance) {
        return BlockBehaviour.Properties.of().mapColor(mapColor).sound(soundGroup).instrument(instrument).strength(hardness, resistance);
    }

    protected static BlockBehaviour.Properties settings(MapColor mapColor, SoundType soundGroup, NoteBlockInstrument instrument, float hardness, float resistance, int luminance) {
        return BlockBehaviour.Properties.of().mapColor(mapColor).sound(soundGroup).instrument(instrument).strength(hardness, resistance).lightLevel(constant(luminance));
    }

    protected static BlockBehaviour.Properties settings(MapColor mapColor, SoundType soundGroup, NoteBlockInstrument instrument, float hardness, float resistance, int luminance, boolean requiresTool) {
        BlockBehaviour.Properties settings = BlockBehaviour.Properties.of().mapColor(mapColor).sound(soundGroup).instrument(instrument).strength(hardness, resistance).lightLevel(constant(luminance));
        if(requiresTool) {
            settings.requiresCorrectToolForDrops();
        }
        return settings;
    }

    protected static StairBlock stairsOf(Block block) {
        return new StairBlock(block.defaultBlockState(), copyShallow(block));
    }

    protected static SlabBlock slabOf(BlockBehaviour block) {
        return new SlabBlock(copyShallow(block));
    }

    protected static WallBlock wallOf(BlockBehaviour block) {
        return new WallBlock(copyShallow(block).forceSolidOn());
    }
}
