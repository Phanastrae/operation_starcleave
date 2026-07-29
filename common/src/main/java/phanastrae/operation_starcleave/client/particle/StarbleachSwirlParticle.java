package phanastrae.operation_starcleave.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class StarbleachSwirlParticle extends TextureSheetParticle {
    // similar to SpellParticle, but without the player distance transparency stuff
    private static final RandomSource RANDOM = RandomSource.create();
    private final SpriteSet sprites;

    public StarbleachSwirlParticle(
            ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites
    ) {
        super(level, x, y, z, 0.5 - RANDOM.nextDouble(), ySpeed, 0.5 - RANDOM.nextDouble());
        this.friction = 0.96F;
        this.gravity = -0.1F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.sprites = sprites;
        this.yd *= 0.2F;
        if (xSpeed == 0.0 && zSpeed == 0.0) {
            this.xd *= 0.1F;
            this.zd *= 0.1F;
        }

        this.quadSize *= 0.75F;
        this.lifetime = (int) (8.0 / (Math.random() * 0.8 + 0.2));
        this.hasPhysics = false;
        this.setSpriteFromAge(sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet sprites) {
            this.sprite = sprites;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            Particle particle = new StarbleachSwirlParticle(clientLevel, x, y, z, xSpeed, ySpeed, zSpeed, this.sprite);
            float baseBrightness = 0.75f + 0.2f * clientLevel.random.nextFloat();
            OperationStarcleaveParticles.giveParticleRainbowColor(particle, clientLevel, baseBrightness, 1 - baseBrightness);
            particle.setParticleSpeed(xSpeed, ySpeed, zSpeed);
            return particle;
        }
    }
}
