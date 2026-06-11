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
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
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
                new StateMatchesPredicate.Builder(GRAVEL).addBlockTag(BlockTags.SAND).build(),
                STARDUST_BLOCK
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
        addClusterConversion(SMALL_AMETHYST_BUD, SMALL_CELESTIAL_OPAL_BUD);
        addClusterConversion(MEDIUM_AMETHYST_BUD, MEDIUM_CELESTIAL_OPAL_BUD);
        addClusterConversion(LARGE_AMETHYST_BUD, LARGE_CELESTIAL_OPAL_BUD);
        addClusterConversion(AMETHYST_CLUSTER, CELESTIAL_OPAL_CLUSTER);
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

    private static void addClusterConversion(Block input, Block result) {
        addConversion(
                input,
                (l, p, s, r) -> copyProperties(result, s, BlockStateProperties.WATERLOGGED, BlockStateProperties.FACING)
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
        return copyProperties(newBlock, blockState, RotatedPillarBlock.AXIS);
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

    public static BlockState copyProperties(Block newBlock, BlockState oldState, Property<?>... properties) {
        return copyProperties(newBlock.defaultBlockState(), oldState, properties);
    }

    public static BlockState copyProperties(BlockState newState, BlockState oldState, Property<?>... properties) {
        for (Property<?> property : properties) {
            newState = copyProperty(newState, oldState, property);
        }

        return newState;
    }

    public static <T extends Comparable<T>> BlockState copyProperty(BlockState newState, BlockState oldState, Property<T> property) {
        if (oldState.getProperties().contains(property)) {
            newState = newState.setValue(property, oldState.getValue(property));
        }

        return newState;
    }
}
