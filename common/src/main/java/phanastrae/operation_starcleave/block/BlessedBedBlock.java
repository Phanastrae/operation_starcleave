package phanastrae.operation_starcleave.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import phanastrae.operation_starcleave.block.entity.BlessedBedBlockEntity;

public class BlessedBedBlock extends BedBlock {

    public BlessedBedBlock(Properties settings) {
        super(DyeColor.YELLOW, settings);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BlessedBedBlockEntity(pos, state);
    }

    public static void blessedSleep(LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(MobEffects.LUCK, 36000, 0)); // luck I for 10 minutes
        entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 18000, 0)); // absorption I for 5 minutes
        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 1800, 0)); // regen I for 30 seconds
    }

    @Override
    public void fallOn(Level world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        entity.causeFallDamage(fallDistance, 0.0F, entity.damageSources().fall());
    }
}
