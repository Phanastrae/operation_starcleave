package phanastrae.operation_starcleave.mixin;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.operation_starcleave.duck.WorldDuck;
import phanastrae.operation_starcleave.world.firmament.Firmament;

import java.util.function.Supplier;

@Mixin(World.class)
public class WorldMixin implements WorldDuck {
    private int operation_starcleave$cleavingFlashTicksLeft;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void operation_starcleave$onInit(MutableWorldProperties properties, RegistryKey registryRef, DynamicRegistryManager registryManager, RegistryEntry dimensionEntry, Supplier profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates, CallbackInfo ci) {
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
    @Inject(method = "hasRain", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBiome(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/registry/entry/RegistryEntry;", shift = At.Shift.BEFORE), cancellable = true)
    private void operation_starcleave$blockRain(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        World world = (World)(Object)this;
        Firmament firmament = Firmament.fromWorld(world);
        if(firmament == null) return;

        int damage = firmament.getDamage(pos.getX(), pos.getZ());
        if(damage >= 5 && firmament.getY() > pos.getY()) {
            cir.setReturnValue(false);
        }
    }
}
