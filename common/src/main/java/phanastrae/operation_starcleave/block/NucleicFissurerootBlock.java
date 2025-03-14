package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;

public class NucleicFissurerootBlock extends RotatedPillarBlock {
    public static final MapCodec<NucleicFissurerootBlock> CODEC = simpleCodec(NucleicFissurerootBlock::new);

    public NucleicFissurerootBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        Direction.Axis axis = state.getValue(RotatedPillarBlock.AXIS);

        Direction direction;
        switch(axis) {
            case X -> direction = random.nextBoolean() ? Direction.EAST : Direction.WEST;
            case Z -> direction = random.nextBoolean() ? Direction.NORTH : Direction.SOUTH;
            default -> direction = random.nextBoolean() ? Direction.UP : Direction.DOWN;
        }

        BlockPos adjPos = pos.relative(direction);
        BlockState adjState = level.getBlockState(adjPos);

        if(adjState.is(OperationStarcleaveBlocks.NUCLEOSYNTHESEED)) {
            NucleosyntheseedBlock.trySpread(adjState, level, adjPos, random, false);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if(random.nextInt(4) == 0) {
            for (Direction direction : Direction.values()) {
                BlockPos adjPos = pos.relative(direction);
                BlockState adjState = level.getBlockState(adjPos);
                if (!adjState.isFaceSturdy(level, pos, direction.getOpposite())) {
                    for (int i = 0; i < 1 + random.nextInt(2); i++) {
                        level.addParticle(
                                OperationStarcleaveParticleTypes.PLASMA_DUST,
                                pos.getX() + 0.5,
                                pos.getY() + 0.5,
                                pos.getZ() + 0.5,
                                random.nextGaussian() * 0.05,
                                random.nextGaussian() * 0.05,
                                random.nextGaussian() * 0.05
                        );
                    }
                }
            }
        }
    }
}
