package phanastrae.operation_starcleave.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BlessedClothCarpetBlock extends CarpetBlock {
    public BlessedClothCarpetBlock(Properties settings) {
        super(settings);
    }

    @Override
    public void fallOn(Level world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        // TODO implement properly, this doesn't really work as intended
        entity.causeFallDamage(fallDistance, 0.0F, entity.damageSources().fall());
    }
}
