package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class PetrichoricPlasmaBlock extends AbstractPetrichoricBlock {
    public static final MapCodec<PetrichoricPlasmaBlock> CODEC = simpleCodec(PetrichoricPlasmaBlock::new);
    public static final Direction[] DIRECTIONS_EXCEPT_UP = new Direction[]{
            Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.DOWN
    };

    @Override
    protected MapCodec<? extends PetrichoricPlasmaBlock> codec() {
        return CODEC;
    }

    public PetrichoricPlasmaBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean notify) {
        level.scheduleTick(pos, this, getDelay(level.getRandom(), level, pos, oldState));
        super.onPlace(state, level, pos, oldState, notify);
    }

    @Override
    public BlockState updateShape(
            BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos
    ) {
        level.scheduleTick(pos, this, getDelay(level.getRandom(), level, pos, state));
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        absorbWater(level, pos, random);

        if(nextToGap(pos, level)) {
            level.setBlockAndUpdate(pos, OperationStarcleaveBlocks.COAGULATED_PLASMA.defaultBlockState());
        }
    }

    public static boolean nextToGap(BlockPos pos, Level level) {
        for(Direction direction : DIRECTIONS_EXCEPT_UP) {
            BlockPos adjPos = pos.relative(direction);
            BlockState adjState = level.getBlockState(adjPos);

            if(!adjState.is(OperationStarcleaveBlocks.PETRICHORIC_PLASMA) && !adjState.isFaceSturdy(level, adjPos, direction)) {
                return true;
            }
        }
        return false;
    }

    public static int getDelay(RandomSource random, LevelAccessor level, BlockPos pos, BlockState state) {
        return 2 + random.nextInt(3);
    }
}
