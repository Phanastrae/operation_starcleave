package phanastrae.operation_starcleave.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import phanastrae.operation_starcleave.block.tag.OperationStarcleaveBlockTags;

public class BigSkyshell extends Feature<BlockStateConfiguration> {
    public BigSkyshell(Codec<BlockStateConfiguration> config) {
        super(config);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockStateConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockStateConfiguration config = context.config();

        BlockPos startPos = context.origin();
        if (random.nextBoolean()) {
            startPos = startPos.below();
        }
        BlockPos endPos = startPos.offset(random.nextBoolean() ? 1 : -1, 1, random.nextBoolean() ? 1 : -1);

        for (int attempt = 1; attempt <= 3; attempt++) {
            int floorHeight = startPos.getY() - 1;
            if (floorHeight < level.getMinBuildHeight() || level.getMaxBuildHeight() <= endPos.getY()) {
                // do not place outside of world height
                return false;
            }

            boolean cubeHasValidSupport = true;
            for (BlockPos pos : BlockPos.betweenClosed(startPos.offset(0, -1, 0), endPos.offset(0, -2, 0))) {
                if (!level.getBlockState(pos).isFaceSturdy(level, pos, Direction.UP)) {
                    cubeHasValidSupport = false;
                    break;
                }
            }
            if (cubeHasValidSupport) {
                for (BlockPos pos : BlockPos.betweenClosed(startPos, endPos)) {
                    if (!canReplace(level.getBlockState(pos))) {
                        // area is not clear, do not place at all
                        return false;
                    }
                }

                for (BlockPos pos : BlockPos.betweenClosed(startPos, endPos)) {
                    level.setBlock(pos, config.state, 3);
                }
                return true;
            }

            startPos = startPos.below();
            endPos = endPos.below();
        }

        return false;
    }

    public boolean canReplace(BlockState state) {
        return state.canBeReplaced() || state.is(OperationStarcleaveBlockTags.BIG_SKYSHELL_REPLACEABLE);
    }
}
