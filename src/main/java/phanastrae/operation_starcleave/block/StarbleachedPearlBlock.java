package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import phanastrae.operation_starcleave.entity.projectile.StarbleachedPearlEntity;

public class StarbleachedPearlBlock extends Block {
    public static final MapCodec<StarbleachedPearlBlock> CODEC = createCodec(StarbleachedPearlBlock::new);
    public static final BooleanProperty TRIGGERED = Properties.TRIGGERED;

    @Override
    public MapCodec<? extends StarbleachedPearlBlock> getCodec() {
        return CODEC;
    }

    public StarbleachedPearlBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(TRIGGERED, Boolean.valueOf(false)));
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        boolean redstone = world.isReceivingRedstonePower(pos);
        boolean triggered = state.get(TRIGGERED);
        if (redstone && !triggered) {
            int neighboringPearlBlocks = 0;
            int neighboringDampners = 0;
            for(Direction direction : DIRECTIONS) {
                BlockState state1 = world.getBlockState(pos.add(direction.getVector()));
                if(state1.isOf(OperationStarcleaveBlocks.STARBLEACHED_PEARL_BLOCK)) {
                    neighboringPearlBlocks++;
                }
                if(state1.isIn(BlockTags.DAMPENS_VIBRATIONS)) {
                    neighboringDampners++;
                }
            }
            float pearlMultiplier = 1f / (neighboringPearlBlocks + 1);
            float dampenMultiplier = (1 - neighboringDampners / 7f);
            float audioMultiplier = 0.7f * pearlMultiplier * dampenMultiplier;

            int rstone = world.getReceivedRedstonePower(pos);
            StarbleachedPearlEntity.repel(new Vec3d(pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5), 0.5f * (rstone + 4) * 15/19f, 0.125f * (rstone + 8) * 15/23f, world, null, audioMultiplier);
            world.setBlockState(pos, state.with(TRIGGERED, Boolean.valueOf(true)), Block.NOTIFY_LISTENERS);
        } else if (!redstone && triggered) {
            world.setBlockState(pos, state.with(TRIGGERED, Boolean.valueOf(false)), Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(TRIGGERED);
    }
}
