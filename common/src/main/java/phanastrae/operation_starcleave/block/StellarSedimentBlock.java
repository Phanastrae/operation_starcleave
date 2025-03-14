package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class StellarSedimentBlock extends Block implements BonemealableBlock {
    public static final MapCodec<StellarSedimentBlock> CODEC = simpleCodec(StellarSedimentBlock::new);

    @Override
    public MapCodec<StellarSedimentBlock> codec() {
        return CODEC;
    }

    public StellarSedimentBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state) {
        return world.getBlockState(pos.above()).canBeReplaced();
    }

    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        BlockPos.MutableBlockPos posMutable = pos.mutable();
        for(int x = -1; x <= 1; x++) {
            for(int z = -1; z <= 1; z++) {
                posMutable.setWithOffset(pos, x, 0, z);
                int x2 = x*x;
                int z2 = z*z;
                // 100% chance of converting targeted block, 50% chance for direct neighbors, 20% for diagonal neighbors
                if(random.nextInt(1 + x2 + z2 + 2 * x2 * z2) != 0) continue;

                BlockState targetState = world.getBlockState(posMutable);
                if(targetState.is(OperationStarcleaveBlocks.STELLAR_SEDIMENT)) {
                    if(world.getBlockState(posMutable.above()).canBeReplaced()) {
                        world.setBlockAndUpdate(posMutable, OperationStarcleaveBlocks.STELLAR_MULCH.defaultBlockState());
                    }
                }
            }
        }
    }
}
