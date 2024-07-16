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

public class PetrichoricPlasmaBlock extends AbstractPetrichoricBlock {
    public static final MapCodec<PetrichoricPlasmaBlock> CODEC = createCodec(PetrichoricPlasmaBlock::new);

    public static final IntProperty IMPURITY = IntProperty.of("impurity", 0, 7);

    @Override
    protected MapCodec<? extends PetrichoricPlasmaBlock> getCodec() {
        return CODEC;
    }

    public PetrichoricPlasmaBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(IMPURITY, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(IMPURITY);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        world.scheduleBlockTick(pos, this, getDelay(world.getRandom(), world, pos));
        super.onBlockAdded(state, world, pos, oldState, notify);
    }

    @Override
    public BlockState getStateForNeighborUpdate(
            BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
    ) {
        world.scheduleBlockTick(pos, this, getDelay(world.getRandom(), world, pos));
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public static int getDelay(Random random, WorldAccess world, BlockPos pos) {
        BlockState downState = world.getBlockState(pos.down());
        if(downState.isOf(OperationStarcleaveBlocks.PETRICHORIC_PLASMA) || downState.isOf(OperationStarcleaveBlocks.PETRICHORIC_VAPOR)) {
            return 1 + random.nextInt(2);
        } else {
            return 4 + random.nextInt(18);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        absorbWater(world, pos);

        int impurity = getImpurity(state);

        int adjPlasma = 0;
        int adjGaps = 0;
        int adjVapor = 0;
        for(Direction direction : DIRECTIONS) {
            if(direction == Direction.UP) continue;

            BlockPos adjPos = pos.offset(direction);
            BlockState adjState = world.getBlockState(adjPos);
            if(adjState.isReplaceable()) {
                adjGaps++;
            } else if(adjState.isOf(this)) {
                adjPlasma++;
            } else if(adjState.isOf(OperationStarcleaveBlocks.PETRICHORIC_VAPOR)) {
                if(PetrichoricVaporBlock.getDistance(adjState) <= (random.nextBoolean() ? 1 : 2)) {
                    adjVapor++;
                } else {
                    adjGaps++;
                }
            }
        }
        boolean gapAdjacent = (adjGaps > 0) || (adjVapor > 0 && adjPlasma < 2);

        for (Direction direction : DIRECTIONS) {
            if (direction == Direction.UP) continue;

            BlockPos adjPos = pos.offset(direction);
            BlockState adjState = world.getBlockState(adjPos);

            boolean horizontal = direction.getAxis().isHorizontal();
            boolean replaceable = adjState.isReplaceable() || (adjState.isOf(OperationStarcleaveBlocks.PETRICHORIC_VAPOR) && !horizontal);

            int newImpurity = impurity + (((horizontal || !replaceable) && random.nextInt(4) == 0) ? 1 : 0);

            if ((newImpurity <= 7 && (impurity < 7 || (!horizontal && replaceable))) || (!horizontal && gapAdjacent) || (horizontal && gapAdjacent && !replaceable)) {
                if (replaceable || canDestroy(adjState)) {
                    if (!horizontal || random.nextInt(8) == 0) {
                        BlockState newState = getStateForImpurity(newImpurity);
                        world.setBlockState(adjPos, newState);
                        world.setBlockState(pos, newState);

                        impurity = newImpurity; // TODO fix bias
                    }
                }
            }
        }

        BlockPos downPos = pos.down();
        BlockState downState = world.getBlockState(downPos);
        if(downState.isOf(this)) {
            world.setBlockState(pos, OperationStarcleaveBlocks.PETRICHORIC_VAPOR.getDefaultState());
            int downImpurity = getImpurity(downState);
            if(impurity > downImpurity) {
                world.setBlockState(downPos, state);
            }
        }
    }

    public BlockState getStateForImpurity(int impurity) {
        if(impurity < 0) {
            return Blocks.AIR.getDefaultState();
        } else {
            if(impurity > 7) impurity = 7;
            return this.getDefaultState().with(IMPURITY, impurity);
        }
    }

    public static int getImpurity(BlockState state) {
        if(state.contains(IMPURITY)) {
            return state.get(IMPURITY);
        } else {
            return 0;
        }
    }
}
