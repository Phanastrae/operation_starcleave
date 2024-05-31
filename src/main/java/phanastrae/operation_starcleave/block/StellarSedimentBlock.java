package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class StellarSedimentBlock extends Block implements Fertilizable {
    public static final MapCodec<StellarSedimentBlock> CODEC = createCodec(StellarSedimentBlock::new);

    @Override
    public MapCodec<StellarSedimentBlock> getCodec() {
        return CODEC;
    }

    public StellarSedimentBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return world.getBlockState(pos.up()).isReplaceable();
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        BlockPos.Mutable posMutable = pos.mutableCopy();
        for(int x = -1; x <= 1; x++) {
            for(int z = -1; z <= 1; z++) {
                posMutable.set(pos, x, 0, z);
                int x2 = x*x;
                int z2 = z*z;
                // 100% chance of converting targeted block, 50% chance for direct neighbors, 20% for diagonal neighbors
                if(random.nextInt(1 + x2 + z2 + 2 * x2 * z2) != 0) continue;

                BlockState targetState = world.getBlockState(posMutable);
                if(targetState.isOf(OperationStarcleaveBlocks.STELLAR_SEDIMENT)) {
                    if(world.getBlockState(posMutable.up()).isReplaceable()) {
                        world.setBlockState(posMutable, OperationStarcleaveBlocks.STELLAR_MULCH.getDefaultState());
                    }
                }
            }
        }
    }
}
