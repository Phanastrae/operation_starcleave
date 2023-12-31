package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
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
            int rstone = world.getReceivedRedstonePower(pos);
            StarbleachedPearlEntity.repel(new Vec3d(pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5), rstone / 2f, rstone / 8f, world, null);
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
