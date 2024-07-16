package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class PetrichoricVaporBlock extends AbstractPetrichoricBlock {
    public static final MapCodec<PetrichoricPlasmaBlock> CODEC = createCodec(PetrichoricPlasmaBlock::new);

    public static final IntProperty DISTANCE = IntProperty.of("distance", 0, 3);

    @Override
    protected MapCodec<? extends PetrichoricPlasmaBlock> getCodec() {
        return CODEC;
    }

    public PetrichoricVaporBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(DISTANCE, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(DISTANCE);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        world.scheduleBlockTick(pos, this, getDelay(world.getRandom()));
        super.onBlockAdded(state, world, pos, oldState, notify);
    }

    @Override
    public BlockState getStateForNeighborUpdate(
            BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
    ) {
        world.scheduleBlockTick(pos, this, getDelay(world.getRandom()));
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public static int getDelay(Random random) {
        return 1 + random.nextInt(2);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        absorbWater(world, pos);

        int distance = getDistance(state);

        BlockPos downPos = pos.down();
        BlockState downState = world.getBlockState(downPos);
        BlockPos upPos = pos.up();
        BlockState upState = world.getBlockState(upPos);

        if(downState.isOf(OperationStarcleaveBlocks.PETRICHORIC_VAPOR)) {
            int downDistance = getDistance(downState);
            int desiredDistance = downDistance + 1;
            if (distance != desiredDistance) {
                world.setBlockState(pos, getStateForDistance(desiredDistance));
                distance = desiredDistance;
            }
        } else if(!downState.isOf(OperationStarcleaveBlocks.PETRICHORIC_PLASMA)) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }

        if ((upState.isReplaceable() && distance < 3) || canDestroy(upState)) {
            world.setBlockState(upPos, getStateForDistance(distance + 1));
        }
    }

    public static int getDistance(BlockState state) {
        if(state.contains(DISTANCE)) {
            return state.get(DISTANCE);
        } else {
            return 0;
        }
    }

    public BlockState getStateForDistance(int distance) {
        if(distance > 3) {
            return Blocks.AIR.getDefaultState();
        } else {
            if(distance < 0) distance = 0;
            return this.getDefaultState().with(DISTANCE, distance);
        }
    }
}
