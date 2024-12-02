package phanastrae.operation_starcleave.particle;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import phanastrae.operation_starcleave.OperationStarcleave;

public class OperationStarcleaveParticleTypes {

    public static final SimpleParticleType FIRMAMENT_GLIMMER = FabricParticleTypes.simple();
    public static final SimpleParticleType GLIMMER_SMOKE = FabricParticleTypes.simple();
    public static final SimpleParticleType LARGE_GLIMMER_SMOKE = FabricParticleTypes.simple();

    public static void init() {
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, OperationStarcleave.id("firmament_glimmer"), FIRMAMENT_GLIMMER);
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, OperationStarcleave.id("glimmer_smoke"), GLIMMER_SMOKE);
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, OperationStarcleave.id("large_glimmer_smoke"), LARGE_GLIMMER_SMOKE);
    }
}
