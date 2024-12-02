package phanastrae.operation_starcleave.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.operation_starcleave.duck.WorldDuck;
import phanastrae.operation_starcleave.world.firmament.Firmament;

import java.util.function.Supplier;

@Mixin(Level.class)
public class LevelMixin implements WorldDuck {
    private int operation_starcleave$cleavingFlashTicksLeft;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void operation_starcleave$onInit(WritableLevelData properties, ResourceKey registryRef, RegistryAccess registryManager, Holder dimensionEntry, Supplier profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates, CallbackInfo ci) {
        this.operation_starcleave$cleavingFlashTicksLeft = 0;
    }

    @Override
    public void operation_starcleave$setCleavingFlashTicksLeft(int ticks) {
        this.operation_starcleave$cleavingFlashTicksLeft = ticks;
    }

    @Override
    public int operation_starcleave$getCleavingFlashTicksLeft() {
        return this.operation_starcleave$cleavingFlashTicksLeft;
    }

    // Stop Precipitation beneath damaged firmament
    @Inject(method = "isRainingAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;", shift = At.Shift.BEFORE), cancellable = true)
    private void operation_starcleave$blockRain(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        Level world = (Level)(Object)this;
        Firmament firmament = Firmament.fromWorld(world);
        if(firmament == null) return;

        int damage = firmament.getDamage(pos.getX(), pos.getZ());
        if(damage >= 5 && firmament.getY() > pos.getY()) {
            cir.setReturnValue(false);
        }
    }
}
