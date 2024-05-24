package phanastrae.operation_starcleave.particle;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import phanastrae.operation_starcleave.OperationStarcleave;

public class OperationStarcleaveParticleTypes {

    public static final DefaultParticleType FIRMAMENT_GLIMMER = FabricParticleTypes.simple();
    public static final DefaultParticleType GLIMMER_SMOKE = FabricParticleTypes.simple();
    public static final DefaultParticleType LARGE_GLIMMER_SMOKE = FabricParticleTypes.simple();

    public static void init() {
        Registry.register(Registries.PARTICLE_TYPE, OperationStarcleave.id("firmament_glimmer"), FIRMAMENT_GLIMMER);
        Registry.register(Registries.PARTICLE_TYPE, OperationStarcleave.id("glimmer_smoke"), GLIMMER_SMOKE);
        Registry.register(Registries.PARTICLE_TYPE, OperationStarcleave.id("large_glimmer_smoke"), LARGE_GLIMMER_SMOKE);
    }
}
