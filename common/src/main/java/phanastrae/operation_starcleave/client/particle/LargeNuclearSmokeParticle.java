package phanastrae.operation_starcleave.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.LargeSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class LargeNuclearSmokeParticle extends LargeSmokeParticle {

    protected LargeNuclearSmokeParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);

        this.hasPhysics = false;
    }


    public static class LargeNuclearSmokeFactory extends LargeSmokeParticle.Provider {
        private SpriteSet sprites;

        public LargeNuclearSmokeFactory(SpriteSet spriteProvider) {
            super(spriteProvider);
            this.sprites = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            Particle particle = new LargeNuclearSmokeParticle(clientLevel, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);

            float l = clientLevel.random.nextFloat();
            float red = Mth.lerp(l * l, 14, 127) / 255F;
            float green = Mth.lerp(l * l, 56, 212) / 255F;
            float blue = Mth.lerp(l * l, 25, 36) / 255F;
            particle.setColor(red, green, blue);

            particle.setParticleSpeed(xSpeed, ySpeed + 0.28, zSpeed);
            particle.scale(5.2F);
            particle.setLifetime(Mth.ceil(particle.getLifetime() * (3 + 3 * clientLevel.getRandom().nextFloat())));

            return particle;
        }
    }
}
