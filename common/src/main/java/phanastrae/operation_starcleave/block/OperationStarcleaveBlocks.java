package phanastrae.operation_starcleave.block;

import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.fluid.OperationStarcleaveFluids;
import phanastrae.operation_starcleave.mixin.common.accessor.BlockBehaviourAccessor;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToIntFunction;

import static net.minecraft.world.level.block.SoundType.*;
import static net.minecraft.world.level.block.state.BlockBehaviour.OffsetType.XYZ;
import static net.minecraft.world.level.block.state.BlockBehaviour.OffsetType.XZ;
import static net.minecraft.world.level.block.state.properties.NoteBlockInstrument.*;
import static net.minecraft.world.level.material.MapColor.*;
import static net.minecraft.world.level.material.PushReaction.DESTROY;

public class OperationStarcleaveBlocks {
    protected static final BlockBehaviour.StatePredicate ALWAYS = (blockState, blockView, blockPos) -> true;
    protected static final BlockBehaviour.StatePredicate NEVER = (blockState, blockView, blockPos) -> false;
    protected static final BlockBehaviour.StateArgumentPredicate<EntityType<?>> SPAWN_ALWAYS = (blockState, blockView, blockPos, entityType) -> true;
    protected static final BlockBehaviour.StateArgumentPredicate<EntityType<?>> SPAWN_NEVER = (blockState, blockView, blockPos, entityType) -> false;

