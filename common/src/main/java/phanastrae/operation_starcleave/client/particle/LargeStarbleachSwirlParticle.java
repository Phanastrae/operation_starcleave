package phanastrae.operation_starcleave.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class LargeStarbleachSwirlParticle extends StarbleachSwirlParticle {

    public LargeStarbleachSwirlParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        this.quadSize *= 2.5F;
        this.lifetime *= (int) 2.5;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet sprites) {
            this.sprite = sprites;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            Particle particle = new LargeStarbleachSwirlParticle(clientLevel, x, y, z, xSpeed, ySpeed, zSpeed, this.sprite);
            float baseBrightness = 0.75f + 0.2f * clientLevel.random.nextFloat();
            OperationStarcleaveParticles.giveParticleRainbowColor(particle, clientLevel, baseBrightness, 1 - baseBrightness);
            particle.setParticleSpeed(xSpeed, ySpeed, zSpeed);
            return particle;
        }
    }
}
