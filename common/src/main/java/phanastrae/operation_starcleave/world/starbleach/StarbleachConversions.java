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
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.block.StarbleachCauldronBlock;
import phanastrae.operation_starcleave.block.StellarFarmlandBlock;
import phanastrae.operation_starcleave.block.tag.OperationStarcleaveBlockTags;
import phanastrae.operation_starcleave.mixin.common.accessor.item.AxeItemAccessor;
import phanastrae.operation_starcleave.world.firmament.Firmament;

import java.util.Map;
import java.util.function.Predicate;

import static net.minecraft.world.level.block.Blocks.*;
import static phanastrae.operation_starcleave.block.OperationStarcleaveBlocks.*;

public class StarbleachConversions {
    // TODO data-drive conversions
    public static final StateConversion[] ALL_CONVERSIONS = setupConversions();

    public static StateConversion[] setupConversions() {
        StateConversion stellarMulchConversion = new StateConversion(
                StateMatchesPredicate.fromBlocks(PODZOL, MYCELIUM),
                SimpleStateProvider.simple(STELLAR_MULCH)
        );
        StateConversion grassySedimentConversion = new StateConversion(
                StateMatchesPredicate.fromBlock(GRASS_BLOCK),
                (l, p, s, r) -> getGrassySedimentState(l, p, r)
        );
        StateConversion sedimentConversion = new StateConversion(
                new StateMatchesPredicate.Builder(DIRT, COARSE_DIRT, ROOTED_DIRT, END_STONE).addBlockTag(BlockTags.BASE_STONE_OVERWORLD).build(),
                (l, p, s, r) -> getSedimentState(l, p, r)
        );
        StateConversion airFromNetherConversion = new StateConversion(
                StateMatchesPredicate.fromBlocks(NETHERRACK, SOUL_SAND, SOUL_SOIL, CRIMSON_NYLIUM, WARPED_NYLIUM),
                SimpleStateProvider.simple(AIR)
        );
        StateConversion stardustConversion = new StateConversion(
                new StateMatchesPredicate.Builder(GRAVEL).addBlockTag(BlockTags.SAND).build(),
                SimpleStateProvider.simple(STARDUST_BLOCK)
        );
        StateConversion leavesConversion = new StateConversion(
                new StateMatchesPredicate.Builder(CHORUS_PLANT, CHORUS_FLOWER).addBlockTag(BlockTags.LEAVES).addBlockTag(BlockTags.WART_BLOCKS).build(),
                new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
                        .add(STARBLEACHED_LEAVES.defaultBlockState(), 1)
                        .add(AIR.defaultBlockState(), 2)
                        .build()
                )
        );
        StateConversion logsConversion = new StateConversion(
                StateMatchesPredicate.fromBlockTag(BlockTags.LOGS),
                (l, p, s, r) -> getLogState(s)
        );
        StateConversion farmlandConversion = new StateConversion(
                StateMatchesPredicate.fromBlock(FARMLAND),
                (l, p, s, r) -> getFarmlandState(l, p)
        );
        StateConversion pathConversion = new StateConversion(
                StateMatchesPredicate.fromBlock(DIRT_PATH),
                SimpleStateProvider.simple(STELLAR_PATH)
        );
        StateConversion opalBlockConversion = new StateConversion(
                StateMatchesPredicate.fromBlock(AMETHYST_BLOCK),
                SimpleStateProvider.simple(CELESTIAL_OPAL_BLOCK)
        );
        StateConversion opalBuddingConversion = new StateConversion(
                StateMatchesPredicate.fromBlock(BUDDING_AMETHYST),
                SimpleStateProvider.simple(BUDDING_CELESTIAL_OPAL)
        );
        StateConversion opalSmallConversion = new StateConversion(
                StateMatchesPredicate.fromBlock(SMALL_AMETHYST_BUD),
                (l, p, s, r) -> getClusterState(s, SMALL_CELESTIAL_OPAL_BUD)
        );
        StateConversion opalMediumConversion = new StateConversion(
                StateMatchesPredicate.fromBlock(MEDIUM_AMETHYST_BUD),
                (l, p, s, r) -> getClusterState(s, MEDIUM_CELESTIAL_OPAL_BUD)
        );
        StateConversion opalLargeConversion = new StateConversion(
                StateMatchesPredicate.fromBlock(LARGE_AMETHYST_BUD),
                (l, p, s, r) -> getClusterState(s, LARGE_CELESTIAL_OPAL_BUD)
        );
        StateConversion opalClusterConversion = new StateConversion(
                StateMatchesPredicate.fromBlock(AMETHYST_CLUSTER),
                (l, p, s, r) -> getClusterState(s, CELESTIAL_OPAL_CLUSTER)
        );

        return new StateConversion[]{
                stellarMulchConversion,
                grassySedimentConversion,
                sedimentConversion,
                airFromNetherConversion,
                stardustConversion,
                leavesConversion,
                logsConversion,
                farmlandConversion,
                pathConversion,
                opalBlockConversion,
                opalBuddingConversion,
                opalSmallConversion,
                opalMediumConversion,
                opalLargeConversion,
                opalClusterConversion
        };
    }

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

    public static BlockState getGrassySedimentState(Level level, BlockPos blockPos, RandomSource random) {
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

    public static BlockState getSedimentState(Level level, BlockPos blockPos, RandomSource random) {
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

    public static BlockState getLogState(BlockState blockState) {
        // get either log or wood block
        boolean isWoodNotLog = isWoodNotLog(blockState);
        Block newBlock = isWoodNotLog ? STARBLEACHED_WOOD : STARBLEACHED_LOG;
        BlockState newState = newBlock.defaultBlockState();

        // try to preserve axis
        if (blockState.getProperties().contains(RotatedPillarBlock.AXIS)) {
            newState = newState.setValue(RotatedPillarBlock.AXIS, blockState.getValue(RotatedPillarBlock.AXIS));
        }
        return newState;
    }

    public static BlockState getFarmlandState(Level level, BlockPos blockPos) {
        Firmament firmament = Firmament.fromLevel(level);
        BlockState farmland = STELLAR_FARMLAND.defaultBlockState();
        if (firmament != null && StellarFarmlandBlock.isStarlit(level, blockPos, firmament)) {
            return farmland.setValue(FarmBlock.MOISTURE, 7);
        } else {
            return farmland;
        }
    }

    public static BlockState getClusterState(BlockState blockState, Block newBlock) {
        BlockState newState = newBlock.defaultBlockState();
        // try to preserve waterlogged and facing
        if (blockState.getProperties().contains(BlockStateProperties.WATERLOGGED)) {
            newState = newState.setValue(BlockStateProperties.WATERLOGGED, blockState.getValue(BlockStateProperties.WATERLOGGED));
        }
        if (blockState.getProperties().contains(BlockStateProperties.FACING)) {
            newState = newState.setValue(BlockStateProperties.FACING, blockState.getValue(BlockStateProperties.FACING));
        }
        return newState;
    }
}
