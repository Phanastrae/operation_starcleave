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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.block.PhlogisticFireBlock;
import phanastrae.operation_starcleave.entity.OperationStarcleaveDamageTypes;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityAttachment;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;

public class PhlogisticSparkEntity extends AbstractHurtingProjectile {

    public PhlogisticSparkEntity(EntityType<? extends PhlogisticSparkEntity> entityType, Level level) {
        super(entityType, level);
        this.accelerationPower = 0.04;
    }

    protected PhlogisticSparkEntity(
            double x, double y, double z, Level level
    ) {
        super(OperationStarcleaveEntityTypes.PHLOGISTIC_SPARK, level);
    }

    public PhlogisticSparkEntity(
            double x, double y, double z, Vec3 movement, Level level
    ) {
        super(OperationStarcleaveEntityTypes.PHLOGISTIC_SPARK, x, y, z, movement, level);
        this.accelerationPower = 0.04;
    }

    public PhlogisticSparkEntity(LivingEntity owner, Vec3 movement, Level level) {
        super(OperationStarcleaveEntityTypes.PHLOGISTIC_SPARK, owner, movement, level);
        this.accelerationPower = 0.04;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    public boolean isOnFire() {
        return true;
    }

    @Override
    protected boolean shouldBurn() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Nullable
    @Override
    protected ParticleOptions getTrailParticle() {
        return OperationStarcleaveParticleTypes.GLIMMER_SMOKE;
    }

    @Override
    protected double getDefaultGravity() {
        return 0.025F;
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
            OperationStarcleaveEntityAttachment osea = OperationStarcleaveEntityAttachment.fromEntity(target);
            Entity owner = this.getOwner();

            int remainingFireTicks = osea.getPhlogisticFireTicks();
            DamageSource damageSource = OperationStarcleaveDamageTypes.phlogisticSpark(serverLevel, this, owner);

            osea.setOnPhlogisticFireFor(5.0F);
            if (!target.hurt(damageSource, 5.0F)) {
                osea.setPhlogisticFireTicks(remainingFireTicks);
            } else {
                EnchantmentHelper.doPostAttackEffects(serverLevel, target, damageSource);
            }
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

                boolean hitWater = (level.getBlockState(pos).is(Blocks.WATER));
                if (level.isEmptyBlock(pos) || hitWater) {
                    level.setBlockAndUpdate(pos, PhlogisticFireBlock.getState(this.level(), pos).setValue(PhlogisticFireBlock.WATERLOGGED, hitWater));
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
