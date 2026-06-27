package phanastrae.operation_starcleave.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class StarbleachedLeafParticle extends TextureSheetParticle {
    private static final int INITIAL_LIFETIME = 300;
    private float rotSpeed;
    private final float particleRandom;
    private final float spinAcceleration;

    protected StarbleachedLeafParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet) {
        super(level, x, y, z);
        this.setSprite(spriteSet.get(this.random));

        this.rotSpeed = (float) Math.toRadians(this.random.nextBoolean() ? -15.0 : 15.0);
        this.particleRandom = this.random.nextFloat();
        this.spinAcceleration = (float) Math.toRadians(this.random.nextBoolean() ? -2.5 : 2.5);

        this.roll = this.random.nextFloat() * (float) Math.TAU;
        this.oRoll = this.roll;

        this.lifetime = INITIAL_LIFETIME;

        this.gravity = 1.25E-3F;

        float size = this.random.nextBoolean() ? 0.05F : 0.075F;
        this.quadSize = size;
        this.setSize(size, size);

        this.friction = 1.0F;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.lifetime-- <= 0) {
            this.remove();
        }

        if (!this.removed) {
            float age = (float) (INITIAL_LIFETIME - this.lifetime);
            float relativeAge = Math.min(age / INITIAL_LIFETIME, 1.0F);

            double xPush = Math.cos(Math.toRadians(this.particleRandom * 360.0F));
            double zPush = Math.sin(Math.toRadians(this.particleRandom * 360.0F));
            double horizontalAcceleration = 0.0015F * Math.pow(relativeAge, 1.25);

            this.xd += xPush * horizontalAcceleration;
            this.zd += zPush * horizontalAcceleration;
            this.yd -= this.gravity;

            this.rotSpeed += this.spinAcceleration / 20.0F;

            this.oRoll = this.roll;
            this.roll = this.roll + this.rotSpeed / 20.0F;

            this.move(this.xd, this.yd, this.zd);

            if (this.onGround || this.lifetime < INITIAL_LIFETIME - 1 && (this.xd == 0.0 || this.zd == 0.0)) {
                this.remove();
            }

            if (!this.removed) {
                this.xd *= this.friction;
                this.yd *= this.friction;
                this.zd *= this.friction;
            }
        }
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet sprites) {
            this.sprite = sprites;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new StarbleachedLeafParticle(level, x, y, z, this.sprite);
        }
    }
}
