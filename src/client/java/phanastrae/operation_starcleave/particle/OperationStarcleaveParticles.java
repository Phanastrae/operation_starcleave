package phanastrae.operation_starcleave.particle;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;

public class OperationStarcleaveParticles {

    public static void init() {
        ParticleFactoryRegistry.getInstance().register(OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER, FirmamentGlimmerFactory::new);
        ParticleFactoryRegistry.getInstance().register(OperationStarcleaveParticleTypes.GLIMMER_SMOKE, GlimmerSmokeFactory::new);
        ParticleFactoryRegistry.getInstance().register(OperationStarcleaveParticleTypes.LARGE_GLIMMER_SMOKE, LargeGlimmerSmokeFactory::new);
    }

    public static class FirmamentGlimmerFactory extends WaterSuspendParticle.UnderwaterFactory {

        public FirmamentGlimmerFactory(SpriteProvider spriteProvider) {
            super(spriteProvider);
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            Particle particle = super.createParticle(simpleParticleType, clientWorld, d, e, f, g, h, i);
            if(particle != null) {
                float ang = clientWorld.random.nextFloat();
                float red = MathHelper.sin(ang * MathHelper.TAU) * 0.2f + 0.8f;
                float green = MathHelper.sin((ang + 1/3f) * MathHelper.TAU) * 0.2f + 0.8f;
                float blue = MathHelper.sin((ang + 2/3f) * MathHelper.TAU) * 0.2f + 0.8f;
                particle.setColor(red, green, blue);
                particle.setVelocity(g, h, i);
            }
            return particle;
        }
    }

    public static class GlimmerSmokeFactory extends FireSmokeParticle.Factory {

        public GlimmerSmokeFactory(SpriteProvider spriteProvider) {
            super(spriteProvider);
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            Particle particle = super.createParticle(simpleParticleType, clientWorld, d, e, f, g, h, i);
            if(particle != null) {
                float ang = clientWorld.random.nextFloat();
                float baseBrightness = 0.6f + 0.2f * clientWorld.random.nextFloat();
                float rgbBrightness = (1 - baseBrightness);
                float red = MathHelper.sin(ang * MathHelper.TAU) * rgbBrightness + baseBrightness;
                float green = MathHelper.sin((ang + 1/3f) * MathHelper.TAU) * rgbBrightness + baseBrightness;
                float blue = MathHelper.sin((ang + 2/3f) * MathHelper.TAU) * rgbBrightness + baseBrightness;
                particle.setColor(red, green, blue);
                particle.setVelocity(g, h, i);
            }
            return particle;
        }
    }

    public static class LargeGlimmerSmokeFactory extends LargeFireSmokeParticle.Factory {

        public LargeGlimmerSmokeFactory(SpriteProvider spriteProvider) {
            super(spriteProvider);
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            Particle particle = super.createParticle(simpleParticleType, clientWorld, d, e, f, g, h, i);
            if(particle != null) {
                float ang = clientWorld.random.nextFloat();
                float baseBrightness = 0.6f + 0.2f * clientWorld.random.nextFloat();
                float rgbBrightness = (1 - baseBrightness);
                float red = MathHelper.sin(ang * MathHelper.TAU) * rgbBrightness + baseBrightness;
                float green = MathHelper.sin((ang + 1/3f) * MathHelper.TAU) * rgbBrightness + baseBrightness;
                float blue = MathHelper.sin((ang + 2/3f) * MathHelper.TAU) * rgbBrightness + baseBrightness;
                particle.setColor(red, green, blue);
                particle.setVelocity(g, h, i);
            }
            return particle;
        }
    }
}
