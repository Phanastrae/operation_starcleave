package phanastrae.operation_starcleave.block;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.fluid.OperationStarcleaveFluids;

import java.util.function.BiConsumer;
import java.util.function.ToIntFunction;

import static net.minecraft.world.level.block.SoundType.*;
import static net.minecraft.world.level.block.state.BlockBehaviour.OffsetType.XYZ;
import static net.minecraft.world.level.block.state.properties.NoteBlockInstrument.*;
import static net.minecraft.world.level.material.MapColor.*;
import static net.minecraft.world.level.material.PushReaction.DESTROY;

public class OperationStarcleaveBlocks {
    protected static final BlockBehaviour.StatePredicate ALWAYS = (blockState, blockView, blockPos) -> true;
    protected static final BlockBehaviour.StatePredicate NEVER = (blockState, blockView, blockPos) -> false;
    protected static final BlockBehaviour.StateArgumentPredicate<EntityType<?>> SPAWN_ALWAYS = (blockState, blockView, blockPos, entityType) -> true;
    protected static final BlockBehaviour.StateArgumentPredicate<EntityType<?>> SPAWN_NEVER = (blockState, blockView, blockPos, entityType) -> false;

    public static final BiMap<ResourceLocation, Block> UNREGISTERED_BLOCKS = HashBiMap.create();

    public static final Block NETHERITE_PUMPKIN = register(
            "netherite_pumpkin",
            new NetheritePumpkinBlock(properties()
                    .strength(10.0F, 1200.0F)
                    .mapColor(COLOR_BLACK)
                    .sound(NETHERITE_BLOCK)
                    .pushReaction(DESTROY)
                    .isValidSpawn(SPAWN_ALWAYS)
                    .requiresCorrectToolForDrops()
            )
    );

    public static final Block STELLAR_SEDIMENT = register(
            "stellar_sediment",
            new StellarSedimentBlock(properties()
                    .strength(1.25F, 2F)
                    .mapColor(COLOR_PURPLE)
                    .sound(SoundType.SAND)
                    .instrument(BASEDRUM)
                    .lightLevel(constant(2))
            )
    );
    public static final Block STELLAR_PATH = register(
            "stellar_path",
            new StellarPathBlock(properties()
                    .strength(1.15F, 2F)
                    .mapColor(COLOR_PURPLE)
                    .sound(SoundType.SAND)
                    .instrument(BASEDRUM)
                    .isViewBlocking(ALWAYS)
                    .isSuffocating(ALWAYS)
                    .lightLevel(constant(2))
            )
    );
    public static final Block STELLAR_FARMLAND = register(
            "stellar_farmland",
            new StellarFarmlandBlock(properties()
                    .strength(1.25F, 2F)
                    .mapColor(COLOR_PURPLE)
                    .sound(SoundType.SAND)
                    .instrument(BASEDRUM)
                    .isViewBlocking(ALWAYS)
                    .isSuffocating(ALWAYS)
                    .lightLevel(b -> b.getValue(FarmBlock.MOISTURE) == 7 ? 15 : 2)
                    .randomTicks()
            )
    );

    public static final Block BISREEDS = register(
            "bisreeds",
            new BisreedBlock(properties()
                    .mapColor(COLOR_PINK)
                    .sound(SoundType.CROP)
                    .pushReaction(PushReaction.DESTROY)
                    .noCollission()
                    .randomTicks()
                    .instabreak()
            )
    );

    public static final Block STELLAR_MULCH = register(
            "stellar_mulch",
            new StellarMulchBlock(properties()
                    .strength(1.15F, 2F)
                    .mapColor(COLOR_PURPLE)
                    .sound(MUD)
                    .instrument(BASEDRUM)
                    .lightLevel(constant(4))
                    .randomTicks()
            )
    );
    public static final Block MULCHBORNE_TUFT = register(
            "mulchborne_tuft",
            new MulchborneTuftBlock(properties()
                    .mapColor(COLOR_MAGENTA)
                    .sound(SoundType.GRASS)
                    .offsetType(XYZ)
                    .pushReaction(DESTROY)
                    .lightLevel(constant(6))
                    .replaceable()
                    .noCollission()
                    .instabreak()
            )
    );
    public static final Block POTTED_MULCHBORNE_TUFT = register(
            "potted_mulchborne_tuft",
            flowerPot(MULCHBORNE_TUFT, 6)
    );

