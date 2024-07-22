package phanastrae.operation_starcleave.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import phanastrae.operation_starcleave.entity.OperationStarcleaveDamageTypes;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;

public class StarbleachedInsidesStatusEffect extends StatusEffect {
    protected StarbleachedInsidesStatusEffect() {
        super(StatusEffectCategory.HARMFUL, 0x63f2e2);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        World world = entity.getWorld();
        if(world != null) {
            entity.damage(OperationStarcleaveDamageTypes.of(world, OperationStarcleaveDamageTypes.INTERNAL_STARBLEACHING), 0.5f * (amplifier + 1));
            if(world instanceof ServerWorld serverWorld) {
                float h = entity.getHeight();
                float w = entity.getWidth();
                serverWorld.spawnParticles(OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER, entity.getX(), entity.getY() + h / 2, entity.getZ(), 350,
                        w / 2,
                        h / 2,
                        w / 2,
                        0.05);
            }
        }

        return true;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        int i = 5;
        return duration % i == 0;
    }
}
