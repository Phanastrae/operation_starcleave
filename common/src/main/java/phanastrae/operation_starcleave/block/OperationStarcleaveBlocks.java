package phanastrae.operation_starcleave.block;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
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

import static net.minecraft.world.level.block.SoundType.*;
import static net.minecraft.world.level.block.state.BlockBehaviour.OffsetType.XYZ;
import static net.minecraft.world.level.block.state.properties.NoteBlockInstrument.*;
import static net.minecraft.world.level.material.MapColor.*;
import static net.minecraft.world.level.material.PushReaction.DESTROY;

public class OperationStarcleaveBlocks {
    protected static final BlockBehaviour.StatePredicate ALWAYS = (blockState, blockView, blockPos) -> true;
    protected static final BlockBehaviour.StatePredicate NEVER = (blockState, blockView, blockPos) -> false;
    protected static final BlockBehaviour.StateArgumentPredicate<EntityType<?>> SPAWN_ALWAYS = (blockState, blockView, blockPos, entityType) -> true;
    protected static final BlockBehaviour.StateArgumentPredicate<EntityType<?>> SPAWN_NEVER = (blockState, blockView, blockPos, entityType) -> true;

    public static final Block NETHERITE_PUMPKIN = new NetheritePumpkinBlock(settings()
            .strength(10.0F, 1200.0F)
            .mapColor(COLOR_BLACK)
            .sound(NETHERITE_BLOCK)
            .pushReaction(DESTROY)
            .isValidSpawn(SPAWN_ALWAYS)
            .requiresCorrectToolForDrops()
    );

    public static final Block STELLAR_SEDIMENT = new StellarSedimentBlock(settings()
            .strength(1.25F, 2F)
            .mapColor(COLOR_PURPLE)
            .sound(SoundType.SAND)
            .instrument(BASEDRUM)
            .isValidSpawn(SPAWN_NEVER)
            .lightLevel(constant(2))
    );
    public static final Block STELLAR_FARMLAND = new StellarFarmlandBlock(settings()
            .strength(1.25F, 2F)
            .mapColor(COLOR_PURPLE)
            .sound(SoundType.SAND)
            .instrument(BASEDRUM)
            .isValidSpawn(SPAWN_NEVER)
            .isViewBlocking(ALWAYS)
            .isSuffocating(ALWAYS)
            .lightLevel(b -> b.getValue(FarmBlock.MOISTURE) == 7 ? 15 : 2)
            .randomTicks()
    );

    public static final Block BISREEDS = new BisreedBlock(settings()
            .mapColor(COLOR_PINK)
            .sound(SoundType.CROP)
            .pushReaction(PushReaction.DESTROY)
            .noCollission()
            .randomTicks()
            .instabreak()
    );

    public static final Block STELLAR_MULCH = new StellarMulchBlock(settings()
            .strength(1.15F, 2F)
            .mapColor(COLOR_PURPLE)
            .sound(MUD)
            .instrument(BASEDRUM)
            .isValidSpawn(SPAWN_NEVER)
            .lightLevel(constant(4))
            .randomTicks()
    );
    public static final Block MULCHBORNE_TUFT = new MulchborneTuftBlock(settings()
            .mapColor(COLOR_MAGENTA)
            .sound(SoundType.GRASS)
            .offsetType(XYZ)
            .pushReaction(DESTROY)
            .lightLevel(constant(6))
            .replaceable()
            .noCollission()
            .instabreak()
    );

    public static final Block HOLY_MOSS = new HolyMossBlock(settings()
            .strength(1.15F, 2F)
            .mapColor(MapColor.SAND)
            .sound(MOSS)
            .instrument(BASEDRUM)
            .isValidSpawn(SPAWN_NEVER)
            .lightLevel(constant(13))
            .randomTicks()
    );
    public static final Block SHORT_HOLY_MOSS = new ShortHolyMossBlock(settings()
            .mapColor(MapColor.SAND)
            .sound(MOSS)
            .offsetType(XYZ)
            .pushReaction(DESTROY)
            .lightLevel(constant(13))
            .replaceable()
            .noCollission()
            .instabreak()
    );

