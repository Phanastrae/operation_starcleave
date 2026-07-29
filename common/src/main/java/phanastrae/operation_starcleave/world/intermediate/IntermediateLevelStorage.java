package phanastrae.operation_starcleave.world.intermediate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface IntermediateLevelStorage {

    boolean setBlockState(BlockPos pos, BlockState state);

    BlockState getBlockState(BlockPos pos);

    @Nullable
    BlockEntity getBlockEntity(BlockPos pos);

    void addEntity(Entity entity);
}
