package phanastrae.operation_starcleave.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.entity.OperationStarcleaveDamageTypes;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;

public class NuclearStardropEntity extends AbstractHurtingProjectile {

    public NuclearStardropEntity(EntityType<? extends NuclearStardropEntity> entityType, Level level) {
        super(entityType, level);
        this.accelerationPower = 0.04;
    }

    protected NuclearStardropEntity(
            double x, double y, double z, Level level
    ) {
        super(OperationStarcleaveEntityTypes.NUCLEAR_STARDROP, level);
    }

    public NuclearStardropEntity(
            double x, double y, double z, Vec3 movement, Level level
    ) {
        super(OperationStarcleaveEntityTypes.NUCLEAR_STARDROP, x, y, z, movement, level);
        this.accelerationPower = 0.04;
    }

    public NuclearStardropEntity(LivingEntity owner, Vec3 movement, Level level) {
        super(OperationStarcleaveEntityTypes.NUCLEAR_STARDROP, owner, movement, level);
        this.accelerationPower = 0.04;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Nullable
    @Override
    protected ParticleOptions getTrailParticle() {
        return OperationStarcleaveParticleTypes.LARGE_GLIMMER_SMOKE;
    }

    @Override
    protected double getDefaultGravity() {
        return 0.06F;
    }

    @Override
    public void tick() {
        Vec3 deltaMovement = this.getDeltaMovement();
        this.setDeltaMovement(deltaMovement.add(0, -this.getGravity(), 0));
        super.tick();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level() instanceof ServerLevel serverLevel) {
            Entity target = result.getEntity();

            DamageSource damageSource = OperationStarcleaveDamageTypes.source(serverLevel, OperationStarcleaveDamageTypes.IN_PHLOGISTIC_FIRE);

            target.hurt(damageSource, 5.0F);

            EnchantmentHelper.doPostAttackEffects(serverLevel, target, damageSource);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        Level level = this.level();
        super.onHitBlock(result);
        if (!level.isClientSide) {
            Entity entity = this.getOwner();
            if (!(entity instanceof Mob) || level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                BlockPos pos = result.getBlockPos().relative(result.getDirection());

                if (level.isEmptyBlock(pos)) {
                    level.setBlockAndUpdate(pos, OperationStarcleaveBlocks.NUCLEOSYNTHESEED.defaultBlockState());
                }
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    @Override
    protected void onDeflection(@Nullable Entity entity, boolean deflectedByPlayer) {
        super.onDeflection(entity, deflectedByPlayer);
        if(deflectedByPlayer) {
            this.accelerationPower = 0.1;
        } else {
            this.accelerationPower = 0.04;
        }
    }
}
