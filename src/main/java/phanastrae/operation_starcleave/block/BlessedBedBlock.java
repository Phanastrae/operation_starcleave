package phanastrae.operation_starcleave.block;

import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import phanastrae.operation_starcleave.block.entity.BlessedBedBlockEntity;

public class BlessedBedBlock extends BedBlock {

    public BlessedBedBlock(Settings settings) {
        super(DyeColor.YELLOW, settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BlessedBedBlockEntity(pos, state);
    }

    public static void blessedSleep(LivingEntity entity) {
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.LUCK, 36000, 0)); // luck I for 10 minutes
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 18000, 0)); // absorption I for 5 minutes
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 1800, 0)); // regen I for 30 seconds
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        entity.handleFallDamage(fallDistance, 0.0F, entity.getDamageSources().fall());
    }
}
