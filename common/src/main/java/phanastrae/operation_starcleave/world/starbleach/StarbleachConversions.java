package phanastrae.operation_starcleave.world.starbleach;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.block.StarbleachCauldronBlock;
import phanastrae.operation_starcleave.block.StellarFarmlandBlock;
import phanastrae.operation_starcleave.block.tag.OperationStarcleaveBlockTags;
import phanastrae.operation_starcleave.mixin.common.accessor.item.AxeItemAccessor;
import phanastrae.operation_starcleave.world.firmament.Firmament;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static net.minecraft.world.level.block.Blocks.*;
import static phanastrae.operation_starcleave.block.OperationStarcleaveBlocks.*;

public class StarbleachConversions {
    // TODO data-drive conversions
    public static final List<StateConversion> ALL_CONVERSIONS = new ArrayList<>();

    @Nullable
    public static BlockState getStarbleachResult(Level level, BlockPos blockPos, BlockState blockState, RandomSource random, Starbleach.StarbleachTarget starbleachTarget) {
        BlockState newBlockstate = null;

        if (starbleachTarget.shouldFillCauldrons()) {
            newBlockstate = getStarbleachCauldronResult(blockState);
            if (newBlockstate != null) {
                return newBlockstate;
            }
        }

        if (starbleachTarget.shouldConvertBlocks()) {
            newBlockstate = getStarbleachBlockResult(level, blockPos, blockState, random);
            if (newBlockstate != null) {
                return newBlockstate;
            }
        }

        return newBlockstate;
    }

    @Nullable
    public static BlockState getStarbleachCauldronResult(BlockState blockState) {
        if (blockState.is(CAULDRON)) {
            return STARBLEACH_CAULDRON.defaultBlockState();
        }
        if (blockState.is(STARBLEACH_CAULDRON)) {
            if (blockState.getValue(StarbleachCauldronBlock.LEVEL_7) != StarbleachCauldronBlock.MAX_STARBLEACH_LEVEL) {
                return blockState.cycle(StarbleachCauldronBlock.LEVEL_7);
            }
        }

        return null;
    }

    @Nullable
    public static BlockState getStarbleachBlockResult(Level level, BlockPos blockPos, BlockState blockState, RandomSource random) {
        if (blockState.is(OperationStarcleaveBlockTags.STARBLEACH_IMMUNE)) {
            return null;
        }

        // TODO consider optimising this, currently trying to Starbleach non-Starbleachable blocks iterates through every single conversion
        for (StateConversion conversion : ALL_CONVERSIONS) {
            BlockState state = conversion.getState(level, blockPos, blockState, random);
            if (state != null) {
                return state;
            }
        }

        return null;
    }

