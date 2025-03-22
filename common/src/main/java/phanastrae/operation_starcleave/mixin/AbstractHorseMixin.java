package phanastrae.operation_starcleave.mixin;

import net.minecraft.util.Mth;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityAttachment;
import phanastrae.operation_starcleave.world.firmament.Firmament;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin extends Animal implements ContainerListener, HasCustomInventoryScreen, OwnableEntity, PlayerRideableJumping, Saddleable {
    @Shadow @Nullable
    public abstract LivingEntity getControllingPassenger();
    @Shadow protected boolean allowStandSliding;
    @Shadow public abstract void standIfPossible();
    @Shadow protected abstract void playJumpSound();
    @Shadow protected float playerJumpPendingScale;

    protected AbstractHorseMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void operation_starcleave$handleGliding(CallbackInfo ci) {
        OperationStarcleaveEntityAttachment osea = OperationStarcleaveEntityAttachment.fromEntity(this);

        osea.setPrevPegasusWingSpread(osea.getPegasusWingSpread());
        osea.setPrevPegasusWingFlap(osea.getPegasusWingFlap());
        osea.setWasPegasusFlying(osea.isPegasusFlying());

        boolean isPegasus = OperationStarcleaveEntityAttachment.isPegasus((AbstractHorse)(Object)this);
        boolean isMidairPegasus = isPegasus && !this.onGround();

        if(osea.isPegasusFlying() && osea.getPegasusFlightCharge() <= 0) {
            osea.setPegasusFlying(false);
        }

        if(isPegasus) {
            if(osea.isPegasusFlying()) {
                osea.setPegasusFlightCharge(Math.max(osea.getPegasusFlightCharge() - 0.025F, 0F));
            } else {
                float starlight = this.operation_starcleave$calculateStarlight();
                float rechargeSpeed = this.onGround()
                        ? 0.05F * (0.25F + 0.75F * starlight)
                        : 0.00625F * (0.125F + 0.875F * starlight);

                float maxCharge = 0.5F + 0.5F * starlight;

                float oldCharge = osea.getPegasusFlightCharge();
                osea.setPegasusFlightCharge(Math.min(oldCharge + rechargeSpeed, Math.max(maxCharge, oldCharge - 0.00625F)));
            }
        } else {
            osea.setPegasusFlightCharge(0F);
        }

        if(!isPegasus && osea.getPegasusFlightCharge() != 0) {
            osea.setPegasusFlightCharge(0);
        }

        if(isMidairPegasus && this.fallDistance > 1.5F && !osea.isPegasusGliding()) {
            osea.setPegasusGliding(true);
        } else if(!isMidairPegasus && osea.isPegasusGliding()) {
            osea.setPegasusGliding(false);
        }

        if(osea.isPegasusGliding()) {
            this.fallDistance = -6;

            osea.setPegasusWingSpread(Math.min(1, osea.getPegasusWingSpread() + 0.08F));
        } else if(isPegasus) {
            osea.setPegasusWingSpread(Math.max(0, osea.getPegasusWingSpread() - 0.14F));
        } else {
            osea.setPegasusWingSpread(0);
        }

        if(osea.isPegasusFlying()) {
            osea.setPegasusWingFlap(Math.min(1, osea.getPegasusWingFlap() + 0.4F));
        } else if(isPegasus) {
            osea.setPegasusWingFlap(Math.max(0, osea.getPegasusWingFlap() - 0.15F));
        } else {
            osea.setPegasusWingFlap(0);
        }

        if(this.isControlledByLocalInstance()) {
            if(osea.isPegasusFlying()) {
                AttributeInstance jumpStrengthAttribute = this.getAttribute(Attributes.JUMP_STRENGTH);
                double jumpStrength = jumpStrengthAttribute == null ? 0 : jumpStrengthAttribute.getValue();

                Vec3 dm = this.getDeltaMovement();
                this.setDeltaMovement(dm.x, dm.y > 0.6 ? dm.y : Math.min(dm.y + jumpStrength * 0.35, 0.6), dm.z);
            }

            if(osea.isPegasusGliding()) {
                LivingEntity control = this.getControllingPassenger();
                if (control == null) {
                    control = this;
                }
                Vec3 lookAngle = control.getLookAngle();

                Vec3 velocity = this.getDeltaMovement();
                Vec3 targetVelocity = lookAngle.scale(velocity.length());
                double turnScale = (osea.isPegasusFlying() ? 0.1 : 0.35) * (1 - lookAngle.y * lookAngle.y * lookAngle.y * lookAngle.y);
                Vec3 turnAcceleration = targetVelocity.subtract(velocity).scale(turnScale);

                this.addDeltaMovement(turnAcceleration);

                float starlight = this.operation_starcleave$calculateStarlight();
                double boostSpeed = 0.05 * (0.5 + 0.5 * starlight) * this.getSpeed();
                this.addDeltaMovement(lookAngle.multiply(1, 0, 1).scale(boostSpeed));
            }
        }
    }

    @Unique
    private float operation_starcleave$calculateStarlight() {
        Level level = this.level();
        int skyLight = level.getBrightness(LightLayer.SKY, this.blockPosition());

        float starlight;
        if(skyLight == 0) {
            starlight = 0;
        } else {
            Firmament firmament = Firmament.fromLevel(this.level());
            if(firmament == null) {
                starlight = 0;
            } else {
                int firmamentHeight = firmament.getY();
                double horseHeight = this.getY();
                double heightAboveFirmament = horseHeight - firmamentHeight;
                float heightDropoff = 1F - (float)Math.clamp(heightAboveFirmament / 24.0, 0, 1); // gradually dropoff up to 24 blocks above the firmament

                if(heightDropoff <= 0) {
                    starlight = 0;
                } else {
                    float SAMPLE_RADIUS = 3.5F;
                    int SEARCH_SIZE = Mth.ceil(SAMPLE_RADIUS);
                    int bx = this.getBlockX();
                    int bz = this.getBlockZ();

                    float damage = 0;
                    for(int i = -SEARCH_SIZE; i <= SEARCH_SIZE; i++) {
                        for(int j = -SEARCH_SIZE; j <= SEARCH_SIZE; j++) {
                            int distSqr = i*i + j*j;
                            if(distSqr < SAMPLE_RADIUS * SAMPLE_RADIUS) {
                                int x = bx + i * 4;
                                int z = bz + j * 4;

                                // sample damage
                                float d = firmament.getDamage(x, z) / 7F;
                                // reduce sampled damage based on distance
                                d = d - Math.max(0, 2 * Mth.sqrt(distSqr) / SAMPLE_RADIUS - 1);
                                // increase damage if larger
                                damage = Math.max(damage, d);
                            }
                        }
                    }

                    starlight = damage * (skyLight / 15F) * heightDropoff;
                }
            }
        }

        return starlight;
    }

    @Inject(method = "getRiddenInput", at = @At("HEAD"), cancellable = true)
    private void operation_starcleave$cancelInputWhileGliding(Player player, Vec3 travelVector, CallbackInfoReturnable<Vec3> cir) {
        OperationStarcleaveEntityAttachment osea = OperationStarcleaveEntityAttachment.fromEntity(this);

        if(osea.isPegasus() && osea.isPegasusGliding() && !osea.isPegasusFlying()) {
            cir.setReturnValue(Vec3.ZERO);
        }
    }

    @Inject(method = "onPlayerJump", at = @At("HEAD"), cancellable = true)
    private void operation_starcleave$onPlayerJump(int jumpPower, CallbackInfo ci) {
        OperationStarcleaveEntityAttachment osea = OperationStarcleaveEntityAttachment.fromEntity(this);
        if(osea.isPegasus()) {
            if(!osea.wasPegasusFlying() && osea.getPegasusFlightCharge() > 0.1F) {
                osea.setPegasusFlying(true);

                this.allowStandSliding = true;
                this.standIfPossible();
                if(this.onGround()) {
                    this.playerJumpPendingScale = 1.1F;
                }
            }

            ci.cancel();
        }
    }

    @Inject(method = "handleStartJump", at = @At("HEAD"), cancellable = true)
    private void operation_starcleave$onHorseStartJump(int jumpPower, CallbackInfo ci) {
        OperationStarcleaveEntityAttachment osea = OperationStarcleaveEntityAttachment.fromEntity(this);
        if(osea.isPegasus()) {
            if(!osea.wasPegasusFlying() && osea.getPegasusFlightCharge() > 0.1F) {
                osea.setPegasusFlying(true);

                this.allowStandSliding = true;
                this.standIfPossible();
                this.playJumpSound();
            }

            ci.cancel();
        }
    }

    @Inject(method = "handleStopJump", at = @At("HEAD"))
    private void operation_starcleave$onHorseStopJump(CallbackInfo ci) {
        OperationStarcleaveEntityAttachment osea = OperationStarcleaveEntityAttachment.fromEntity(this);
        osea.setPegasusFlying(false);
    }
}
