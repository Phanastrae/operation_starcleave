package phanastrae.operation_starcleave.entity.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import phanastrae.operation_starcleave.entity.OperationStarcleaveDamageTypes;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;

public class StarbleachedInsidesStatusEffect extends MobEffect {
    protected StarbleachedInsidesStatusEffect() {
        super(MobEffectCategory.HARMFUL, 0x63f2e2);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        Level world = entity.level();
        if(world != null) {
            entity.hurt(OperationStarcleaveDamageTypes.source(world, OperationStarcleaveDamageTypes.INTERNAL_STARBLEACHING), 0.5f * (amplifier + 1));
            if(world instanceof ServerLevel serverWorld) {
                float h = entity.getBbHeight();
                float w = entity.getBbWidth();
                serverWorld.sendParticles(OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER, entity.getX(), entity.getY() + h / 2, entity.getZ(), 350,
                        w / 2,
                        h / 2,
                        w / 2,
                        0.05);
            }
        }

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        int i = 5;
        return duration % i == 0;
    }
}
