package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;
import phanastrae.operation_starcleave.world.starbleach.Starbleach;

public class StellarMulchBlock extends StellarSedimentBlock{
    public static final MapCodec<StellarSedimentBlock> CODEC = simpleCodec(StellarMulchBlock::new);

    @Override
    public MapCodec<StellarSedimentBlock> codec() {
        return CODEC;
    }

    public StellarMulchBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    private static boolean stayAlive(BlockState state, LevelReader world, BlockPos pos) {
        BlockPos blockPos = pos.above();
        BlockState blockState = world.getBlockState(blockPos);
        int i = LightEngine.getLightBlockInto(world, state, pos, blockState, blockPos, Direction.UP, blockState.getLightBlock(world, blockPos));
        return i < world.getMaxLightLevel();
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (!stayAlive(state, world, pos)) {
            world.setBlockAndUpdate(pos, OperationStarcleaveBlocks.STELLAR_SEDIMENT.defaultBlockState());
        }
    }

    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        BlockPos.MutableBlockPos posMutable = pos.mutable();
        for(int i = 0; i < 30; i++) {
            posMutable.setWithOffset(pos, random.nextInt(5) - 2, random.nextInt(3) - 1, random.nextInt(5) - 2);
            BlockState targetState = world.getBlockState(posMutable);
            if(targetState.is(OperationStarcleaveBlocks.STELLAR_SEDIMENT)) {
                if(world.getBlockState(posMutable.above()).isAir()) {
                    world.setBlockAndUpdate(posMutable, OperationStarcleaveBlocks.STELLAR_MULCH.defaultBlockState());
                }
            } else if(targetState.canBeReplaced()) {
                Starbleach.decorate(world, posMutable, 18, OperationStarcleaveBlocks.STELLAR_MULCH, OperationStarcleaveBlocks.MULCHBORNE_TUFT);
            }
        }
    }
}
