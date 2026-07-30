package phanastrae.operation_starcleave.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.block.tag.OperationStarcleaveBlockTags;

import static phanastrae.operation_starcleave.block.OperationStarcleaveBlocks.STELLARUBBLE_MIX;

public class AsterubbleBoulder extends Feature<BlockStateConfiguration> {
    public AsterubbleBoulder(Codec<BlockStateConfiguration> config) {
        super(config);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockStateConfiguration> context) {
        // this generation is similar to Forest Rocks
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockStateConfiguration config = context.config();

        BlockPos centerPos = context.origin();

        for (int i = 0; i < 3; i++) {
            int xSize = random.nextInt(2);
            int ySize = random.nextInt(2);
            int zSize = random.nextInt(2);

            // do not place outside of world height
            if (centerPos.getY() - ySize < level.getMinBuildHeight() || level.getMaxBuildHeight() <= centerPos.getY() + ySize) {
                break;
            }

            float f = (float) (xSize + ySize + zSize) * 0.333F + 0.5F;
            for (BlockPos pos : BlockPos.betweenClosed(centerPos.offset(-xSize, -ySize, -zSize), centerPos.offset(xSize, ySize, zSize))) {
                if (pos.distSqr(centerPos) <= f * f && canReplace(level.getBlockState(pos))) {
                    level.setBlock(pos, config.state, 3);

                    for (int j = 0; j < 4; j++) {
                        Direction direction = Direction.from2DDataValue(j);
                        if (random.nextFloat() < 0.6F) {
                            tryToRubbleify(level, random, pos.offset(direction.getNormal()));
                        }
                    }
                }
            }

            centerPos = centerPos.offset(-1 + random.nextInt(2), -random.nextInt(2), -1 + random.nextInt(2));
        }

        return true;
    }

    public boolean canReplace(BlockState state) {
        return state.canBeReplaced() || state.is(OperationStarcleaveBlockTags.ASTERUBBLE_BOULDER_REPLACEABLE);
    }

    public void tryToRubbleify(WorldGenLevel level, RandomSource random, BlockPos pos) {
        BlockState adjState = level.getBlockState(pos);

        if (adjState.is(Blocks.DIRT) || adjState.is(Blocks.GRASS_BLOCK) || adjState.is(Blocks.PODZOL) || adjState.is(Blocks.MYCELIUM)) {
            Block block = random.nextFloat() < 0.6F ? Blocks.DIRT : Blocks.COARSE_DIRT;
            level.setBlock(pos, block.defaultBlockState(), 3);
        } else if (adjState.is(OperationStarcleaveBlocks.STELLAR_SEDIMENT) || adjState.is(OperationStarcleaveBlocks.HOLY_MOSS) || adjState.is(OperationStarcleaveBlocks.STELLAR_MULCH)) {
            Block block = random.nextFloat() < 0.6F ? OperationStarcleaveBlocks.STELLAR_SEDIMENT : STELLARUBBLE_MIX;
            level.setBlock(pos, block.defaultBlockState(), 3);
        }
    }
}
