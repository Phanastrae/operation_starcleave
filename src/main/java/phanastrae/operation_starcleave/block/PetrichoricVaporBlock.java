package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class PetrichoricVaporBlock extends AbstractPetrichoricBlock {
    public static final MapCodec<PetrichoricPlasmaBlock> CODEC = simpleCodec(PetrichoricPlasmaBlock::new);

    public static final IntegerProperty DISTANCE = IntegerProperty.create("distance", 0, 3);

    @Override
    protected MapCodec<? extends PetrichoricPlasmaBlock> codec() {
        return CODEC;
    }

    public PetrichoricVaporBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState().setValue(DISTANCE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DISTANCE);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        world.scheduleTick(pos, this, getDelay(world.getRandom()));
        super.onPlace(state, world, pos, oldState, notify);
    }

    @Override
    public BlockState updateShape(
            BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos
    ) {
        world.scheduleTick(pos, this, getDelay(world.getRandom()));
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    public static int getDelay(RandomSource random) {
        return 1 + random.nextInt(2);
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        absorbWater(world, pos);

        int distance = getDistance(state);

        BlockPos downPos = pos.below();
        BlockState downState = world.getBlockState(downPos);
        BlockPos upPos = pos.above();
        BlockState upState = world.getBlockState(upPos);

        if(downState.is(OperationStarcleaveBlocks.PETRICHORIC_VAPOR)) {
            int downDistance = getDistance(downState);
            int desiredDistance = downDistance + 1;
            if (distance != desiredDistance) {
                world.setBlockAndUpdate(pos, getStateForDistance(desiredDistance));
                distance = desiredDistance;
            }
        } else if(!downState.is(OperationStarcleaveBlocks.PETRICHORIC_PLASMA)) {
            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }

        if ((upState.canBeReplaced() && distance < 3) || canDestroy(upState)) {
            world.setBlockAndUpdate(upPos, getStateForDistance(distance + 1));
        }
    }

    public static int getDistance(BlockState state) {
        if(state.hasProperty(DISTANCE)) {
            return state.getValue(DISTANCE);
        } else {
            return 0;
        }
    }

    public BlockState getStateForDistance(int distance) {
        if(distance > 3) {
            return Blocks.AIR.defaultBlockState();
        } else {
            if(distance < 0) distance = 0;
            return this.defaultBlockState().setValue(DISTANCE, distance);
        }
    }
}
