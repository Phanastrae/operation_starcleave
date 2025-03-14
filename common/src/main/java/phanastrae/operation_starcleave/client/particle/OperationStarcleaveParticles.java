package phanastrae.operation_starcleave.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;

public class OperationStarcleaveParticles {

    public static void init(ClientParticleRegistrar r) {
        r.register(OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER, FirmamentGlimmerFactory::new);
        r.register(OperationStarcleaveParticleTypes.GLIMMER_SMOKE, GlimmerSmokeFactory::new);
        r.register(OperationStarcleaveParticleTypes.LARGE_GLIMMER_SMOKE, LargeGlimmerSmokeFactory::new);
        r.register(OperationStarcleaveParticleTypes.PLASMA_DUST, PlasmaDustFactory::new);
        r.register(OperationStarcleaveParticleTypes.NUCLEAR_SMOKE, NuclearSmokeFactory::new);
        r.register(OperationStarcleaveParticleTypes.LARGE_NUCLEAR_SMOKE, LargeNuclearSmokeFactory::new);
    }

    public static class FirmamentGlimmerFactory extends SuspendedParticle.UnderwaterProvider {

        public FirmamentGlimmerFactory(SpriteSet spriteProvider) {
            super(spriteProvider);
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            Particle particle = super.createParticle(simpleParticleType, clientLevel, d, e, f, g, h, i);
            if(particle != null) {
                float ang = clientLevel.random.nextFloat();
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

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            Particle particle = super.createParticle(simpleParticleType, clientLevel, d, e, f, g, h, i);
            if(particle != null) {
                float ang = clientLevel.random.nextFloat();
                float baseBrightness = 0.6f + 0.2f * clientLevel.random.nextFloat();
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

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            Particle particle = super.createParticle(simpleParticleType, clientLevel, d, e, f, g, h, i);
            if(particle != null) {
                float ang = clientLevel.random.nextFloat();
                float baseBrightness = 0.6f + 0.2f * clientLevel.random.nextFloat();
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

    public static class PlasmaDustFactory extends SuspendedParticle.UnderwaterProvider {

        public PlasmaDustFactory(SpriteSet spriteProvider) {
            super(spriteProvider);
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            Particle particle = super.createParticle(simpleParticleType, clientLevel, d, e, f, g, h, i);
            if(particle != null) {
                float l = clientLevel.random.nextFloat();
                float red = Mth.lerp(l, 111, 204) / 255F;
                float green = Mth.lerp(l, 186, 240) / 255F;
                float blue = Mth.lerp(l, 26, 60) / 255F;
                particle.setColor(red, green, blue);
                particle.setParticleSpeed(g, h, i);
            }
            return particle;
        }
    }

    public static class NuclearSmokeFactory extends LargeSmokeParticle.Provider {

        public NuclearSmokeFactory(SpriteSet spriteProvider) {
            super(spriteProvider);
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            Particle particle = super.createParticle(simpleParticleType, clientLevel, d, e, f, g, h, i);
            if(particle != null) {
                float l = clientLevel.random.nextFloat();
                float red = Mth.lerp(l * l, 14, 127) / 255F;
                float green = Mth.lerp(l * l, 56, 212) / 255F;
                float blue = Mth.lerp(l * l, 25, 36) / 255F;
                particle.setColor(red, green, blue);
                particle.setParticleSpeed(g, h, i);
            }
            return particle;
        }
    }

    public static class LargeNuclearSmokeFactory extends LargeSmokeParticle.Provider {

        public LargeNuclearSmokeFactory(SpriteSet spriteProvider) {
            super(spriteProvider);
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            Particle particle = super.createParticle(simpleParticleType, clientLevel, d, e, f, g, h, i);
            if(particle != null) {
                float l = clientLevel.random.nextFloat();
                float red = Mth.lerp(l * l, 14, 127) / 255F;
                float green = Mth.lerp(l * l, 56, 212) / 255F;
                float blue = Mth.lerp(l * l, 25, 36) / 255F;
                particle.setColor(red, green, blue);
                particle.setParticleSpeed(g, h + 0.28, i);
                particle.scale(5.2F);
                particle.setLifetime(particle.getLifetime() * 6);
            }
            return particle;
        }
    }

    @FunctionalInterface
    public interface ClientParticleRegistrar {
        <T extends ParticleOptions> void register(ParticleType<T> type, ParticleRegistration<T> registration);
    }

    @FunctionalInterface
    public interface ParticleRegistration<T extends ParticleOptions> {
        ParticleProvider<T> create(SpriteSet sprites);
    }
}