    public static final Block HOLY_MOSS = register(
            "holy_moss",
            new HolyMossBlock(properties()
                    .strength(1.15F, 2F)
                    .mapColor(MapColor.SAND)
                    .sound(MOSS)
                    .instrument(BASEDRUM)
                    .lightLevel(constant(13))
                    .randomTicks()
            )
    );
    public static final Block SHORT_HOLY_MOSS = register(
            "short_holy_moss",
            new ShortHolyMossBlock(properties()
                    .mapColor(MapColor.SAND)
                    .sound(MOSS)
                    .offsetType(XYZ)
                    .pushReaction(DESTROY)
                    .lightLevel(constant(13))
                    .replaceable()
                    .noCollission()
                    .instabreak()
            )
    );
    public static final Block POTTED_SHORT_HOLY_MOSS = register(
            "potted_short_holy_moss",
            flowerPot(SHORT_HOLY_MOSS, 13)
    );

    public static final Block STARDUST_BLOCK = register(
            "stardust_block",
            new ColoredFallingBlock(new ColorRGBA(0xEF9FCFFF), properties()
                    .strength(0.2F, 0.1F)
                    .mapColor(ICE)
                    .sound(SoundType.SAND)
                    .instrument(BASEDRUM)
                    .lightLevel(constant(9))
            )
    );

    public static final Block STARDUST_BRICKS = register(
            "stardust_bricks",
            new Block(properties()
                    .strength(0.6F, 0.4F)
                    .mapColor(ICE)
                    .sound(TUFF_BRICKS)
                    .instrument(BASEDRUM)
                    .lightLevel(constant(9))
                    .requiresCorrectToolForDrops()
            )
    );
    public static final Block STARDUST_BRICK_STAIRS = register(
            "stardust_brick_stairs",
            stairsOf(STARDUST_BRICKS)
    );
    public static final Block STARDUST_BRICK_SLAB = register(
            "stardust_brick_slab",
            slabOf(STARDUST_BRICKS)
    );
    public static final Block STARDUST_BRICK_WALL = register(
            "stardust_brick_wall",
            wallOf(STARDUST_BRICKS)
    );

    public static final Block STARDUST_CLUSTER = register(
            "stardust_cluster",
            new StardustClusterBlock(properties()
                    .lightLevel(constant(15))
                    .replaceable()
                    .instabreak()
                    .noLootTable()
                    .noOcclusion()
            )
    );

    public static final Block STARBLEACHED_LOG = register(
            "starbleached_log",
            new RotatedPillarBlock(properties()
                    .strength(2F, 6F)
                    .mapColor(COLOR_GRAY)
                    .sound(SoundType.STONE)
                    .instrument(BASEDRUM)
                    .lightLevel(constant(8))
                    .requiresCorrectToolForDrops()
            )
    );
    public static final Block STARBLEACHED_WOOD = register(
            "starbleached_wood",
            new RotatedPillarBlock(copyShallow(STARBLEACHED_LOG))
    );

    public static final Block STARBLEACHED_LEAVES = register(
            "starbleached_leaves",
            new StarbleachedLeavesBlock(properties()
                    .strength(0.25F, 2F)
                    .mapColor(COLOR_GRAY)
                    .sound(SoundType.STONE)
                    .instrument(BASEDRUM)
                    .isSuffocating(NEVER)
                    .isViewBlocking(NEVER)
                    .isRedstoneConductor(NEVER)
                    .isValidSpawn(SPAWN_NEVER)
                    .lightLevel(constant(11))
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
            )
    );

    public static final Block STARBLEACHED_TILES = register(
            "starbleached_tiles",
            new Block(properties()
                    .strength(1.5F, 6F)
                    .mapColor(COLOR_GRAY)
                    .sound(SoundType.STONE)
                    .instrument(BASEDRUM)
                    .lightLevel(constant(8))
                    .requiresCorrectToolForDrops()
            )
    );
    public static final Block STARBLEACHED_TILE_STAIRS = register(
            "starbleached_tile_stairs",
            stairsOf(STARBLEACHED_TILES)
    );
    public static final Block STARBLEACHED_TILE_SLAB = register(
            "starbleached_tile_slab",
            slabOf(STARBLEACHED_TILES)
    );
    public static final Block STARBLEACHED_TILE_WALL = register(
            "starbleached_tile_wall",
            wallOf(STARBLEACHED_TILES)
    );

