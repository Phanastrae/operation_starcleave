package phanastrae.operation_starcleave.particle;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.mixin.common.accessor.SimpleParticleTypeAccessor;

import java.util.function.BiConsumer;

public class OperationStarcleaveParticleTypes {

    public static final SimpleParticleType FIRMAMENT_GLIMMER = simple(false);
    public static final SimpleParticleType GLIMMER_SMOKE = simple(false);
    public static final SimpleParticleType LARGE_GLIMMER_SMOKE = simple(false);
    public static final SimpleParticleType STARBLEACH_SWIRL = simple(false);
    public static final SimpleParticleType LARGE_STARBLEACH_SWIRL = simple(false);
    public static final SimpleParticleType STARBLEACHED_LEAVES = simple(false);
    public static final SimpleParticleType PLASMA_DUST = simple(false);
    public static final SimpleParticleType NUCLEAR_SMOKE = simple(false);
    public static final SimpleParticleType LARGE_NUCLEAR_SMOKE = simple(false);
    public static final SimpleParticleType NUCLEO_LIGHTNING = simple(false);

    public static void init(BiConsumer<ResourceLocation, ParticleType<?>> r) {
        r.accept(id("firmament_glimmer"), FIRMAMENT_GLIMMER);
        r.accept(id("glimmer_smoke"), GLIMMER_SMOKE);
        r.accept(id("large_glimmer_smoke"), LARGE_GLIMMER_SMOKE);
        r.accept(id("starbleach_swirl"), STARBLEACH_SWIRL);
        r.accept(id("large_starbleach_swirl"), LARGE_STARBLEACH_SWIRL);
        r.accept(id("starbleached_leaves"), STARBLEACHED_LEAVES);
        r.accept(id("plasma_dust"), PLASMA_DUST);
        r.accept(id("nuclear_smoke"), NUCLEAR_SMOKE);
        r.accept(id("large_nuclear_smoke"), LARGE_NUCLEAR_SMOKE);
        r.accept(id("nucleo_lightning"), NUCLEO_LIGHTNING);
    }

    private static ResourceLocation id(String path) {
        return OperationStarcleave.id(path);
    }

    private static SimpleParticleType simple(boolean overrideLimiter) {
        return SimpleParticleTypeAccessor.invokeInit(overrideLimiter);
    }
}
