package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.NetherConfiguredFeatures;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.world.starbleach.Starbleach;

public class HolyMossBlock extends Block implements Fertilizable {
    public static final MapCodec<HolyMossBlock> CODEC = createCodec(HolyMossBlock::new);

    @Override
    public MapCodec<HolyMossBlock> getCodec() {
        return CODEC;
    }

    public HolyMossBlock(AbstractBlock.Settings settings) {
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
        for(int i = 0; i < 30; i++) {
            posMutable.set(pos.add(random.nextInt(5) - 2, random.nextInt(3) - 1, random.nextInt(5) - 2));
            BlockState state1 = world.getBlockState(posMutable);
            if(state1.isOf(OperationStarcleaveBlocks.STELLAR_SEDIMENT)) {
                if(world.getBlockState(posMutable.up()).isReplaceable()) {
                    world.setBlockState(posMutable, OperationStarcleaveBlocks.HOLY_MOSS.getDefaultState());
                }
            } else if(state1.isReplaceable()) {
                Starbleach.decorate(world, posMutable, 9);
            }
        }
    }
}