    public static final Block CHISELED_STARBLEACHED_TILES = register(
            "chiseled_starbleached_tiles",
            new Block(copyShallow(STARBLEACHED_TILES))
    );

    public static final Block IMBUED_STARBLEACHED_TILES = register(
            "imbued_starbleached_tiles",
            new ImbuedStarbleachedTilesBlock(properties()
                    .strength(1.25F, 6F)
                    .mapColor(COLOR_CYAN)
                    .sound(SoundType.STONE)
                    .instrument(BASEDRUM)
                    .lightLevel(constant(15))
                    .requiresCorrectToolForDrops()
            )
    );

    public static final Block STARBLEACHED_PEARL_BLOCK = register(
            "starbleached_pearl_block",
            new StarbleachedPearlBlock(properties()
                    .strength(1.3F, 6F)
                    .mapColor(COLOR_CYAN)
                    .sound(GLASS)
                    .instrument(BASEDRUM)
                    .lightLevel(constant(12))
                    .requiresCorrectToolForDrops()
            )
    );

    public static final Block STARBLEACH_CAULDRON = register(
            "starbleach_cauldron",
            new StarbleachCauldronBlock(copyShallow(Blocks.CAULDRON)
                    .lightLevel(constant(13))
            )
    );

    public static final Block STELLAR_TILES = register(
            "stellar_tiles",
            new Block(properties()
                    .strength(1.75F, 6F)
                    .mapColor(COLOR_PURPLE)
                    .sound(SoundType.DEEPSLATE)
                    .instrument(BASEDRUM)
                    .lightLevel(constant(2))
            )
    );
    public static final Block STELLAR_TILE_SLAB = register(
            "stellar_tile_slab",
            slabOf(STELLAR_TILES)
    );

    public static final Block STELLAR_REPULSOR = register(
            "stellar_repulsor",
            new StellarRepulsorBlock(properties()
                    .strength(1.75F, 6F)
                    .mapColor(MapColor.SAND)
                    .sound(SoundType.WOOL)
                    .instrument(BASEDRUM)
                    .lightLevel(constant(13))
            )
    );

    public static final Block BLESSED_CLOTH_BLOCK = register(
            "blessed_cloth_block",
            new BlessedClothBlock(properties()
                    .strength(0.8F)
                    .mapColor(MapColor.SAND)
                    .sound(SoundType.WOOL)
                    .instrument(GUITAR)
            )
    );
    public static final Block BLESSED_CLOTH_CARPET = register(
            "blessed_cloth_carpet",
            new BlessedClothCarpetBlock(properties()
                    .strength(0.1F)
                    .mapColor(MapColor.SAND)
                    .sound(SoundType.WOOL)
            )
    );
    public static final Block BLESSED_CLOTH_CURTAIN = register(
            "blessed_cloth_curtain",
            new BlessedClothCurtainBlock(properties()
                    .strength(0.8F)
                    .mapColor(MapColor.SAND)
                    .sound(SoundType.WOOL)
                    .instrument(GUITAR)
                    .noOcclusion()
            )
    );

    public static final Block BLESSED_BED = register(
            "blessed_bed",
            new BlessedBedBlock(properties()
                    .strength(0.2F)
                    .mapColor(MapColor.SAND)
                    .sound(SoundType.WOOD)
                    .pushReaction(DESTROY)
                    .noOcclusion()
            )
    );

    public static final Block SUBCAELIC_PHLOGLIGHT = register(
            "subcaelic_phloglight",
            new RotatedPillarBlock(properties()
                    .strength(0.3F)
                    .mapColor(COLOR_ORANGE)
                    .sound(SoundType.FROGLIGHT)
                    .lightLevel(constant(15))
            )
    );

