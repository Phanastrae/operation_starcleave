package phanastrae.operation_starcleave.world.starbleach;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import org.jetbrains.annotations.Nullable;
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

    public static void init() {
        addConversion(StateMatchesPredicate.fromBlocks(PODZOL, MYCELIUM), STELLAR_MULCH);
        addConversion(GRASS_BLOCK, StarbleachConversions::getGrassySedimentState);
        addConversion(
                new StateMatchesPredicate.Builder(DIRT, COARSE_DIRT, ROOTED_DIRT, END_STONE).addBlockTag(BlockTags.BASE_STONE_OVERWORLD).build(),
                StarbleachConversions::getSedimentState
        );
        addConversion(StateMatchesPredicate.fromBlocks(NETHERRACK, SOUL_SAND, SOUL_SOIL, CRIMSON_NYLIUM, WARPED_NYLIUM), AIR);
        addConversion(
                BlockTags.SAND,
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
        addConversion(BlockTags.LOGS, StarbleachConversions::getLogState);
        addConversion(FARMLAND, StarbleachConversions::getFarmlandState);
        addConversion(DIRT_PATH, STELLAR_PATH);

        addConversion(AMETHYST_BLOCK, CELESTIAL_OPAL_BLOCK);
        addConversion(BUDDING_AMETHYST, BUDDING_CELESTIAL_OPAL);
        addCopyPropertiesConversion(SMALL_AMETHYST_BUD, SMALL_CELESTIAL_OPAL_BUD);
        addCopyPropertiesConversion(MEDIUM_AMETHYST_BUD, MEDIUM_CELESTIAL_OPAL_BUD);
        addCopyPropertiesConversion(LARGE_AMETHYST_BUD, LARGE_CELESTIAL_OPAL_BUD);
        addCopyPropertiesConversion(AMETHYST_CLUSTER, CELESTIAL_OPAL_CLUSTER);

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

    // conventional stripped woods tag
    public static final TagKey<Block> STRIPPED_WOODS = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "stripped_woods"));

    public static boolean isWoodNotLog(BlockState state) {
        // there is no tag for woods vs logs, but there is a tag for stripped_woods vs stripped_logs, so try stripping the block and check that instead
        // if block is not strippable, treat it like a log
        Block block = state.getBlock();
        Map<Block, Block> strippables = AxeItemAccessor.getSTRIPPABLES();
        if (strippables.containsKey(block)) {
            Block stripped = strippables.get(block);
            return stripped.defaultBlockState().is(STRIPPED_WOODS);
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
