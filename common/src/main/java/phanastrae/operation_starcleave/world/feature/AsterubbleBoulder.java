package phanastrae.operation_starcleave.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import phanastrae.operation_starcleave.block.tag.OperationStarcleaveBlockTags;

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
                }
            }

            centerPos = centerPos.offset(-1 + random.nextInt(2), -random.nextInt(2), -1 + random.nextInt(2));
        }

        return true;
    }

    public boolean canReplace(BlockState state) {
        return state.canBeReplaced() || state.is(OperationStarcleaveBlockTags.ASTERUBBLE_BOULDER_REPLACEABLE);
    }
}