    public static final Block PHLOGISTIC_FIRE = register(
            "phlogistic_fire",
            new PhlogisticFireBlock(properties()
                    .mapColor(COLOR_LIGHT_GREEN)
                    .sound(SoundType.WOOL)
                    .pushReaction(DESTROY)
                    .lightLevel(constant(15))
                    .replaceable()
                    .noCollission()
                    .instabreak()
            )
    );
    public static final Block PETRICHORIC_PLASMA = register(
            "petrichoric_plasma",
            new PetrichoricPlasmaLiquidBlock(OperationStarcleaveFluids.FLOWING_PETRICHORIC_PLASMA, properties()
                    .strength(100.0F)
                    .mapColor(COLOR_LIGHT_GREEN)
                    .sound(EMPTY)
                    .pushReaction(DESTROY)
                    .lightLevel(constant(15))
                    .replaceable()
                    .noCollission()
                    .randomTicks()
                    .noLootTable()
                    .liquid()
            )
    );
    public static final Block PETRICHORIC_VAPOR = register(
            "petrichoric_vapor",
            new PetrichoricVaporBlock(properties()
                    .strength(100F)
                    .mapColor(COLOR_LIGHT_GREEN)
                    .sound(EMPTY)
                    .pushReaction(DESTROY)
                    .emissiveRendering(ALWAYS)
                    .lightLevel(constant(15))
                    .noLootTable()
                    .noOcclusion()
                    .noCollission()
            )
    );

