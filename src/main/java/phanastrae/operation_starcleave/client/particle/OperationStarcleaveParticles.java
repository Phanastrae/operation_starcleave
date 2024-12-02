package phanastrae.operation_starcleave.client.particle;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;

public class OperationStarcleaveParticles {

    public static void init() {
        ParticleFactoryRegistry.getInstance().register(OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER, FirmamentGlimmerFactory::new);
        ParticleFactoryRegistry.getInstance().register(OperationStarcleaveParticleTypes.GLIMMER_SMOKE, GlimmerSmokeFactory::new);
        ParticleFactoryRegistry.getInstance().register(OperationStarcleaveParticleTypes.LARGE_GLIMMER_SMOKE, LargeGlimmerSmokeFactory::new);
    }

    public static class FirmamentGlimmerFactory extends SuspendedParticle.UnderwaterProvider {

        public FirmamentGlimmerFactory(SpriteSet spriteProvider) {
            super(spriteProvider);
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
            Particle particle = super.createParticle(simpleParticleType, clientWorld, d, e, f, g, h, i);
            if(particle != null) {
                float ang = clientWorld.random.nextFloat();
                float red = Mth.sin(ang * Mth.TWO_PI) * 0.2f + 0.8f;
                float green = Mth.sin((ang + 1/3f) * Mth.TWO_PI) * 0.2f + 0.8f;
                float blue = Mth.sin((ang + 2/3f) * Mth.TWO_PI) * 0.2f + 0.8f;
                particle.setColor(red, green, blue);
                particle.setParticleSpeed(g, h, i);
            }
            return particle;
        }
    }

    public static class GlimmerSmokeFactory extends SmokeParticle.Provider {

        public GlimmerSmokeFactory(SpriteSet spriteProvider) {
            super(spriteProvider);
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
            Particle particle = super.createParticle(simpleParticleType, clientWorld, d, e, f, g, h, i);
            if(particle != null) {
                float ang = clientWorld.random.nextFloat();
                float baseBrightness = 0.6f + 0.2f * clientWorld.random.nextFloat();
                float rgbBrightness = (1 - baseBrightness);
                float red = Mth.sin(ang * Mth.TWO_PI) * rgbBrightness + baseBrightness;
                float green = Mth.sin((ang + 1/3f) * Mth.TWO_PI) * rgbBrightness + baseBrightness;
                float blue = Mth.sin((ang + 2/3f) * Mth.TWO_PI) * rgbBrightness + baseBrightness;
                particle.setColor(red, green, blue);
                particle.setParticleSpeed(g, h, i);
            }
            return particle;
        }
    }

    public static class LargeGlimmerSmokeFactory extends LargeSmokeParticle.Provider {

        public LargeGlimmerSmokeFactory(SpriteSet spriteProvider) {
            super(spriteProvider);
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
            Particle particle = super.createParticle(simpleParticleType, clientWorld, d, e, f, g, h, i);
            if(particle != null) {
                float ang = clientWorld.random.nextFloat();
                float baseBrightness = 0.6f + 0.2f * clientWorld.random.nextFloat();
                float rgbBrightness = (1 - baseBrightness);
                float red = Mth.sin(ang * Mth.TWO_PI) * rgbBrightness + baseBrightness;
                float green = Mth.sin((ang + 1/3f) * Mth.TWO_PI) * rgbBrightness + baseBrightness;
                float blue = Mth.sin((ang + 2/3f) * Mth.TWO_PI) * rgbBrightness + baseBrightness;
                particle.setColor(red, green, blue);
                particle.setParticleSpeed(g, h, i);
            }
            return particle;
        }
    }
}
