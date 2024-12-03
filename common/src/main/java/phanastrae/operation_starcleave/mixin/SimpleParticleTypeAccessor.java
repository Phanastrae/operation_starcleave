package phanastrae.operation_starcleave.mixin;

import net.minecraft.core.particles.SimpleParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SimpleParticleType.class)
public interface SimpleParticleTypeAccessor {

    @Invoker("<init>")
    static SimpleParticleType invokeInit(boolean overrideLimiter) {
        throw new AssertionError();
    }
}