    public static final Block NUCLEOSYNTHESEED = register(
            "nucleosyntheseed",
            new NucleosyntheseedBlock(properties()
                    .strength(5.0F)
                    .mapColor(DyeColor.GREEN)
                    .sound(NETHER_WOOD)
                    .lightLevel(constant(13))
                    .requiresCorrectToolForDrops()
                    .randomTicks()
            )
    );
    public static final Block NUCLEIC_FISSUREROOT = register(
            "nucleic_fissureroot",
            new NucleicFissurerootBlock(properties()
                    .strength(2.5F)
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? COLOR_GREEN : COLOR_LIGHT_GREEN)
                    .sound(NETHER_WOOD)
                    .instrument(BASS)
                    .lightLevel(constant(9))
                    .requiresCorrectToolForDrops()
                    .randomTicks()
            )
    );
    public static final Block NUCLEIC_FISSURERIND = register(
            "nucleic_fissurerind",
            new NucleicFissurerootBlock(copyShallow(NUCLEIC_FISSUREROOT)
                    .mapColor(state -> COLOR_LIGHT_GREEN)
            )
    );
    // striped[sic]
    public static final Block STRIPED_NUCLEIC_FISSUREROOT = register(
            "striped_nucleic_fissureroot",
            new NucleicFissurerootBlock(copyShallow(NUCLEIC_FISSUREROOT))
    );
    public static final Block STRIPED_NUCLEIC_FISSURERIND = register(
            "striped_nucleic_fissurerind",
            new NucleicFissurerootBlock(copyShallow(NUCLEIC_FISSURERIND))
    );
    public static final Block NUCLEIC_FISSURELEAVES = register(
            "nucleic_fissureleaves",
            new NucleicFissureleavesBlock(properties()
                    .strength(0.2F)
                    .mapColor(COLOR_LIGHT_GREEN)
                    .sound(SoundType.GRASS)
                    .pushReaction(PushReaction.DESTROY)
                    .isSuffocating(NEVER)
                    .isViewBlocking(NEVER)
                    .isRedstoneConductor(NEVER)
                    .isValidSpawn(SPAWN_NEVER)
                    .lightLevel(constant(12))
                    .noOcclusion()
                    .randomTicks()
            )
    );

    public static final Block COAGULATED_PLASMA = register(
            "coagulated_plasma",
            new CoagulatedPlasmaBlock(properties()
                    .strength(3.0F, 6.0F)
                    .mapColor(TERRACOTTA_GREEN)
                    .sound(SoundType.DEEPSLATE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .lightLevel(constant(8))
                    .requiresCorrectToolForDrops()
                    .randomTicks()
            )
    );
    public static final Block PLASMA_ICE = register(
            "plasma_ice",
            new PlasmaIceBlock(properties()
                    .strength(2.8F)
                    .friction(0.989F)
                    .mapColor(COLOR_LIGHT_GREEN)
                    .sound(SoundType.GLASS)
                    .lightLevel(constant(13))
                    .randomTicks()
            )
    );

    public static final Block STARFLAKED_BISMUTH_BLOCK = register(
            "starflaked_bismuth_block",
            new Block(properties()
                    .strength(2.5F, 3.0F)
                    .mapColor(COLOR_PINK)
                    .sound(SoundType.METAL)
                    .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                    .requiresCorrectToolForDrops()
            )
    );
    public static final Block STARFLAKED_BISMUTH_SLAB = register(
            "starflaked_bismuth_slab",
            slabOf(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_BLOCK)
    );

    public static final Block CHISELED_STARFLAKED_BISMUTH_BLOCK = register(
            "chiseled_starflaked_bismuth_block",
            new Block(BlockBehaviour.Properties.ofFullCopy(STARFLAKED_BISMUTH_BLOCK)
                    .strength(2.0F, 2.5F)
            )
    );

    public static final Block STARFLAKED_BISMUTH_PILLAR = register(
            "starflaked_bismuth_pillar",
            new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(CHISELED_STARFLAKED_BISMUTH_BLOCK))
    );

    public static final Block STARFLAKED_BISMUTH_BRICKS = register(
            "starflaked_bismuth_bricks",
            new Block(BlockBehaviour.Properties.ofFullCopy(CHISELED_STARFLAKED_BISMUTH_BLOCK))
    );
    public static final Block STARFLAKED_BISMUTH_BRICK_STAIRS = register(
            "starflaked_bismuth_brick_stairs",
            stairsOf(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_BRICKS)
    );
    public static final Block STARFLAKED_BISMUTH_BRICK_SLAB = register(
            "starflaked_bismuth_brick_slab",
            slabOf(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_BRICKS)
    );
    public static final Block STARFLAKED_BISMUTH_BRICK_WALL = register(
            "starflaked_bismuth_brick_wall",
            wallOf(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_BRICKS)
    );

    public static final Block CHISELED_STARFLAKED_BISMUTH_BRICKS = register(
            "chiseled_starflaked_bismuth_bricks",
            new Block(BlockBehaviour.Properties.ofFullCopy(STARFLAKED_BISMUTH_BRICKS))
    );

    public static final Block STARFLAKED_BISMUTH_TILES = register(
            "starflaked_bismuth_tiles",
            new Block(BlockBehaviour.Properties.ofFullCopy(CHISELED_STARFLAKED_BISMUTH_BLOCK))
    );
    public static final Block STARFLAKED_BISMUTH_TILE_STAIRS = register(
            "starflaked_bismuth_tile_stairs",
            stairsOf(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_TILES)
    );
    public static final Block STARFLAKED_BISMUTH_TILE_SLAB = register(
            "starflaked_bismuth_tile_slab",
            slabOf(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_TILES)
    );
    public static final Block STARFLAKED_BISMUTH_TILE_WALL = register(
            "starflaked_bismuth_tile_wall",
            wallOf(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_TILES)
    );

    public static final Block STARFLAKED_BISMUTH_MOSAIC = register(
            "starflaked_bismuth_mosaic",
            new Block(BlockBehaviour.Properties.ofFullCopy(CHISELED_STARFLAKED_BISMUTH_BLOCK))
    );
    public static final Block STARFLAKED_BISMUTH_MOSAIC_STAIRS = register(
            "starflaked_bismuth_mosaic_stairs",
            stairsOf(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_MOSAIC)
    );
    public static final Block STARFLAKED_BISMUTH_MOSAIC_SLAB = register(
            "starflaked_bismuth_mosaic_slab",
            slabOf(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_MOSAIC)
    );
    public static final Block STARFLAKED_BISMUTH_MOSAIC_WALL = register(
            "starflaked_bismuth_mosaic_wall",
            wallOf(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_MOSAIC)
    );

    public static final Block STARFLAKED_BISMUTH_DOOR = register(
            "starflaked_bismuth_door",
            doorOf(OperationStarcleaveBlockSetTypes.STARFLAKED_BISMUTH, STARFLAKED_BISMUTH_BLOCK, 2.5F, 3.0F)
    );
    public static final Block STARFLAKED_BISMUTH_TRAPDOOR = register(
            "starflaked_bismuth_trapdoor",
            trapdoorOf(OperationStarcleaveBlockSetTypes.STARFLAKED_BISMUTH, STARFLAKED_BISMUTH_BLOCK, 2.5F, 3.0F)
    );

    public static final Block CELESTIAL_OPAL_BLOCK = register(
            "celestial_opal_block",
            new AmethystBlock(properties()
                    .strength(4.0F)
                    .mapColor(COLOR_LIGHT_GRAY)
                    .sound(SoundType.AMETHYST)
                    .lightLevel(constant(9))
                    .requiresCorrectToolForDrops()
            )
    );

    public static final Block CELESTIAL_OPAL_SPIRE = register(
            "celestial_opal_spire",
            new CelestialOpalClusterBlock(
                    16.0F,
                    3.0F,
                    BlockBehaviour.Properties.of()
                            .strength(4.0F)
                            .mapColor(COLOR_LIGHT_GRAY)
                            .sound(SoundType.AMETHYST_CLUSTER)
                            .pushReaction(PushReaction.DESTROY)
                            .lightLevel(constant(11))
                            .forceSolidOn()
                            .noOcclusion()
            )
    );
    public static final Block CELESTIAL_OPAL_CLUSTER = register(
            "celestial_opal_cluster",
            new CelestialOpalClusterBlock(
                    7.0F,
                    3.0F,
                    BlockBehaviour.Properties.ofLegacyCopy(CELESTIAL_OPAL_SPIRE)
            )
    );
    public static final Block LARGE_CELESTIAL_OPAL_BUD = register(
            "large_celestial_opal_bud",
            new CelestialOpalBudBlock(
                    5.0F, 3.0F, BlockBehaviour.Properties.ofLegacyCopy(CELESTIAL_OPAL_CLUSTER)
                    .sound(SoundType.MEDIUM_AMETHYST_BUD) // same as in vanilla - for some reason large buds have medium sounds?
                    .lightLevel(constant(8))
            )
    );
    public static final Block MEDIUM_CELESTIAL_OPAL_BUD = register(
            "medium_celestial_opal_bud",
            new CelestialOpalBudBlock(
                    4.0F, 3.0F, BlockBehaviour.Properties.ofLegacyCopy(CELESTIAL_OPAL_CLUSTER)
                    .sound(SoundType.LARGE_AMETHYST_BUD) // same as in vanilla - for some reason medium buds have large sounds?
                    .lightLevel(constant(5))
            )
    );
    public static final Block SMALL_CELESTIAL_OPAL_BUD = register(
            "small_celestial_opal_bud",
            new CelestialOpalBudBlock(
                    3.0F, 4.0F, BlockBehaviour.Properties.ofLegacyCopy(CELESTIAL_OPAL_CLUSTER)
                    .sound(SoundType.SMALL_AMETHYST_BUD)
                    .lightLevel(constant(2))
            )
    );

    private static <T extends Block> T register(String id, T block) {
        return register(OperationStarcleave.id(id), block);
    }

    private static <T extends Block> T register(ResourceLocation location, T block) {
        UNREGISTERED_BLOCKS.put(location, block);
        return block;
    }

    public static void init(BiConsumer<ResourceLocation, Block> r) {
        UNREGISTERED_BLOCKS.forEach(r);
        UNREGISTERED_BLOCKS.clear();
    }

    public static ResourceLocation id(String path) {
        return OperationStarcleave.id(path);
    }

    protected static ToIntFunction<BlockState> constant(int t) {
        return b -> t;
    }

    protected static BlockBehaviour.Properties properties() {
        return BlockBehaviour.Properties.of();
    }

    protected static BlockBehaviour.Properties copyShallow(BlockBehaviour settings) {
        return BlockBehaviour.Properties.ofLegacyCopy(settings);
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

    protected static Block flowerPot(Block potted, int lightLevel) {
        BlockBehaviour.Properties props = properties()
                .lightLevel(state -> lightLevel)
                .instabreak()
                .noOcclusion()
                .pushReaction(PushReaction.DESTROY);
        return new FlowerPotBlock(potted, props);
    }

    protected static DoorBlock doorOf(BlockSetType blockSetType, BlockBehaviour block, float destroyTime, float explosionResistance) {
        return new CustomDoorBlock(blockSetType, copyShallow(block)
                .strength(destroyTime, explosionResistance)
                .pushReaction(PushReaction.DESTROY)
                .noOcclusion()
        );
    }

    protected static TrapDoorBlock trapdoorOf(BlockSetType blockSetType, BlockBehaviour block, float destroyTime, float explosionResistance) {
        return new CustomTrapDoorBlock(blockSetType, copyShallow(block)
                .strength(destroyTime, explosionResistance)
                .isValidSpawn(SPAWN_NEVER)
                .noOcclusion()
        );
    }
}
