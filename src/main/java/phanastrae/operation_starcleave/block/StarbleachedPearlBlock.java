package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec3;
import phanastrae.operation_starcleave.entity.projectile.StarbleachedPearlEntity;

public class StarbleachedPearlBlock extends Block {
    public static final MapCodec<StarbleachedPearlBlock> CODEC = simpleCodec(StarbleachedPearlBlock::new);
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

    @Override
    public MapCodec<? extends StarbleachedPearlBlock> codec() {
        return CODEC;
    }

    public StarbleachedPearlBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(TRIGGERED, Boolean.valueOf(false)));
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        boolean redstone = world.hasNeighborSignal(pos);
        boolean triggered = state.getValue(TRIGGERED);
        if (redstone && !triggered) {
            int neighboringPearlBlocks = 0;
            int neighboringDampners = 0;
            for(Direction direction : UPDATE_SHAPE_ORDER) {
                BlockState state1 = world.getBlockState(pos.offset(direction.getNormal()));
                if(state1.is(OperationStarcleaveBlocks.STARBLEACHED_PEARL_BLOCK)) {
                    neighboringPearlBlocks++;
                }
                if(state1.is(BlockTags.DAMPENS_VIBRATIONS)) {
                    neighboringDampners++;
                }
            }
            float pearlMultiplier = 1f / (neighboringPearlBlocks + 1);
            float dampenMultiplier = (1 - neighboringDampners / 7f);
            float audioMultiplier = 0.7f * pearlMultiplier * dampenMultiplier;

            int rstone = world.getBestNeighborSignal(pos);
            StarbleachedPearlEntity.repel(new Vec3(pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5), 0.5f * (rstone + 4) * 15/19f, 0.125f * (rstone + 8) * 15/23f, world, null, audioMultiplier);
            world.setBlock(pos, state.setValue(TRIGGERED, Boolean.valueOf(true)), Block.UPDATE_CLIENTS);
        } else if (!redstone && triggered) {
            world.setBlock(pos, state.setValue(TRIGGERED, Boolean.valueOf(false)), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TRIGGERED);
    }
}