    @Nullable
    public static BlockState getStarbleachAttachedBlockResult(Level level, BlockPos blockPos, BlockState blockState, RandomSource random, BlockState supportState, Direction direction) {
        if (blockState.is(OperationStarcleaveBlockTags.STARBLEACH_IMMUNE)) {
            return null;
        }

        // TODO further improve this all too at some point
        if (direction == Direction.UP) {
            if (blockState.is(Blocks.SHORT_GRASS)) {
                if (supportState.is(OperationStarcleaveBlocks.HOLY_MOSS)) {
                    return OperationStarcleaveBlocks.SHORT_HOLY_MOSS.defaultBlockState();
                } else if (supportState.is(OperationStarcleaveBlocks.STELLAR_MULCH)) {
                    return OperationStarcleaveBlocks.MULCHBORNE_TUFT.defaultBlockState();
                }
            }

            if (blockState.is(BlockTags.SAPLINGS) && supportState.is(OperationStarcleaveBlockTags.STARBLEACHED_SAPLING_PLANTABLE_ON)) {
                return OperationStarcleaveBlocks.STARBLEACHED_SAPLING.defaultBlockState();
            }

            if (blockState.is(BlockTags.SMALL_FLOWERS) && supportState.is(OperationStarcleaveBlockTags.STARBLEACHED_SAPLING_PLANTABLE_ON)) {
                return getRandomSmallStarbleachedFlower(random).defaultBlockState();
            }

            if (blockState.is(PINK_PETALS) && supportState.is(OperationStarcleaveBlockTags.STARBLEACHED_SAPLING_PLANTABLE_ON)) {
                return STARCLOVERS.withPropertiesOf(blockState);
            }

            if (blockState.is(TALL_GRASS)) {
                DoubleBlockHalf half = blockState.getValue(DoublePlantBlock.HALF);
                if ((half == DoubleBlockHalf.LOWER && supportState.is(HOLY_MOSS)) || (half == DoubleBlockHalf.UPPER && supportState.is(TALL_HOLY_MOSS))) {
                    return TALL_HOLY_MOSS.withPropertiesOf(blockState);
                }
            }

            if (blockState.is(BlockTags.WOODEN_PRESSURE_PLATES)) {
                return STARTOUCHED_PRESSURE_PLATE.withPropertiesOf(blockState);
            }

            if (blockState.is(BlockTags.WOODEN_DOORS)) {
                DoubleBlockHalf half = blockState.getValue(DoorBlock.HALF);
                if (half == DoubleBlockHalf.LOWER || (half == DoubleBlockHalf.UPPER && supportState.is(STARTOUCHED_DOOR))) {
                    return STARTOUCHED_DOOR.withPropertiesOf(blockState);
                }
            }

            if (blockState.is(OperationStarcleaveBlockTags.SB_I_STARTOUCHED_TORCH)) {
                return STARTOUCHED_TORCH.withPropertiesOf(blockState);
            }

            if (blockState.is(BlockTags.TALL_FLOWERS) && !blockState.is(SUNFLOWER)) {
                DoubleBlockHalf half = blockState.getValue(DoublePlantBlock.HALF);
                if ((half == DoubleBlockHalf.LOWER && supportState.is(OperationStarcleaveBlockTags.STARBLEACHED_SAPLING_PLANTABLE_ON)) || (half == DoubleBlockHalf.UPPER && supportState.is(STARCLOVER_BUSH))) {
                    return STARCLOVER_BUSH.withPropertiesOf(blockState);
                }
            }

            if (blockState.is(SUNFLOWER)) {
                DoubleBlockHalf half = blockState.getValue(DoublePlantBlock.HALF);
                if ((half == DoubleBlockHalf.LOWER && supportState.is(OperationStarcleaveBlockTags.STARBLEACHED_SAPLING_PLANTABLE_ON)) || (half == DoubleBlockHalf.UPPER && supportState.is(STARFLOWER))) {
                    return STARFLOWER.withPropertiesOf(blockState);
                }
            }
        }

        if (supportState.is(BUDDING_CELESTIAL_OPAL)) {
            if (blockState.is(SMALL_AMETHYST_BUD)) {
                return getAttachedCrystalBlockstate(blockState, SMALL_CELESTIAL_OPAL_BUD, direction);
            } else if (blockState.is(MEDIUM_AMETHYST_BUD)) {
                return getAttachedCrystalBlockstate(blockState, MEDIUM_CELESTIAL_OPAL_BUD, direction);
            } else if (blockState.is(LARGE_AMETHYST_BUD)) {
                return getAttachedCrystalBlockstate(blockState, LARGE_CELESTIAL_OPAL_BUD, direction);
            } else if (blockState.is(AMETHYST_CLUSTER)) {
                return getAttachedCrystalBlockstate(blockState, CELESTIAL_OPAL_CLUSTER, direction);
            }
        }

        if (blockState.is(BlockTags.WOODEN_BUTTONS) && blockState.getValue(ButtonBlock.FACING) == direction) {
            return STARTOUCHED_BUTTON.withPropertiesOf(blockState);
        }

        if (blockState.is(OperationStarcleaveBlockTags.SB_I_STARTOUCHED_WALL_TORCH) && blockState.getValue(WallTorchBlock.FACING) == direction) {
            return STARTOUCHED_WALL_TORCH.withPropertiesOf(blockState);
        }

        return null;
    }

    public static Block getRandomSmallStarbleachedFlower(RandomSource random) {
        return switch (random.nextInt(7)) {
            case 0 -> GREAT_TREES_CARE;
            case 1 -> RED_MOURNER;
            case 2 -> ANGELCLAW;
            case 4 -> WITCHGLARE;
            case 5 -> BLUE_DREAMER;
            case 6 -> DRAGONS_MAW;
            default -> ELDROSE;
        };
    }

