package phanastrae.operation_starcleave.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.fluid.OperationStarcleaveFluids;

import java.util.function.BiConsumer;
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
    public static final Block STELLAR_FARMLAND = new StellarFarmlandBlock(settings(COLOR_PURPLE, SoundType.SAND, BASEDRUM, 1.25f, 2f).lightLevel(b -> b.getValue(FarmBlock.MOISTURE) == 7 ? 15 : 2).isValidSpawn(SPAWN_NEVER).isViewBlocking(ALWAYS).isSuffocating(ALWAYS).randomTicks());
    public static final Block BISREEDS = new BisreedBlock(settings()
            .mapColor(COLOR_PINK)
            .sound(SoundType.CROP)
            .pushReaction(PushReaction.DESTROY)
            .noCollission()
            .randomTicks()
            .instabreak()
    );

    public static final Block STELLAR_MULCH = new StellarMulchBlock(settings(COLOR_PURPLE, MUD, BASEDRUM, 1.15f, 2f, 4).isValidSpawn(SPAWN_NEVER).randomTicks());
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
    public static final Block BLESSED_CLOTH_CURTAIN = new BlessedClothCurtainBlock(settings(SAND, WOOL, GUITAR).strength(0.8F).noOcclusion());

    public static final Block BLESSED_BED = new BlessedBedBlock(settings(SAND, SoundType.WOOD).strength(0.2F).pushReaction(DESTROY).noOcclusion());

    public static final Block PHLOGISTIC_FIRE = new PhlogisticFireBlock(settings(COLOR_LIGHT_GREEN, WOOL).lightLevel(constant(15)).pushReaction(DESTROY).replaceable().noCollission().instabreak());
    public static final Block PETRICHORIC_PLASMA = new PetrichoricPlasmaLiquidBlock(OperationStarcleaveFluids.FLOWING_PETRICHORIC_PLASMA, settings()
            .mapColor(COLOR_LIGHT_GREEN)
            .replaceable()
            .noCollission()
            .randomTicks()
            .strength(100.0F)
            .lightLevel(constant(15))
            .pushReaction(DESTROY)
            .noLootTable()
            .liquid()
            .sound(EMPTY)
    );
    public static final Block PETRICHORIC_VAPOR = new PetrichoricVaporBlock(settings(COLOR_LIGHT_GREEN, EMPTY).strength(100F).lightLevel(constant(15)).pushReaction(DESTROY).emissiveRendering(OperationStarcleaveBlocks::always).noLootTable().noOcclusion().noCollission());

    public static final Block NUCLEOSYNTHESEED = new NucleosyntheseedBlock(settings()
            .strength(4.0F)
            .mapColor(DyeColor.GREEN)
            .sound(NETHER_WOOD)
            .lightLevel(constant(13))
            .randomTicks()
    );
    public static final Block NUCLEIC_FISSUREROOT = new NucleicFissurerootBlock(settings()
            .strength(2.0F)
            .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? COLOR_GREEN : COLOR_LIGHT_GREEN)
            .sound(NETHER_WOOD)
            .instrument(NoteBlockInstrument.BASS)
            .lightLevel(constant(9))
            .randomTicks()
    );
    public static final Block NUCLEIC_FISSURELEAVES = new LeavesBlock(settings()
            .strength(0.2F)
            .mapColor(COLOR_LIGHT_GREEN)
            .sound(GRASS)
            .lightLevel(constant(12))
            .isValidSpawn(OperationStarcleaveBlocks::never)
            .isSuffocating(OperationStarcleaveBlocks::never)
            .isViewBlocking(OperationStarcleaveBlocks::never)
            .isRedstoneConductor(OperationStarcleaveBlocks::never)
            .pushReaction(PushReaction.DESTROY)
            .noOcclusion()
            .randomTicks()
    );
    public static final Block COAGULATED_PLASMA = new Block(settings()
            .strength(3.0F, 6.0F)
            .mapColor(TERRACOTTA_GREEN)
            .sound(SoundType.DEEPSLATE)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .lightLevel(constant(8))
            .requiresCorrectToolForDrops()
            .randomTicks()
    );
    public static final Block PLASMA_ICE = new PlasmaIceBlock(settings()
            .randomTicks()
            .mapColor(COLOR_LIGHT_GREEN)
            .lightLevel(constant(13))
            .strength(2.8F)
            .friction(0.989F)
            .sound(SoundType.GLASS)
    );

    public static void init(BiConsumer<ResourceLocation, Block> r) {
        r.accept(id("netherite_pumpkin"), NETHERITE_PUMPKIN);

        r.accept(id("stellar_sediment"), STELLAR_SEDIMENT);
        r.accept(id("stellar_farmland"), STELLAR_FARMLAND);
        r.accept(id("bisreeds"), BISREEDS);

        r.accept(id("stellar_mulch"), STELLAR_MULCH);
        r.accept(id("mulchborne_tuft"), MULCHBORNE_TUFT);

        r.accept(id("holy_moss"), HOLY_MOSS);
        r.accept(id("short_holy_moss"), SHORT_HOLY_MOSS);

        r.accept(id("stardust_block"), STARDUST_BLOCK);
        r.accept(id("stardust_cluster"), STARDUST_CLUSTER);

        r.accept(id("starbleached_log"), STARBLEACHED_LOG);
        r.accept(id("starbleached_wood"), STARBLEACHED_WOOD);
        r.accept(id("starbleached_leaves"), STARBLEACHED_LEAVES);
        r.accept(id("starbleached_tiles"), STARBLEACHED_TILES);
        r.accept(id("starbleached_tile_stairs"), STARBLEACHED_TILE_STAIRS);
        r.accept(id("starbleached_tile_slab"), STARBLEACHED_TILE_SLAB);
        r.accept(id("starbleached_tile_wall"), STARBLEACHED_TILE_WALL);
        r.accept(id("chiseled_starbleached_tiles"), CHISELED_STARBLEACHED_TILES);
        r.accept(id("imbued_starbleached_tiles"), IMBUED_STARBLEACHED_TILES);
        r.accept(id("starbleached_pearl_block"), STARBLEACHED_PEARL_BLOCK);

        r.accept(id("starbleach_cauldron"), STARBLEACH_CAULDRON);

        r.accept(id("stellar_tiles"), STELLAR_TILES);
        r.accept(id("stellar_tile_slab"), STELLAR_TILE_SLAB);

        r.accept(id("stellar_repulsor"), STELLAR_REPULSOR);

        r.accept(id("blessed_cloth_block"), BLESSED_CLOTH_BLOCK);
        r.accept(id("blessed_cloth_carpet"), BLESSED_CLOTH_CARPET);
        r.accept(id("blessed_cloth_curtain"), BLESSED_CLOTH_CURTAIN);

        r.accept(id("blessed_bed"), BLESSED_BED);

        r.accept(id("phlogistic_fire"), PHLOGISTIC_FIRE);
        r.accept(id("petrichoric_plasma"), PETRICHORIC_PLASMA);
        r.accept(id("petrichoric_vapor"), PETRICHORIC_VAPOR);

        r.accept(id("nucleosyntheseed"), NUCLEOSYNTHESEED);
        r.accept(id("nucleic_fissureroot"), NUCLEIC_FISSUREROOT);
        r.accept(id("nucleic_fissureleaves"), NUCLEIC_FISSURELEAVES);
        r.accept(id("coagulated_plasma"), COAGULATED_PLASMA);
        r.accept(id("plasma_ice"), PLASMA_ICE);
    }

    private static ResourceLocation id(String path) {
        return OperationStarcleave.id(path);
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
        return new CustomStairBlock(block.defaultBlockState(), copyShallow(block));
    }

    protected static SlabBlock slabOf(BlockBehaviour block) {
        return new SlabBlock(copyShallow(block));
    }

    protected static WallBlock wallOf(BlockBehaviour block) {
        return new WallBlock(copyShallow(block).forceSolidOn());
    }

    private static Boolean always(BlockState state, BlockGetter blockGetter, BlockPos pos, EntityType<?> entity) {
        return true;
    }

    private static Boolean never(BlockState state, BlockGetter blockGetter, BlockPos pos, EntityType<?> entity) {
        return false;
    }

    private static boolean always(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return true;
    }

    private static boolean never(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return false;
    }
}
