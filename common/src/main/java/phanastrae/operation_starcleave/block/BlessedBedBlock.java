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
    private static final int SECONDS = 20;
    private static final int MINUTES = 60 * SECONDS;

    public BlessedBedBlock(Properties settings) {
        super(DyeColor.YELLOW, settings);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BlessedBedBlockEntity(pos, state);
    }

    public static void attemptBlessedSleep(LivingEntity entity) {
        Level level = entity.level();
        if (level.isClientSide()) {
            return;
        }

        entity.getSleepingPos().filter(level::hasChunkAt).ifPresent(pos -> {
            BlockState blockstate = level.getBlockState(pos);
            if (blockstate.is(OperationStarcleaveBlocks.BLESSED_BED)) {
                BlessedBedBlock.blessedSleep(entity);
            }
        });
    }

    public static void blessedSleep(LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(MobEffects.LUCK, 7 * MINUTES + 7 * SECONDS, 0)); // luck I for 7 minutes 7 seconds
        entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 18 * MINUTES, 0)); // absorption I for 18 minutes
        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 25 * SECONDS, 0)); // regen I for 25 seconds
    }

    @Override
    public void fallOn(Level world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        entity.causeFallDamage(fallDistance, 0.0F, entity.damageSources().fall());
    }
}