    public static final Map<ResourceLocation, Block> UNREGISTERED_BLOCKS = new LinkedHashMap<>();
    public static final Map<Block, ResourceLocation> UNREGISTERED_BLOCK_LOCATIONS = new LinkedHashMap<>();

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
    public static final Block STELLARUBBLE_MIX = register(
            "stellarubble_mix",
            new StellarSedimentBlock(properties()
                    .strength(2.5F, 5.0F)
                    .mapColor(COLOR_LIGHT_BLUE)
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

    public static final Block STELLAR_BRICKS = register(
            "stellar_bricks",
            new StellarSedimentBlock(properties()
                    .strength(1.8F, 2.5F)
                    .mapColor(COLOR_PURPLE)
                    .sound(MUD_BRICKS)
                    .instrument(BASEDRUM)
                    .lightLevel(constant(2))
            )
    );
    public static final Block STELLAR_BRICK_STAIRS = register(
            "stellar_brick_stairs",
            stairsOf(STELLAR_BRICKS)
    );
    public static final Block STELLAR_BRICK_SLAB = register(
            "stellar_brick_slab",
            slabOf(STELLAR_BRICKS)
    );
    public static final Block STELLAR_BRICK_WALL = register(
            "stellar_brick_wall",
            wallOf(STELLAR_BRICKS)
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
    public static final Block TALL_HOLY_MOSS = register(
            "tall_holy_moss",
            new DoubleHolyMossBlock(properties()
                    .mapColor(MapColor.SAND)
                    .sound(MOSS)
                    .offsetType(XZ)
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

    public static final Block GREAT_TREES_CARE = register(
            "great_trees_care",
            starbleachedFlower(MobEffects.REGENERATION, 14.0F, MobEffects.BLINDNESS, 21.0F, COLOR_PINK, 4)
    );
    public static final Block POTTED_GREAT_TREES_CARE = register(
            "potted_great_trees_care",
            flowerPot(GREAT_TREES_CARE, 4)
    );

    public static final Block RED_MOURNER = register(
            "red_mourner",
            starbleachedFlower(MobEffects.DAMAGE_BOOST, 14.0F, MobEffects.HARM, 0.05F, COLOR_RED, 5)
    );
    public static final Block POTTED_RED_MOURNER = register(
            "potted_red_mourner",
            flowerPot(RED_MOURNER, 5)
    );

    public static final Block ANGELCLAW = register(
            "angelclaw",
            starbleachedFlower(MobEffects.DIG_SPEED, 21.0F, MobEffects.MOVEMENT_SLOWDOWN, 28.0F, COLOR_ORANGE, 6)
    );
    public static final Block POTTED_ANGELCLAW = register(
            "potted_angelclaw",
            flowerPot(ANGELCLAW, 6)
    );

    public static final Block ELDROSE = register(
            "eldrose",
            starbleachedFlower(MobEffects.GLOWING, 28.0F, TERRACOTTA_YELLOW, 7)
    );
    public static final Block POTTED_ELDROSE = register(
            "potted_eldrose",
            flowerPot(ELDROSE, 7)
    );

    public static final Block WITCHGLARE = register(
            "witchglare",
            starbleachedFlower(MobEffects.NIGHT_VISION, 21.0F, MobEffects.POISON, 7.0F, COLOR_LIGHT_GREEN, 6)
    );
    public static final Block POTTED_WITCHGLARE = register(
            "potted_witchglare",
            flowerPot(WITCHGLARE, 6)
    );

    public static final Block BLUE_DREAMER = register(
            "blue_dreamer",
            starbleachedFlower(MobEffects.DAMAGE_RESISTANCE, 14.0F, MobEffects.WEAKNESS, 21.0F, COLOR_BLUE, 5)
    );
    public static final Block POTTED_BLUE_DREAMER = register(
            "potted_blue_dreamer",
            flowerPot(BLUE_DREAMER, 5)
    );

    public static final Block DRAGONS_MAW = register(
            "dragons_maw",
            starbleachedFlower(MobEffects.HEALTH_BOOST, 14.0F, MobEffects.HUNGER, 14.0F, COLOR_PURPLE, 4)
    );
    public static final Block POTTED_DRAGONS_MAW = register(
            "potted_dragons_maw",
            flowerPot(DRAGONS_MAW, 4)
    );

    public static final Block STARCLOVERS = register(
            "starclovers",
            new StarcloversBlock(properties()
                    .mapColor(COLOR_MAGENTA)
                    .sound(SoundType.PINK_PETALS)
                    .pushReaction(PushReaction.DESTROY)
                    .noCollission()
            )
    );
    
    public static final Block STARCLOVER_BUSH = register(
            "starclover_bush",
            new TallStarbleachedFlowerBlock(
                    properties()
                            .mapColor(COLOR_MAGENTA)
                            .sound(SoundType.GRASS)
                            .offsetType(BlockBehaviour.OffsetType.XZ)
                            .pushReaction(PushReaction.DESTROY)
                            .noCollission()
                            .instabreak()
            )
    );
    public static final Block STARFLOWER = register(
            "starflower",
            new TallStarbleachedFlowerBlock(
                    properties()
                            .mapColor(TERRACOTTA_WHITE)
                            .sound(SoundType.GRASS)
                            .offsetType(BlockBehaviour.OffsetType.XZ)
                            .pushReaction(PushReaction.DESTROY)
                            .noCollission()
                            .instabreak()
            )
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
                    .noOcclusion()
            )
    );

    public static final Block FELLCRUST = register(
            "fellcrust",
            new Block(properties()
                    .strength(1.3F, 4.0F)
                    .mapColor(COLOR_LIGHT_BLUE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
            )
    );
    public static final Block FELLCRUST_STAIRS = register(
            "fellcrust_stairs",
            stairsOf(FELLCRUST)
    );
    public static final Block FELLCRUST_SLAB = register(
            "fellcrust_slab",
            slabOf(FELLCRUST)
    );
    public static final Block FELLCRUST_WALL = register(
            "fellcrust_wall",
            wallOf(FELLCRUST)
    );

    public static final Block CHISELED_FELLCRUST = register(
            "chiseled_fellcrust",
            new Block(BlockBehaviour.Properties.ofFullCopy(FELLCRUST))
    );

    public static final Block SMOOTH_FELLCRUST = register(
            "smooth_fellcrust",
            new Block(properties()
                    .strength(3.0F, 5.0F)
                    .mapColor(COLOR_LIGHT_BLUE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
            )
    );
    public static final Block SMOOTH_FELLCRUST_STAIRS = register(
            "smooth_fellcrust_stairs",
            stairsOf(SMOOTH_FELLCRUST)
    );
    public static final Block SMOOTH_FELLCRUST_SLAB = register(
            "smooth_fellcrust_slab",
            slabOf(SMOOTH_FELLCRUST)
    );
    public static final Block SMOOTH_FELLCRUST_WALL = register(
            "smooth_fellcrust_wall",
            wallOf(SMOOTH_FELLCRUST)
    );

    public static final Block CHISELED_SMOOTH_FELLCRUST = register(
            "chiseled_smooth_fellcrust",
            new Block(BlockBehaviour.Properties.ofFullCopy(SMOOTH_FELLCRUST))
    );

    public static final Block SMOOTH_FELLCRUST_BRICKS = register(
            "smooth_fellcrust_bricks",
            new Block(BlockBehaviour.Properties.ofFullCopy(SMOOTH_FELLCRUST))
    );
    public static final Block SMOOTH_FELLCRUST_BRICK_STAIRS = register(
            "smooth_fellcrust_brick_stairs",
            stairsOf(SMOOTH_FELLCRUST_BRICKS)
    );
    public static final Block SMOOTH_FELLCRUST_BRICK_SLAB = register(
            "smooth_fellcrust_brick_slab",
            slabOf(SMOOTH_FELLCRUST_BRICKS)
    );
    public static final Block SMOOTH_FELLCRUST_BRICK_WALL = register(
            "smooth_fellcrust_brick_wall",
            wallOf(SMOOTH_FELLCRUST_BRICKS)
    );

    public static final Block SMOOTH_FELLCRUST_PILLAR = register(
            "smooth_fellcrust_pillar",
            new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(SMOOTH_FELLCRUST))
    );

    public static final Block CUT_FELLCRUST = register(
            "cut_fellcrust",
            new Block(BlockBehaviour.Properties.ofFullCopy(FELLCRUST))
    );
    public static final Block CUT_FELLCRUST_STAIRS = register(
            "cut_fellcrust_stairs",
            stairsOf(CUT_FELLCRUST)
    );
    public static final Block CUT_FELLCRUST_SLAB = register(
            "cut_fellcrust_slab",
            slabOf(CUT_FELLCRUST)
    );
    public static final Block CUT_FELLCRUST_WALL = register(
            "cut_fellcrust_wall",
            wallOf(CUT_FELLCRUST)
    );

    public static final Block COBBLED_FELLCRUST = register(
            "cobbled_fellcrust",
            new Block(properties()
                    .strength(1.8F, 4.0F)
                    .mapColor(TERRACOTTA_BLUE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
            )
    );
    public static final Block COBBLED_FELLCRUST_STAIRS = register(
            "cobbled_fellcrust_stairs",
            stairsOf(COBBLED_FELLCRUST)
    );
    public static final Block COBBLED_FELLCRUST_SLAB = register(
            "cobbled_fellcrust_slab",
            slabOf(COBBLED_FELLCRUST)
    );
    public static final Block COBBLED_FELLCRUST_WALL = register(
            "cobbled_fellcrust_wall",
            wallOf(COBBLED_FELLCRUST)
    );

    public static final Block POLISHED_FELLCRUST = register(
            "polished_fellcrust",
            new Block(properties()
                    .strength(2.2F, 4.0F)
                    .mapColor(TERRACOTTA_BLUE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
            )
    );
    public static final Block POLISHED_FELLCRUST_STAIRS = register(
            "polished_fellcrust_stairs",
            stairsOf(POLISHED_FELLCRUST)
    );
    public static final Block POLISHED_FELLCRUST_SLAB = register(
            "polished_fellcrust_slab",
            slabOf(POLISHED_FELLCRUST)
    );

    public static final Block POLISHED_FELLCRUST_BRICKS = register(
            "polished_fellcrust_bricks",
            new Block(BlockBehaviour.Properties.ofFullCopy(POLISHED_FELLCRUST))
    );
    public static final Block POLISHED_FELLCRUST_BRICK_STAIRS = register(
            "polished_fellcrust_brick_stairs",
            stairsOf(POLISHED_FELLCRUST_BRICKS)
    );
    public static final Block POLISHED_FELLCRUST_BRICK_SLAB = register(
            "polished_fellcrust_brick_slab",
            slabOf(POLISHED_FELLCRUST_BRICKS)
    );
    public static final Block POLISHED_FELLCRUST_BRICK_WALL = register(
            "polished_fellcrust_brick_wall",
            wallOf(POLISHED_FELLCRUST_BRICKS)
    );

    public static final Block CUT_POLISHED_FELLCRUST = register(
            "cut_polished_fellcrust",
            new Block(BlockBehaviour.Properties.ofFullCopy(POLISHED_FELLCRUST)
            )
    );
    public static final Block CUT_POLISHED_FELLCRUST_STAIRS = register(
            "cut_polished_fellcrust_stairs",
            stairsOf(CUT_POLISHED_FELLCRUST)
    );
    public static final Block CUT_POLISHED_FELLCRUST_SLAB = register(
            "cut_polished_fellcrust_slab",
            slabOf(CUT_POLISHED_FELLCRUST)
    );

    public static final Block ASTERUBBLE = register(
            "asterubble",
            new Block(properties()
                    .strength(4.0F, 9.0F)
                    .mapColor(TERRACOTTA_BLUE)
                    .sound(SoundType.DEEPSLATE)
                    .instrument(SNARE)
                    .requiresCorrectToolForDrops()
            )
    );
    public static final Block ASTERUBBLE_STAIRS = register(
            "asterubble_stairs",
            stairsOf(ASTERUBBLE)
    );
    public static final Block ASTERUBBLE_SLAB = register(
            "asterubble_slab",
            slabOf(ASTERUBBLE)
    );
    public static final Block ASTERUBBLE_WALL = register(
            "asterubble_wall",
            wallOf(ASTERUBBLE)
    );

    public static final Block SKYSHELL_BLOCK = register(
            "skyshell_block",
            new Block(properties()
                    .strength(0.5F)
                    .mapColor(DyeColor.WHITE)
                    .sound(DECORATED_POT)
                    .instrument(XYLOPHONE)
            )
    );
    public static final Block SKYSHELL_STAIRS = register(
            "skyshell_stairs",
            stairsOf(SKYSHELL_BLOCK)
    );
    public static final Block SKYSHELL_SLAB = register(
            "skyshell_slab",
            slabOf(SKYSHELL_BLOCK)
    );
    public static final Block SKYSHELL_WALL = register(
            "skyshell_wall",
            wallOf(SKYSHELL_BLOCK)
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
            new RotatedPillarBlock(legacyCopy(STARBLEACHED_LOG))
    );

    public static final Block STARBLEACHED_LEAVES = register(
            "starbleached_leaves",
            new StarbleachedLeavesBlock(properties()
                    .strength(0.3F, 2F)
                    .mapColor(COLOR_GRAY)
                    .sound(AZALEA_LEAVES)
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

    public static final Block STARBLEACHED_LEAF_LITTER = register(
            "starbleached_leaf_litter",
            new StarbleachedLeafLitterBlock(properties()
                    .mapColor(COLOR_PINK)
                    .sound(SoundType.PINK_PETALS) // leaf litter sounds do not exist on 1.21.1, so just use pink petal sounds
                    .pushReaction(PushReaction.DESTROY)
                    .lightLevel(state -> 4 + state.getValue(StarbleachedLeafLitterBlock.SEGMENT_AMOUNT))
                    .replaceable()
                    .noCollission()
            )
    );

    public static final Block STARBLEACHED_LEAF_BUNCH_BLOCK = register(
            "starbleached_leaf_bunch_block",
            new StarbleachedLeafBundleBlock(properties()
                    .strength(0.9F, 3.5F)
                    .mapColor(COLOR_PINK)
                    .sound(AZALEA_LEAVES)
                    .instrument(BASEDRUM)
                    .lightLevel(constant(13))
                    .requiresCorrectToolForDrops()
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

    public static final Block STARBLEACHED_PRESSURE_PLATE = register(
            "starbleached_pressure_plate",
            new CustomPressurePlateBlock(
                    OperationStarcleaveBlockSetTypes.STARBLEACHED,
                    properties()
                            .strength(0.5F)
                            .mapColor(COLOR_GRAY)
                            .instrument(NoteBlockInstrument.BASEDRUM)
                            .pushReaction(PushReaction.DESTROY)
                            .forceSolidOn()
                            .requiresCorrectToolForDrops()
                            .noCollission()
            )
    );

    public static final Block STARBLEACHED_BUTTON = register(
            "starbleached_button",
            new CustomButtonBlock(
                    OperationStarcleaveBlockSetTypes.STARBLEACHED,
                    25, // midpoint of stone and wood
                    properties()
                            .strength(0.5F)
                            .pushReaction(PushReaction.DESTROY)
                            .noCollission()
            )
    );

    public static final Block CHISELED_STARBLEACHED_TILES = register(
            "chiseled_starbleached_tiles",
            new Block(legacyCopy(STARBLEACHED_TILES))
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

    public static final Block STARTOUCHED_LOG = register(
            "startouched_log",
            new RotatedPillarBlock(
                    properties().mapColor(TERRACOTTA_PINK)
                            .instrument(BASS)
                            .strength(3.0F)
                            .sound(SoundType.WOOD)
            )
    );
    public static final Block STARTOUCHED_WOOD = register(
            "startouched_wood",
            new RotatedPillarBlock(
                    properties().mapColor(TERRACOTTA_PINK)
                            .instrument(BASS)
                            .strength(3.0F)
                            .sound(SoundType.WOOD)
            )
    );
    public static final Block STARTOUCHED_PLANKS = register(
            "startouched_planks",
            new Block(
                    properties().mapColor(TERRACOTTA_PINK)
                            .instrument(BASS)
                            .strength(3.0F, 4.0F)
                            .sound(SoundType.WOOD)
            )
    );
    public static final Block STARTOUCHED_STAIRS = register(
            "startouched_stairs",
            stairsOf(STARTOUCHED_PLANKS)
    );
    public static final Block STARTOUCHED_SLAB = register(
            "startouched_slab",
            slabOf(STARTOUCHED_PLANKS)
    );
    public static final Block STARTOUCHED_FENCE = register(
            "startouched_fence",
            fenceOf(STARTOUCHED_PLANKS)
    );
    public static final Block STARTOUCHED_FENCE_GATE = register(
            "startouched_fence_gate",
            fenceGateOf(OperationStarcleaveWoodTypes.STARTOUCHED_WOODSET, STARTOUCHED_PLANKS)
    );
    public static final Block STARTOUCHED_DOOR = register(
            "startouched_door",
            doorOf(OperationStarcleaveBlockSetTypes.STARTOUCHED, STARTOUCHED_PLANKS, 4.0F, 4.0F)
    );
    public static final Block STARTOUCHED_TRAPDOOR = register(
            "startouched_trapdoor",
            trapdoorOf(OperationStarcleaveBlockSetTypes.STARTOUCHED, STARTOUCHED_PLANKS, 4.0F, 4.0F)
    );
    public static final Block STARTOUCHED_PRESSURE_PLATE = register(
            "startouched_pressure_plate",
            new CustomPressurePlateBlock(
                    OperationStarcleaveBlockSetTypes.STARTOUCHED,
                    properties()
                            .strength(0.5F)
                            .mapColor(TERRACOTTA_PINK)
                            .instrument(BASS)
                            .pushReaction(PushReaction.DESTROY)
                            .forceSolidOn()
                            .noCollission()
            )
    );
    public static final Block STARTOUCHED_BUTTON = register(
            "startouched_button",
            new CustomButtonBlock(
                    OperationStarcleaveBlockSetTypes.STARTOUCHED,
                    30,
                    properties()
                            .strength(0.5F)
                            .pushReaction(PushReaction.DESTROY)
                            .noCollission()
            )
    );
    public static final Block STARTOUCHED_SIGN = register(
            "startouched_sign",
            new StandingSignBlock(
                    OperationStarcleaveWoodTypes.STARTOUCHED_WOODSET,
                    properties()
                            .strength(1.0F)
                            .mapColor(MapColor.TERRACOTTA_PINK)
                            .instrument(NoteBlockInstrument.BASS)
                            .forceSolidOn()
                            .noCollission()
            )
    );
    public static final Block STARTOUCHED_WALL_SIGN = register(
            "startouched_wall_sign",
            new WallSignBlock(
                    OperationStarcleaveWoodTypes.STARTOUCHED_WOODSET,
                    properties()
                            .strength(1.0F)
                            .mapColor(TERRACOTTA_PINK)
                            .instrument(NoteBlockInstrument.BASS)
                            .dropsLike(STARTOUCHED_SIGN)
                            .forceSolidOn()
                            .noCollission()
                            .ignitedByLava()
            )
    );
    public static final Block STARTOUCHED_HANGING_SIGN = register(
            "startouched_hanging_sign",
            new CeilingHangingSignBlock(
                    OperationStarcleaveWoodTypes.STARTOUCHED_WOODSET,
                    properties()
                            .strength(1.0F)
                            .mapColor(TERRACOTTA_PINK)
                            .instrument(NoteBlockInstrument.BASS)
                            .forceSolidOn()
                            .noCollission()
            )
    );
    public static final Block STARTOUCHED_WALL_HANGING_SIGN = register(
            "startouched_wall_hanging_sign",
            new WallHangingSignBlock(
                    OperationStarcleaveWoodTypes.STARTOUCHED_WOODSET,
                    properties()
                            .strength(1.0F)
                            .mapColor(TERRACOTTA_PINK)
                            .instrument(NoteBlockInstrument.BASS)
                            .dropsLike(STARTOUCHED_HANGING_SIGN)
                            .forceSolidOn()
                            .noCollission()
                            .ignitedByLava()
            )
    );

    public static final Block STARTOUCHED_TORCH = register(
            "startouched_torch",
            new StartouchedTorchBlock(
                    OperationStarcleaveParticleTypes.STARBLEACH_SWIRL,
                    properties()
                            .lightLevel(constant(14))
                            .sound(SoundType.WOOD)
                            .pushReaction(PushReaction.DESTROY)
                            .noCollission()
                            .instabreak()
            )
    );
    public static final Block STARTOUCHED_WALL_TORCH = register(
            "startouched_wall_torch",
            new StartouchedWallTorchBlock(
                    OperationStarcleaveParticleTypes.STARBLEACH_SWIRL,
                    properties()
                            .lightLevel(constant(14))
                            .sound(SoundType.WOOD)
                            .pushReaction(PushReaction.DESTROY)
                            .dropsLike(STARTOUCHED_TORCH)
                            .noCollission()
                            .instabreak()
            )
    );

    public static final Block STARBLEACHED_SAPLING = register(
            "starbleached_sapling",
            new StarbleachedSaplingBlock(properties()
                    .mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.GRASS)
                    .pushReaction(PushReaction.DESTROY)
                    .lightLevel(constant(7))
                    .noCollission()
                    .instabreak()
            )
    );

    public static final Block POTTED_STARBLEACHED_SAPLING = register(
            "potted_starbleached_sapling",
            flowerPot(STARBLEACHED_SAPLING, 7)
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
            new StarbleachCauldronBlock(legacyCopy(Blocks.CAULDRON)
                    .lightLevel(constant(13))
            )
    );

    public static final Block STELLAR_TILES = register(
            "stellar_tiles",
            new StellarTilesBlock(properties()
                    .strength(1.75F, 6F)
                    .mapColor(COLOR_PURPLE)
                    .sound(SoundType.DEEPSLATE)
                    .instrument(BASEDRUM)
                    .lightLevel(constant(2))
                    .requiresCorrectToolForDrops()
            )
    );
    public static final Block STELLAR_TILE_SLAB = register(
            "stellar_tile_slab",
            slabOf(STELLAR_TILES, StellarTileSlabBlock::new)
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

    public static final Block HOLY_LEAF_PLATFORM = register(
            "holy_leaf_platform",
            new PlatformBlock(properties()
                    .strength(1.6F)
                    .mapColor(COLOR_PINK)
                    .instrument(GUITAR)
                    .isSuffocating(NEVER)
                    .isViewBlocking(NEVER)
                    .isRedstoneConductor(NEVER)
                    .isValidSpawn(SPAWN_NEVER)
                    .lightLevel(constant(9))
                    .dynamicShape()
                    .noCollission()
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
    public static final Block BLESSED_CLOTH_STAIRS = register(
            "blessed_cloth_stairs",
            stairsOf(BLESSED_CLOTH_BLOCK, BlessedClothStairs::new)
    );
    public static final Block BLESSED_CLOTH_SLAB = register(
            "blessed_cloth_slab",
            slabOf(BLESSED_CLOTH_BLOCK, BlessedClothSlab::new)
    );
    public static final Block BLESSED_CLOTH_CARPET = register(
            "blessed_cloth_carpet",
            new BlessedClothCarpetBlock(properties()
                    .strength(0.1F)
                    .mapColor(MapColor.SAND)
                    .sound(SoundType.WOOL)
            )
    );

    public static final Block BLESSED_CLOTH_PADDING = register(
            "blessed_cloth_padding",
            new BlessedClothBlock(BlockBehaviour.Properties.ofFullCopy(BLESSED_CLOTH_BLOCK))
    );
    public static final Block BLESSED_CLOTH_PADDING_STAIRS = register(
            "blessed_cloth_padding_stairs",
            stairsOf(BLESSED_CLOTH_PADDING, BlessedClothStairs::new)
    );
    public static final Block BLESSED_CLOTH_PADDING_SLAB = register(
            "blessed_cloth_padding_slab",
            slabOf(BLESSED_CLOTH_PADDING, BlessedClothSlab::new)
    );
    public static final Block BLESSED_CLOTH_CARPET_PADDING = register(
            "blessed_cloth_carpet_padding",
            new BlessedClothCarpetBlock(BlockBehaviour.Properties.ofFullCopy(BLESSED_CLOTH_CARPET))
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
            new NucleicFissurerootBlock(legacyCopy(NUCLEIC_FISSUREROOT)
                    .mapColor(state -> COLOR_LIGHT_GREEN)
            )
    );
    // striped[sic]
    public static final Block STRIPED_NUCLEIC_FISSUREROOT = register(
            "striped_nucleic_fissureroot",
            new NucleicFissurerootBlock(legacyCopy(NUCLEIC_FISSUREROOT))
    );
    public static final Block STRIPED_NUCLEIC_FISSURERIND = register(
            "striped_nucleic_fissurerind",
            new NucleicFissurerootBlock(legacyCopy(NUCLEIC_FISSURERIND))
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

    public static final Block OURANIC_CHIP_BLOCK = register(
            "ouranic_chip_block",
            new OuranicBlock(properties()
                    .strength(2.5F, 5.0F)
                    .mapColor(DyeColor.LIME)
                    .instrument(BASEDRUM)
                    .sound(COPPER)
                    .lightLevel(constant(12))
                    .requiresCorrectToolForDrops()
            )
    );
    public static final Block OURANIC_CHIP_STAIRS = register(
            "ouranic_chip_stairs",
            stairsOf(OURANIC_CHIP_BLOCK, OuranicStairBlock::new)
    );
    public static final Block OURANIC_CHIP_SLAB = register(
            "ouranic_chip_slab",
            slabOf(OURANIC_CHIP_BLOCK, OuranicSlabBlock::new)
    );
    public static final Block OURANIC_CHIP_WALL = register(
            "ouranic_chip_wall",
            wallOf(OURANIC_CHIP_BLOCK, OuranicWallBlock::new)
    );

    public static final Block CHISELED_OURANIC_CHIP_BLOCK = register(
            "chiseled_ouranic_chip_block",
            new OuranicBlock(BlockBehaviour.Properties.ofFullCopy(OURANIC_CHIP_BLOCK))
    );

    public static final Block OURANIC_BRICKS = register(
            "ouranic_bricks",
            new OuranicBlock(BlockBehaviour.Properties.ofFullCopy(OURANIC_CHIP_BLOCK))
    );
    public static final Block OURANIC_BRICK_STAIRS = register(
            "ouranic_brick_stairs",
            stairsOf(OURANIC_BRICKS, OuranicStairBlock::new)
    );
    public static final Block OURANIC_BRICK_SLAB = register(
            "ouranic_brick_slab",
            slabOf(OURANIC_BRICKS, OuranicSlabBlock::new)
    );
    public static final Block OURANIC_BRICK_WALL = register(
            "ouranic_brick_wall",
            wallOf(OURANIC_BRICKS, OuranicWallBlock::new)
    );

    public static final Block OURANIC_PILLAR = register(
            "ouranic_pillar",
            new OuranicPillarBlock(BlockBehaviour.Properties.ofFullCopy(OURANIC_CHIP_BLOCK))
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
    public static final Block CELESTIAL_OPAL_STAIRS = register(
            "celestial_opal_stairs",
            stairsOf(CELESTIAL_OPAL_BLOCK, GemstoneStairBlock::new)
    );
    public static final Block CELESTIAL_OPAL_SLAB = register(
            "celestial_opal_slab",
            slabOf(CELESTIAL_OPAL_BLOCK, GemstoneSlabBlock::new)
    );
    public static final Block CELESTIAL_OPAL_WALL = register(
            "celestial_opal_wall",
            wallOf(CELESTIAL_OPAL_BLOCK, GemstoneWallBlock::new)
    );

    public static final Block BUDDING_CELESTIAL_OPAL = register(
            "budding_celestial_opal",
            new BuddingCelestialOpalBlock(legacyCopy(CELESTIAL_OPAL_BLOCK)
                    .pushReaction(DESTROY)
                    .randomTicks()
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
                    legacyCopy(CELESTIAL_OPAL_SPIRE)
            )
    );
    public static final Block LARGE_CELESTIAL_OPAL_BUD = register(
            "large_celestial_opal_bud",
            new CelestialOpalBudBlock(
                    5.0F, 3.0F, legacyCopy(CELESTIAL_OPAL_CLUSTER)
                    .sound(SoundType.MEDIUM_AMETHYST_BUD) // same as in vanilla - for some reason large buds have medium sounds?
                    .lightLevel(constant(8))
            )
    );
    public static final Block MEDIUM_CELESTIAL_OPAL_BUD = register(
            "medium_celestial_opal_bud",
            new CelestialOpalBudBlock(
                    4.0F, 3.0F, legacyCopy(CELESTIAL_OPAL_CLUSTER)
                    .sound(SoundType.LARGE_AMETHYST_BUD) // same as in vanilla - for some reason medium buds have large sounds?
                    .lightLevel(constant(5))
            )
    );
    public static final Block SMALL_CELESTIAL_OPAL_BUD = register(
            "small_celestial_opal_bud",
            new CelestialOpalBudBlock(
                    3.0F, 4.0F, legacyCopy(CELESTIAL_OPAL_CLUSTER)
                    .sound(SoundType.SMALL_AMETHYST_BUD)
                    .lightLevel(constant(2))
            )
    );

    public static final Block POLISHED_CELESTIAL_OPAL_BLOCK = register(
            "polished_celestial_opal_block",
            new AmethystBlock(legacyCopy(CELESTIAL_OPAL_BLOCK)
                    .strength(2.5F, 4.0F)
            )
    );
    public static final Block POLISHED_CELESTIAL_OPAL_STAIRS = register(
            "polished_celestial_opal_stairs",
            stairsOf(POLISHED_CELESTIAL_OPAL_BLOCK, GemstoneStairBlock::new)
    );
    public static final Block POLISHED_CELESTIAL_OPAL_SLAB = register(
            "polished_celestial_opal_slab",
            slabOf(POLISHED_CELESTIAL_OPAL_BLOCK, GemstoneSlabBlock::new)
    );

    public static final Block POLISHED_CELESTIAL_OPAL_BRICKS = register(
            "polished_celestial_opal_bricks",
            new AmethystBlock(legacyCopy(POLISHED_CELESTIAL_OPAL_BLOCK))
    );
    public static final Block POLISHED_CELESTIAL_OPAL_BRICK_STAIRS = register(
            "polished_celestial_opal_brick_stairs",
            stairsOf(POLISHED_CELESTIAL_OPAL_BRICKS, GemstoneStairBlock::new)
    );
    public static final Block POLISHED_CELESTIAL_OPAL_BRICK_SLAB = register(
            "polished_celestial_opal_brick_slab",
            slabOf(POLISHED_CELESTIAL_OPAL_BRICKS, GemstoneSlabBlock::new)
    );
    public static final Block POLISHED_CELESTIAL_OPAL_BRICK_WALL = register(
            "polished_celestial_opal_brick_wall",
            wallOf(POLISHED_CELESTIAL_OPAL_BRICKS, GemstoneWallBlock::new)
    );

    public static final Block POLISHED_CELESTIAL_OPAL_PILLAR = register(
            "polished_celestial_opal_pillar",
            new GemstoneRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_CELESTIAL_OPAL_BLOCK))
    );

    public static final Block BUBBLEGLOOM = register(
            "bubblegloom",
            new Block(properties()
                    .strength(2.0F)
                    .mapColor(COLOR_PURPLE)
                    .sound(AMETHYST)
                    .lightLevel(constant(11))
                    .requiresCorrectToolForDrops()
            )
    );
    public static final Block BUBBLEGLOOM_STAIRS = register(
            "bubblegloom_stairs",
            stairsOf(BUBBLEGLOOM)
    );
    public static final Block BUBBLEGLOOM_SLAB = register(
            "bubblegloom_slab",
            slabOf(BUBBLEGLOOM)
    );
    public static final Block BUBBLEGLOOM_WALL = register(
            "bubblegloom_wall",
            wallOf(BUBBLEGLOOM)
    );

    public static final Block POLISHED_BUBBLEGLOOM = register(
            "polished_bubblegloom",
            new Block(BlockBehaviour.Properties.ofFullCopy(BUBBLEGLOOM).strength(2.3F))
    );
    public static final Block POLISHED_BUBBLEGLOOM_STAIRS = register(
            "polished_bubblegloom_stairs",
            stairsOf(POLISHED_BUBBLEGLOOM)
    );
    public static final Block POLISHED_BUBBLEGLOOM_SLAB = register(
            "polished_bubblegloom_slab",
            slabOf(POLISHED_BUBBLEGLOOM)
    );
    public static final Block POLISHED_BUBBLEGLOOM_WALL = register(
            "polished_bubblegloom_wall",
            wallOf(POLISHED_BUBBLEGLOOM)
    );

    public static final Block CUT_POLISHED_BUBBLEGLOOM = register(
            "cut_polished_bubblegloom",
            new Block(BlockBehaviour.Properties.ofFullCopy(POLISHED_BUBBLEGLOOM))
    );
    public static final Block CUT_POLISHED_BUBBLEGLOOM_STAIRS = register(
            "cut_polished_bubblegloom_stairs",
            stairsOf(CUT_POLISHED_BUBBLEGLOOM)
    );
    public static final Block CUT_POLISHED_BUBBLEGLOOM_SLAB = register(
            "cut_polished_bubblegloom_slab",
            slabOf(CUT_POLISHED_BUBBLEGLOOM)
    );
    public static final Block CUT_POLISHED_BUBBLEGLOOM_WALL = register(
            "cut_polished_bubblegloom_wall",
            wallOf(CUT_POLISHED_BUBBLEGLOOM)
    );

    public static final Block POLISHED_BUBBLEGLOOM_PILLAR = register(
            "polished_bubblegloom_pillar",
            new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_BUBBLEGLOOM))
    );

    public static final Block MUCKY_SINGUT_COIL = register(
            "mucky_singut_coil",
            new RotatedPillarBlock(properties()
                    .mapColor(COLOR_PURPLE)
                    .strength(1.3F)
                    .sound(SLIME_BLOCK)
            )
    );

    public static final Block MUCKY_SINGUT_BLOCK = register(
            "mucky_singut_block",
            new Block(BlockBehaviour.Properties.ofFullCopy(MUCKY_SINGUT_COIL))
    );

    public static final Block CLEANSED_SINGUT_COIL = register(
            "cleansed_singut_coil",
            new RotatedPillarBlock(properties()
                    .mapColor(TERRACOTTA_WHITE)
                    .strength(1.1F)
                    .sound(SLIME_BLOCK)
            )
    );

    public static final Block CLEANSED_SINGUT_BLOCK = register(
            "cleansed_singut_block",
            new Block(BlockBehaviour.Properties.ofFullCopy(CLEANSED_SINGUT_COIL))
    );

    private static <T extends Block> T register(String id, T block) {
        return register(OperationStarcleave.id(id), block);
    }

    private static <T extends Block> T register(ResourceLocation location, T block) {
        // manually set drops to avoid isseus with dropsLike()
        BlockBehaviourAccessor bbpa = (BlockBehaviourAccessor) block;
        if (bbpa.operation_starcleave$getDrops() == null) {
            // if another block uses dropsLike() to copy this block's drops, then it will trigger getDrops() on this block, which sets this block's drops to air since it is not yet registered
            // we avoid this by just manually setting the drops immediately
            bbpa.operation_starcleave$setDrops(ResourceKey.create(Registries.LOOT_TABLE, location.withPrefix("blocks/")));
        }

        UNREGISTERED_BLOCKS.put(location, block);
        UNREGISTERED_BLOCK_LOCATIONS.put(block, location);
        return block;
    }

    public static void init(BiConsumer<ResourceLocation, Block> r) {
        UNREGISTERED_BLOCKS.forEach(r);
        UNREGISTERED_BLOCKS.clear();
        UNREGISTERED_BLOCK_LOCATIONS.clear();
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

    protected static BlockBehaviour.Properties legacyCopy(BlockBehaviour settings) {
        return BlockBehaviour.Properties.ofLegacyCopy(settings);
    }

    protected static <T extends StairBlock> T stairsOf(Block block, BiFunction<BlockState, BlockBehaviour.Properties, T> constructor) {
        return constructor.apply(block.defaultBlockState(), legacyCopy(block));
    }

    protected static StairBlock stairsOf(Block block) {
        return stairsOf(block, CustomStairBlock::new);
    }

    protected static <T extends SlabBlock> T slabOf(BlockBehaviour block, Function<BlockBehaviour.Properties, T> constructor) {
        return constructor.apply(legacyCopy(block));
    }

    protected static SlabBlock slabOf(BlockBehaviour block) {
        return slabOf(block, SlabBlock::new);
    }

    protected static <T extends WallBlock> T wallOf(BlockBehaviour block, Function<BlockBehaviour.Properties, T> constructor) {
        return constructor.apply(legacyCopy(block).forceSolidOn());
    }

    protected static WallBlock wallOf(BlockBehaviour block) {
        return wallOf(block, WallBlock::new);
    }

    protected static FenceBlock fenceOf(BlockBehaviour block) {
        return new FenceBlock(legacyCopy(block));
    }

    protected static FenceGateBlock fenceGateOf(WoodType woodType, BlockBehaviour block) {
        return new FenceGateBlock(woodType, legacyCopy(block).forceSolidOn());
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
        return new CustomDoorBlock(blockSetType, legacyCopy(block)
                .strength(destroyTime, explosionResistance)
                .pushReaction(PushReaction.DESTROY)
                .noOcclusion()
        );
    }

    protected static TrapDoorBlock trapdoorOf(BlockSetType blockSetType, BlockBehaviour block, float destroyTime, float explosionResistance) {
        return new CustomTrapDoorBlock(blockSetType, legacyCopy(block)
                .strength(destroyTime, explosionResistance)
                .isValidSpawn(SPAWN_NEVER)
                .noOcclusion()
        );
    }

    protected static Block starbleachedFlower(Holder<MobEffect> effect, float seconds, MapColor color, int lightLevel) {
        return starbleachedFlower(new SuspiciousStewEffects(List.of(
                new SuspiciousStewEffects.Entry(effect, Mth.floor(seconds * 20.0F))
        )), color, lightLevel);
    }

    protected static Block starbleachedFlower(Holder<MobEffect> effect1, float seconds1, Holder<MobEffect> effect2, float seconds2, MapColor color, int lightLevel) {
        return starbleachedFlower(new SuspiciousStewEffects(List.of(
                new SuspiciousStewEffects.Entry(effect1, Mth.floor(seconds1 * 20.0F)),
                new SuspiciousStewEffects.Entry(effect2, Mth.floor(seconds2 * 20.0F))
        )), color, lightLevel);
    }

    protected static Block starbleachedFlower(SuspiciousStewEffects effects, MapColor color, int lightLevel) {
        return new StarbleachedFlowerBlock(
                effects,
                properties()
                        .mapColor(color)
                        .sound(SoundType.GRASS)
                        .offsetType(BlockBehaviour.OffsetType.XZ)
                        .pushReaction(PushReaction.DESTROY)
                        .lightLevel(constant(lightLevel))
                        .noCollission()
                        .instabreak()
        );
    }
}
