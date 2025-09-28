package phanastrae.operation_starcleave.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.ShootTongue;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.operation_starcleave.entity.mob.SubcaelicTorpedoEntity;

import java.util.Optional;

@Mixin(ShootTongue.class)
public class ShootTongueMixin {

    @Shadow
    private int eatAnimationTimer;

    @Inject(method = "canPathfindToTarget", at = @At("HEAD"), cancellable = true)
    private void operation_starcleave$allowCatchingMidairTorpedos(Frog frog, LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (target instanceof SubcaelicTorpedoEntity && target.distanceTo(frog) / 3.0F < 1.75F) {
            double dx = frog.getX() - target.getX();
            double dy = frog.getY() - target.getY();
            double dz = frog.getZ() - target.getZ();

            double horizontalDSqr = dx * dx + dz * dz;
            double dySqr = dy * dy;

            // allow catching if angle is within 60 degrees of horizontal
            double f = 1.732;
            if (dySqr < horizontalDSqr * f * f) {
                cir.setReturnValue(true);
            }
        }
    }

    @WrapOperation(method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/animal/frog/Frog;J)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;distanceTo(Lnet/minecraft/world/entity/Entity;)F", ordinal = 0))
    private float operation_starcleave$extendTorpedoGrabRange(LivingEntity instance, Entity entity, Operation<Float> original) {
        float distance = original.call(instance, entity);

        if (instance instanceof SubcaelicTorpedoEntity) {
            distance /= 3.0F;
        }

        return distance;
    }

    @Inject(method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/animal/frog/Frog;J)V", at = @At("RETURN"))
    private void operation_starcleave$dragInTorpedos(ServerLevel level, Frog owner, long gameTime, CallbackInfo ci) {
        boolean inCatchPhase = this.eatAnimationTimer >= 1 && this.eatAnimationTimer < 6; // state enum is private so easier to check timer instead
        if (inCatchPhase) {
            Optional<Entity> targetOptional = owner.getTongueTarget();
            if (targetOptional.isPresent()) {
                Entity target = targetOptional.get();
                if (target instanceof SubcaelicTorpedoEntity torpedo) {
                    // set velocity
                    Vec3 offset = torpedo.position().vectorTo(owner.position());
                    Vec3 velocity = offset.scale(0.75);
                    torpedo.setDeltaMovement(velocity);

                    // stop fuse growth
                    if (torpedo.getFuseSpeed() > 0) {
                        torpedo.setFuseSpeed(0);
                    }
                    torpedo.limitFuseTimeTo(90);
                }
            }
        }
    }
}
