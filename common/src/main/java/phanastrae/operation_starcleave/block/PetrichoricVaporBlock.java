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
    public static final MapCodec<PetrichoricVaporBlock> CODEC = simpleCodec(PetrichoricVaporBlock::new);
    public static final IntegerProperty DISTANCE = IntegerProperty.create("distance", 0, 3);

    @Override
    protected MapCodec<? extends PetrichoricVaporBlock> codec() {
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
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean notify) {
        level.scheduleTick(pos, this, getDelay(level.getRandom()));
        super.onPlace(state, level, pos, oldState, notify);
    }

    @Override
    public BlockState updateShape(
            BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos
    ) {
        level.scheduleTick(pos, this, getDelay(level.getRandom()));
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    public static int getDelay(RandomSource random) {
        return 1 + random.nextInt(2);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        absorbWater(level, pos, random);

        int distance = getDistance(state);

        BlockPos downPos = pos.below();
        BlockState downState = level.getBlockState(downPos);

        if(downState.is(OperationStarcleaveBlocks.PETRICHORIC_VAPOR)) {
            // if block below is vapor, set this vapor's distance to other vapor's distance + 1
            int downDistance = getDistance(downState);
            int desiredDistance = downDistance + 1;
            if (distance != desiredDistance) {
                level.setBlockAndUpdate(pos, getStateForDistance(desiredDistance));
            }
        } else {
            // if block below is neither vapor nor plasma, remove this vapor
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
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