    @Nullable
    public static BlockState getAttachedCrystalBlockstate(BlockState oldState, Block block, Direction direction) {
        if (oldState.getValue(AmethystClusterBlock.FACING) == direction) {
            return block.withPropertiesOf(oldState);
        } else {
            return null;
        }
    }

    public static void init() {
        addConversion(StateMatchesPredicate.fromBlocks(PODZOL, MYCELIUM), STELLAR_MULCH);
        addConversion(GRASS_BLOCK, StarbleachConversions::getGrassySedimentState);
        addConversion(
                new StateMatchesPredicate.Builder(DIRT, ROOTED_DIRT, END_STONE).addBlockTag(BlockTags.BASE_STONE_OVERWORLD).build(),
                StarbleachConversions::getSedimentState
        );
        addConversion(
                COARSE_DIRT,
                STELLARUBBLE_MIX
        );
        addConversion(StateMatchesPredicate.fromBlocks(NETHERRACK, SOUL_SAND, SOUL_SOIL, CRIMSON_NYLIUM, WARPED_NYLIUM), AIR);
        addConversion(
                state -> state.is(BlockTags.SAND) && !state.is(SUSPICIOUS_SAND),
                STARDUST_BLOCK
        );
        addConversion(
                GRAVEL,
                ASTERUBBLE
        );
        addConversion(
                new StateMatchesPredicate.Builder(CHORUS_PLANT, CHORUS_FLOWER).addBlockTag(BlockTags.LEAVES).addBlockTag(BlockTags.WART_BLOCKS).build(),
                new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
                        .add(STARBLEACHED_LEAVES.defaultBlockState(), 1)
                        .add(AIR.defaultBlockState(), 2)
                        .build()
                )
        );
        addConversion(
                state -> state.is(BlockTags.LOGS) && !state.is(OperationStarcleaveBlockTags.STRIPPED_LOGS_AND_WOODS),
                StarbleachConversions::getLogState
        );
        addConversion(FARMLAND, StarbleachConversions::getFarmlandState);
        addConversion(DIRT_PATH, STELLAR_PATH);

        addConversion(AMETHYST_BLOCK, CELESTIAL_OPAL_BLOCK);
        addConversion(BUDDING_AMETHYST, BUDDING_CELESTIAL_OPAL);

        addConversion(OperationStarcleaveBlockTags.SB_I_FELLCRUST, StarbleachConversions::getFellcrustState);
        addConversion(OperationStarcleaveBlockTags.SB_I_FELLCRUST_STAIRS, StarbleachConversions::getFellcrustStairsState);
        addConversion(OperationStarcleaveBlockTags.SB_I_FELLCRUST_SLAB, StarbleachConversions::getFellcrustSlabState);
        addConversion(OperationStarcleaveBlockTags.SB_I_FELLCRUST_WALL, StarbleachConversions::getFellcrustWallState);
        addConversion(OperationStarcleaveBlockTags.SB_I_CHISELED_FELLCRUST, CHISELED_FELLCRUST);
        addConversion(OperationStarcleaveBlockTags.SB_I_SMOOTH_FELLCRUST, SMOOTH_FELLCRUST);
        addCopyPropertiesConversion(OperationStarcleaveBlockTags.SB_I_SMOOTH_FELLCRUST_STAIRS, SMOOTH_FELLCRUST_STAIRS);
        addCopyPropertiesConversion(OperationStarcleaveBlockTags.SB_I_SMOOTH_FELLCRUST_SLAB, SMOOTH_FELLCRUST_SLAB);
        addConversion(OperationStarcleaveBlockTags.SB_I_CUT_FELLCRUST, CUT_FELLCRUST);
        addCopyPropertiesConversion(OperationStarcleaveBlockTags.SB_I_CUT_FELLCRUST_SLAB, CUT_FELLCRUST_SLAB);

        addCopyPropertiesConversion(OperationStarcleaveBlockTags.STRIPPED_LOGS, STARTOUCHED_LOG);
        addCopyPropertiesConversion(OperationStarcleaveBlockTags.STRIPPED_WOODS, STARTOUCHED_WOOD);
        addConversion(BlockTags.PLANKS, STARTOUCHED_PLANKS);
        addCopyPropertiesConversion(BlockTags.WOODEN_STAIRS, STARTOUCHED_STAIRS);
        addCopyPropertiesConversion(BlockTags.WOODEN_SLABS, STARTOUCHED_SLAB);
        addCopyPropertiesConversion(BlockTags.WOODEN_FENCES, STARTOUCHED_FENCE);
        addCopyPropertiesConversion(BlockTags.FENCE_GATES, STARTOUCHED_FENCE_GATE);
        addCopyPropertiesConversion(BlockTags.WOODEN_TRAPDOORS, STARTOUCHED_TRAPDOOR);

