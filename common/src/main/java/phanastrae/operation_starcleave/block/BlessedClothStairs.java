package phanastrae.operation_starcleave.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BlessedClothStairs extends StairBlock {

    public BlessedClothStairs(BlockState baseState, BlockBehaviour.Properties properties) {
        super(baseState, properties);
    }

    @Override
    public void fallOn(Level world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        entity.causeFallDamage(fallDistance, 0.0F, entity.damageSources().fall());
    }
}