    public static final Block STARDUST_BLOCK = new ColoredFallingBlock(new ColorRGBA(0xEF9FCFFF), settings()
            .strength(0.2F, 0.1F)
            .mapColor(ICE)
            .sound(SoundType.SAND)
            .instrument(BASEDRUM)
            .isValidSpawn(SPAWN_NEVER)
            .lightLevel(constant(9))
    );
    public static final Block STARDUST_CLUSTER = new StardustClusterBlock(settings()
            .lightLevel(constant(15))
            .replaceable()
            .instabreak()
            .noLootTable()
            .noOcclusion()
    );

    public static final Block STARBLEACHED_LOG = new RotatedPillarBlock(settings()
            .strength(2F, 6F)
            .mapColor(COLOR_GRAY)
            .sound(SoundType.STONE)
            .instrument(BASEDRUM)
            .isValidSpawn(SPAWN_NEVER)
            .lightLevel(constant(8))
            .requiresCorrectToolForDrops()
    );
    public static final Block STARBLEACHED_WOOD = new RotatedPillarBlock(copyShallow(STARBLEACHED_LOG));

    public static final Block STARBLEACHED_LEAVES = new StarbleachedLeavesBlock(settings()
            .strength(0.25F, 2F)
            .mapColor(COLOR_GRAY)
            .sound(SoundType.STONE)
            .instrument(BASEDRUM)
            .isValidSpawn(SPAWN_NEVER)
            .isSuffocating(NEVER)
            .isViewBlocking(NEVER)
            .isRedstoneConductor(NEVER)
            .lightLevel(constant(11))
            .requiresCorrectToolForDrops()
            .noOcclusion()
    );

    public static final Block STARBLEACHED_TILES = new Block(settings()
            .strength(1.5F, 6F)
            .mapColor(COLOR_GRAY)
            .sound(SoundType.STONE)
            .instrument(BASEDRUM)
            .isValidSpawn(SPAWN_NEVER)
            .lightLevel(constant(8))
            .requiresCorrectToolForDrops()
    );
    public static final Block STARBLEACHED_TILE_STAIRS = stairsOf(STARBLEACHED_TILES);
    public static final Block STARBLEACHED_TILE_SLAB = slabOf(STARBLEACHED_TILES);
    public static final Block STARBLEACHED_TILE_WALL = wallOf(STARBLEACHED_TILES);

    public static final Block CHISELED_STARBLEACHED_TILES = new Block(copyShallow(STARBLEACHED_TILES));

    public static final Block IMBUED_STARBLEACHED_TILES = new ImbuedStarbleachedTilesBlock(settings()
            .strength(1.25F, 6F)
            .mapColor(COLOR_CYAN)
            .sound(SoundType.STONE)
            .instrument(BASEDRUM)
            .isValidSpawn(SPAWN_NEVER)
            .lightLevel(constant(15))
            .requiresCorrectToolForDrops()
    );

    public static final Block STARBLEACHED_PEARL_BLOCK = new StarbleachedPearlBlock(settings()
            .strength(1.3F, 6F)
            .mapColor(COLOR_CYAN)
            .sound(GLASS)
            .instrument(BASEDRUM)
            .isValidSpawn(SPAWN_NEVER)
            .lightLevel(constant(12))
            .requiresCorrectToolForDrops()
    );

    public static final Block STARBLEACH_CAULDRON = new StarbleachCauldronBlock(copyShallow(Blocks.CAULDRON)
            .lightLevel(constant(13))
    );

    public static final Block STELLAR_TILES = new Block(settings()
            .strength(1.75F, 6F)
            .mapColor(COLOR_PURPLE)
            .sound(SoundType.DEEPSLATE)
            .instrument(BASEDRUM)
            .isValidSpawn(SPAWN_NEVER)
            .lightLevel(constant(2))
    );
    public static final Block STELLAR_TILE_SLAB = slabOf(STELLAR_TILES);

    public static final Block STELLAR_REPULSOR = new StellarRepulsorBlock(settings()
            .strength(1.75F, 6F)
            .mapColor(MapColor.SAND)
            .sound(SoundType.WOOL)
            .instrument(BASEDRUM)
            .lightLevel(constant(13))
            .isValidSpawn(SPAWN_NEVER)
    );

