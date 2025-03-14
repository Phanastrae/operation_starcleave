package phanastrae.operation_starcleave.fabric.mixin;

import com.google.common.util.concurrent.AtomicDouble;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.fabric.duck.FabricEntityDuckInterface;
import phanastrae.operation_starcleave.fluid.OperationStarcleaveFluids;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    private LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Unique
    private boolean operation_starcleave$isInWaterEsqueFluid() {
        if(this.firstTick) {
            return false;
        } else {
            AtomicBoolean ab = new AtomicBoolean(false);
            OperationStarcleaveFluids.forEachXPGF(xpgf -> {
                if(xpgf.isWaterEsque()) {
                    boolean b = this.fluidHeight.getDouble(xpgf.getFluidTag()) > 0.0;
                    if (b) {
                        ab.set(true);
                    }
                }
            });
            return ab.get();
        }
    }

    @Unique
    private double operation_starcleave$getHighestCustomFluid() {
        AtomicDouble ad = new AtomicDouble(0.0);
        OperationStarcleaveFluids.forEachXPGF(xpgf -> {
            if(xpgf.isWaterEsque()) {
                double fluidHeight = this.getFluidHeight(xpgf.getFluidTag());
                if (fluidHeight > 0.0) {
                    ad.set(fluidHeight);
                }
            }
        });
        return ad.get();
    }

    @Unique
    private void operation_starcleave$setForceInWaterTrue(boolean value) {
        ((FabricEntityDuckInterface)this).operation_starcleave$setForceInWaterTrue(value);
    }

    @Inject(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;"))
    private void operation_starcleave$travel_forceInWaterTrue(Vec3 travelVector, CallbackInfo ci) {
        if(this.operation_starcleave$isInWaterEsqueFluid()) {
            this.operation_starcleave$setForceInWaterTrue(true);
        }
    }

    @Inject(method = "travel", at = @At(value = "RETURN"))
    private void operation_starcleave$travel_unforceInWaterTrue_2(Vec3 travelVector, CallbackInfo ci) {
        // make sure value is set back to false if it wasn't already set
        this.operation_starcleave$setForceInWaterTrue(false);
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getFluidJumpThreshold()D"))
    private void operation_starcleave$aiStep_customFluidSwim(CallbackInfo ci, @Local(ordinal = 0) LocalBooleanRef LBR_bl, @Local(ordinal = 3) LocalDoubleRef LDR_g) {
        if(!LBR_bl.get()) {
            double maxCustomFluidHeight = this.operation_starcleave$getHighestCustomFluid();
            if(maxCustomFluidHeight > 0.0 && maxCustomFluidHeight > LDR_g.get()) {
                LBR_bl.set(true);
                LDR_g.set(maxCustomFluidHeight);
            }
        }
    }
}
