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

public class PetrichoricPlasmaBlock extends AbstractPetrichoricBlock {
    public static final MapCodec<PetrichoricPlasmaBlock> CODEC = simpleCodec(PetrichoricPlasmaBlock::new);

    public static final IntegerProperty IMPURITY = IntegerProperty.create("impurity", 0, 7);

    @Override
    protected MapCodec<? extends PetrichoricPlasmaBlock> codec() {
        return CODEC;
    }

    public PetrichoricPlasmaBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState().setValue(IMPURITY, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(IMPURITY);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        world.scheduleTick(pos, this, getDelay(world.getRandom(), world, pos));
        super.onPlace(state, world, pos, oldState, notify);
    }

    @Override
    public BlockState updateShape(
            BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos
    ) {
        world.scheduleTick(pos, this, getDelay(world.getRandom(), world, pos));
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    public static int getDelay(RandomSource random, LevelAccessor world, BlockPos pos) {
        BlockState downState = world.getBlockState(pos.below());
        if(downState.is(OperationStarcleaveBlocks.PETRICHORIC_PLASMA) || downState.is(OperationStarcleaveBlocks.PETRICHORIC_VAPOR)) {
            return 1 + random.nextInt(2);
        } else {
            return 4 + random.nextInt(18);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        absorbWater(world, pos);

        int impurity = getImpurity(state);

        int adjPlasma = 0;
        int adjGaps = 0;
        int adjVapor = 0;
        for(Direction direction : UPDATE_SHAPE_ORDER) {
            if(direction == Direction.UP) continue;

            BlockPos adjPos = pos.relative(direction);
            BlockState adjState = world.getBlockState(adjPos);
            if(adjState.canBeReplaced()) {
                adjGaps++;
            } else if(adjState.is(this)) {
                adjPlasma++;
            } else if(adjState.is(OperationStarcleaveBlocks.PETRICHORIC_VAPOR)) {
                if(PetrichoricVaporBlock.getDistance(adjState) <= (random.nextBoolean() ? 1 : 2)) {
                    adjVapor++;
                } else {
                    adjGaps++;
                }
            }
        }
        boolean gapAdjacent = (adjGaps > 0) || (adjVapor > 0 && adjPlasma < 2);

        for (Direction direction : UPDATE_SHAPE_ORDER) {
            if (direction == Direction.UP) continue;

            BlockPos adjPos = pos.relative(direction);
            BlockState adjState = world.getBlockState(adjPos);

            boolean horizontal = direction.getAxis().isHorizontal();
            boolean replaceable = adjState.canBeReplaced() || (adjState.is(OperationStarcleaveBlocks.PETRICHORIC_VAPOR) && !horizontal);

            int newImpurity = impurity + (((horizontal || !replaceable) && random.nextInt(4) == 0) ? 1 : 0);

            if ((newImpurity <= 7 && (impurity < 7 || (!horizontal && replaceable))) || (!horizontal && gapAdjacent) || (horizontal && gapAdjacent && !replaceable)) {
                if (replaceable || canDestroy(adjState)) {
                    if (!horizontal || random.nextInt(8) == 0) {
                        BlockState newState = getStateForImpurity(newImpurity);
                        world.setBlockAndUpdate(adjPos, newState);
                        world.setBlockAndUpdate(pos, newState);

                        impurity = newImpurity; // TODO fix bias
                    }
                }
            }
        }

        BlockPos downPos = pos.below();
        BlockState downState = world.getBlockState(downPos);
        if(downState.is(this)) {
            world.setBlockAndUpdate(pos, OperationStarcleaveBlocks.PETRICHORIC_VAPOR.defaultBlockState());
            int downImpurity = getImpurity(downState);
            if(impurity > downImpurity) {
                world.setBlockAndUpdate(downPos, state);
            }
        }
    }

    public BlockState getStateForImpurity(int impurity) {
        if(impurity < 0) {
            return Blocks.AIR.defaultBlockState();
        } else {
            if(impurity > 7) impurity = 7;
            return this.defaultBlockState().setValue(IMPURITY, impurity);
        }
    }

    public static int getImpurity(BlockState state) {
        if(state.hasProperty(IMPURITY)) {
            return state.getValue(IMPURITY);
        } else {
            return 0;
        }
    }
}