        addConversion(COBBLESTONE, SKYSHELL_BLOCK);
        addCopyPropertiesConversion(COBBLESTONE_STAIRS, SKYSHELL_STAIRS);
        addCopyPropertiesConversion(COBBLESTONE_SLAB, SKYSHELL_SLAB);
        addCopyPropertiesConversion(COBBLESTONE_WALL, SKYSHELL_WALL);
    }

    private static void addConversion(StateConversion conversion) {
        ALL_CONVERSIONS.add(conversion);
    }

    private static void addConversion(Predicate<BlockState> predicate, StateConversion.ConversionStateProvider stateProvider) {
        addConversion(new StateConversion(predicate, stateProvider));
    }

    private static void addConversion(Predicate<BlockState> predicate, BlockStateProvider provider) {
        addConversion(predicate, (level, pos, state, random) -> provider.getState(random, pos));
    }

    private static void addConversion(Predicate<BlockState> predicate, Block result) {
        addConversion(
                predicate,
                SimpleStateProvider.simple(result)
        );
    }

    private static void addConversion(Block input, Block result) {
        addConversion(StateMatchesPredicate.fromBlock(input), result);
    }

    private static void addConversion(TagKey<Block> input, Block result) {
        addConversion(StateMatchesPredicate.fromBlockTag(input), result);
    }

    private static void addConversion(Block input, StateConversion.ConversionStateProvider provider) {
        addConversion(
                StateMatchesPredicate.fromBlock(input),
                provider
        );
    }

    private static void addConversion(TagKey<Block> input, StateConversion.ConversionStateProvider provider) {
        addConversion(
                StateMatchesPredicate.fromBlockTag(input),
                provider
        );
    }

    private static void addCopyPropertiesConversion(Block input, Block result) {
        addConversion(
                input,
                (l, p, s, r) -> result.withPropertiesOf(s)
        );
    }

    private static void addCopyPropertiesConversion(TagKey<Block> input, Block result) {
        addConversion(
                input,
                (l, p, s, r) -> result.withPropertiesOf(s)
        );
    }

    public static BlockState getGrassySedimentState(Level level, BlockPos blockPos, BlockState blockState, RandomSource random) {
        int steepness = 0;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockState state = level.getBlockState(blockPos.offset(direction.getStepX(), 1, direction.getStepZ()));
            if (!state.canBeReplaced()) {
                steepness += 1;
            }
        }
        if (random.nextInt(2 + steepness) >= 2) {
            return STELLAR_MULCH.defaultBlockState();
        }

        int nearbyMulch = countBlocksInArea(level, blockPos, 1, (state) -> state.is(STELLAR_MULCH));
        if (random.nextInt(1 + (9 - nearbyMulch) * (9 - nearbyMulch)) <= 2) {
            return STELLAR_MULCH.defaultBlockState();
        } else {
            return HOLY_MOSS.defaultBlockState();
        }
    }

    public static BlockState getSedimentState(Level level, BlockPos blockPos, BlockState blockState, RandomSource random) {
        if (level.getBlockState(blockPos.above()).isAir()) {
            int nearbyMulch = countBlocksInArea(level, blockPos, 1, (state) -> state.is(STELLAR_MULCH));
            if (random.nextInt(1 + (9 - nearbyMulch) * (9 - nearbyMulch)) <= 30) {
                return STELLAR_MULCH.defaultBlockState();
            } else if (random.nextInt(7) == 0) {
                return STELLARUBBLE_MIX.defaultBlockState();
            }
        }

        return STELLAR_SEDIMENT.defaultBlockState();
    }

    public static int countBlocksInArea(Level level, BlockPos blockPos, int radius, Predicate<BlockState> predicate) {
        int matches = 0;
        BlockPos.MutableBlockPos mutable = blockPos.mutable();
        for (int x = -radius; x <= radius; x++) {
            mutable.setX(x);
            for (int z = -radius; z <= radius; z++) {
                mutable.setZ(z);
                BlockState state = level.getBlockState(mutable);
                if (predicate.test(state)) {
                    matches++;
                }
            }
        }
        return matches;
    }

    public static boolean isWoodNotLog(BlockState state) {
        // there is no tag for woods vs logs, but there is a tag for stripped_woods vs stripped_logs, so try stripping the block and check that instead
        // if block is not strippable, treat it like a log
        Block block = state.getBlock();
        Map<Block, Block> strippables = AxeItemAccessor.getSTRIPPABLES();
        if (strippables.containsKey(block)) {
            Block stripped = strippables.get(block);
            return stripped.defaultBlockState().is(OperationStarcleaveBlockTags.STRIPPED_WOODS);
        }

        return false;
    }

    public static BlockState getLogState(Level level, BlockPos blockPos, BlockState blockState, RandomSource random) {
        Block newBlock = isWoodNotLog(blockState) ? STARBLEACHED_WOOD : STARBLEACHED_LOG;
        return newBlock.withPropertiesOf(blockState);
    }

    public static BlockState getFarmlandState(Level level, BlockPos blockPos, BlockState blockState, RandomSource random) {
        Firmament firmament = Firmament.fromLevel(level);
        BlockState farmland = STELLAR_FARMLAND.defaultBlockState();
        if (firmament != null && StellarFarmlandBlock.isStarlit(level, blockPos, firmament)) {
            return farmland.setValue(FarmBlock.MOISTURE, 7);
        } else {
            return farmland;
        }
    }

    public static boolean hasFellcrustAbove(Level level, BlockPos blockPos, boolean includeWalls) {
        BlockState upState = level.getBlockState(blockPos.above());
        return upState.is(OperationStarcleaveBlockTags.SB_I_FELLCRUST) || upState.is(FELLCRUST) || upState.is(COBBLED_FELLCRUST)
                || ((upState.is(OperationStarcleaveBlockTags.SB_I_FELLCRUST_SLAB) || upState.is(FELLCRUST_SLAB) || upState.is(COBBLED_FELLCRUST_SLAB)) && upState.getValue(SlabBlock.TYPE) != SlabType.TOP)
                || ((upState.is(OperationStarcleaveBlockTags.SB_I_FELLCRUST_STAIRS) || upState.is(FELLCRUST_STAIRS) || upState.is(COBBLED_FELLCRUST_STAIRS)) && upState.getValue(StairBlock.HALF) == Half.BOTTOM)
                || (includeWalls && (upState.is(OperationStarcleaveBlockTags.SB_I_FELLCRUST_WALL) || upState.is(FELLCRUST_WALL) || upState.is(COBBLED_FELLCRUST_WALL)));
    }

    public static BlockState getFellcrustState(Level level, BlockPos blockPos, BlockState blockState, RandomSource random) {
        Block newBlock = hasFellcrustAbove(level, blockPos, false) ? COBBLED_FELLCRUST : FELLCRUST;
        return newBlock.defaultBlockState();
    }

    public static BlockState getFellcrustSlabState(Level level, BlockPos blockPos, BlockState blockState, RandomSource random) {
        Block newBlock = (blockState.hasProperty(SlabBlock.TYPE) && blockState.getValue(SlabBlock.TYPE) == SlabType.BOTTOM) || !hasFellcrustAbove(level, blockPos, false) ? FELLCRUST_SLAB : COBBLED_FELLCRUST_SLAB;
        return newBlock.withPropertiesOf(blockState);
    }

    public static BlockState getFellcrustStairsState(Level level, BlockPos blockPos, BlockState blockState, RandomSource random) {
        Block newBlock = (blockState.hasProperty(StairBlock.HALF) && blockState.getValue(StairBlock.HALF) == Half.BOTTOM) || !hasFellcrustAbove(level, blockPos, false) ? FELLCRUST_STAIRS : COBBLED_FELLCRUST_STAIRS;
        return newBlock.withPropertiesOf(blockState);
    }

    public static BlockState getFellcrustWallState(Level level, BlockPos blockPos, BlockState blockState, RandomSource random) {
        Block newBlock = hasFellcrustAbove(level, blockPos, true) ? COBBLED_FELLCRUST_WALL : FELLCRUST_WALL;
        return newBlock.withPropertiesOf(blockState);
    }
}
