package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import phanastrae.operation_starcleave.world.starbleach.Starbleach;

public class StellarMulchBlock extends StellarSedimentBlock{
    public static final MapCodec<StellarSedimentBlock> CODEC = createCodec(StellarMulchBlock::new);

    @Override
    public MapCodec<StellarSedimentBlock> getCodec() {
        return CODEC;
    }

    public StellarMulchBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    private static boolean stayAlive(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.up();
        BlockState blockState = world.getBlockState(blockPos);
        int i = ChunkLightProvider.getRealisticOpacity(world, state, pos, blockState, blockPos, Direction.UP, blockState.getOpacity(world, blockPos));
        return i < world.getMaxLightLevel();
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!stayAlive(state, world, pos)) {
            world.setBlockState(pos, OperationStarcleaveBlocks.STELLAR_SEDIMENT.getDefaultState());
        }
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        BlockPos.Mutable posMutable = pos.mutableCopy();
        for(int i = 0; i < 30; i++) {
            posMutable.set(pos, random.nextInt(5) - 2, random.nextInt(3) - 1, random.nextInt(5) - 2);
            BlockState targetState = world.getBlockState(posMutable);
            if(targetState.isOf(OperationStarcleaveBlocks.STELLAR_SEDIMENT)) {
                if(world.getBlockState(posMutable.up()).isAir()) {
                    world.setBlockState(posMutable, OperationStarcleaveBlocks.STELLAR_MULCH.getDefaultState());
                }
            } else if(targetState.isReplaceable()) {
                Starbleach.decorate(world, posMutable, 18, OperationStarcleaveBlocks.STELLAR_MULCH, OperationStarcleaveBlocks.MULCHBORNE_TUFT);
            }
        }
    }
}