    public static final Block BLESSED_CLOTH_BLOCK = new BlessedClothBlock(settings()
            .strength(0.8F)
            .mapColor(MapColor.SAND)
            .sound(SoundType.WOOL)
            .instrument(GUITAR)
    );
    public static final Block BLESSED_CLOTH_CARPET = new BlessedClothCarpetBlock(settings()
            .strength(0.1F)
            .mapColor(MapColor.SAND)
            .sound(SoundType.WOOL)
    );
    public static final Block BLESSED_CLOTH_CURTAIN = new BlessedClothCurtainBlock(settings()
            .strength(0.8F)
            .mapColor(MapColor.SAND)
            .sound(SoundType.WOOL)
            .instrument(GUITAR)
            .noOcclusion()
    );

    public static final Block BLESSED_BED = new BlessedBedBlock(settings()
            .strength(0.2F)
            .mapColor(MapColor.SAND)
            .sound(SoundType.WOOD)
            .pushReaction(DESTROY)
            .noOcclusion()
    );

    public static final Block PHLOGISTIC_FIRE = new PhlogisticFireBlock(settings()
            .mapColor(COLOR_LIGHT_GREEN)
            .sound(SoundType.WOOL)
            .pushReaction(DESTROY)
            .lightLevel(constant(15))
            .replaceable()
            .noCollission()
            .instabreak()
    );
    public static final Block PETRICHORIC_PLASMA = new PetrichoricPlasmaLiquidBlock(OperationStarcleaveFluids.FLOWING_PETRICHORIC_PLASMA, settings()
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
    );
    public static final Block PETRICHORIC_VAPOR = new PetrichoricVaporBlock(settings()
            .strength(100F)
            .mapColor(COLOR_LIGHT_GREEN)
            .sound(EMPTY)
            .pushReaction(DESTROY)
            .emissiveRendering(ALWAYS)
            .lightLevel(constant(15))
            .noLootTable()
            .noOcclusion()
            .noCollission()
    );

    public static final Block NUCLEOSYNTHESEED = new NucleosyntheseedBlock(settings()
            .strength(5.0F)
            .mapColor(DyeColor.GREEN)
            .sound(NETHER_WOOD)
            .lightLevel(constant(13))
            .requiresCorrectToolForDrops()
            .randomTicks()
    );
    public static final Block NUCLEIC_FISSUREROOT = new NucleicFissurerootBlock(settings()
            .strength(2.5F)
            .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? COLOR_GREEN : COLOR_LIGHT_GREEN)
            .sound(NETHER_WOOD)
            .instrument(BASS)
            .lightLevel(constant(9))
            .requiresCorrectToolForDrops()
            .randomTicks()
    );
    public static final Block NUCLEIC_FISSURELEAVES = new NucleicFissureleavesBlock(settings()
            .strength(0.2F)
            .mapColor(COLOR_LIGHT_GREEN)
            .sound(SoundType.GRASS)
            .pushReaction(PushReaction.DESTROY)
            .isValidSpawn(SPAWN_NEVER)
            .isSuffocating(NEVER)
            .isViewBlocking(NEVER)
            .isRedstoneConductor(NEVER)
            .lightLevel(constant(12))
            .noOcclusion()
            .randomTicks()
    );

    public static final Block COAGULATED_PLASMA = new CoagulatedPlasmaBlock(settings()
            .strength(3.0F, 6.0F)
            .mapColor(TERRACOTTA_GREEN)
            .sound(SoundType.DEEPSLATE)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .lightLevel(constant(8))
            .requiresCorrectToolForDrops()
            .randomTicks()
    );
    public static final Block PLASMA_ICE = new PlasmaIceBlock(settings()
            .strength(2.8F)
            .friction(0.989F)
            .mapColor(COLOR_LIGHT_GREEN)
            .sound(SoundType.GLASS)
            .lightLevel(constant(13))
            .randomTicks()
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

    protected static BlockBehaviour.Properties settings() {
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
}
