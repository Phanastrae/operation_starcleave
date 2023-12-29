package phanastrae.operation_starcleave.mixin;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.world.OperationStarcleaveWorld;

import java.util.function.Supplier;

@Mixin(World.class)
public class WorldMixin implements OperationStarcleaveWorld {
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
}
