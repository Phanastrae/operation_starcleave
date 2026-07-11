package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;
import phanastrae.operation_starcleave.data.worldgen.features.OperationStarcleaveConfiguredFeatures;

public class HolyMossBlock extends StellarSedimentBlock {
    public static final MapCodec<StellarSedimentBlock> CODEC = simpleCodec(StellarSedimentBlock::new);

    @Override
    public MapCodec<StellarSedimentBlock> codec() {
        return CODEC;
    }

    public HolyMossBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    private static boolean stayAlive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockPos = pos.above();
        BlockState blockState = level.getBlockState(blockPos);
        int i = LightEngine.getLightBlockInto(level, state, pos, blockState, blockPos, Direction.UP, blockState.getLightBlock(level, blockPos));
        return i < level.getMaxLightLevel();
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!stayAlive(state, level, pos)) {
            level.setBlockAndUpdate(pos, OperationStarcleaveBlocks.STELLAR_SEDIMENT.defaultBlockState());
        }
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        level.registryAccess()
                .registry(Registries.CONFIGURED_FEATURE)
                .flatMap(feature -> feature.getHolder(OperationStarcleaveConfiguredFeatures.HOLY_MOSS_PATCH_BONEMEAL))
                .ifPresent(feature -> feature.value().place(level, level.getChunkSource().getGenerator(), random, pos.above()));
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 0.5F;
    }
}
