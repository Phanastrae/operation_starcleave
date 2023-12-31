package phanastrae.operation_starcleave.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;

public class OperationStarcleaveParticles {

    public static void init() {
        ParticleFactoryRegistry.getInstance().register(OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER, FirmamentGlimmerFactory::new);
    }


    public static class FirmamentGlimmerFactory extends WaterSuspendParticle.UnderwaterFactory {

        public FirmamentGlimmerFactory(SpriteProvider spriteProvider) {
            super(spriteProvider);
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            Particle particle = super.createParticle(defaultParticleType, clientWorld, d, e, f, g, h, i);
            if(particle != null) {
                float fl = clientWorld.random.nextFloat();
                float twopi = 2 * MathHelper.PI;
                float red = MathHelper.sin(fl * twopi) * 0.2f + 0.8f;
                float green = MathHelper.sin((fl + 1/3f) * twopi) * 0.2f + 0.8f;
                float blue = MathHelper.sin((fl + 2/3f) * twopi) * 0.2f + 0.8f;
                particle.setColor(red, green, blue);
                particle.setVelocity(g, h, i);
            }
            return particle;
        }
    }
}
