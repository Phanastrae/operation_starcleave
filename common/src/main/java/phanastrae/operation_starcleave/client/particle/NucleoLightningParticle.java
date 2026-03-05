package phanastrae.operation_starcleave.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class NucleoLightningParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    private final double xDir;
    private final double yDir;
    private final double zDir;

    protected NucleoLightningParticle(
            ClientLevel level,
            double x,
            double y,
            double z,
            double xSpeed,
            double ySpeed,
            double zSpeed,
            SpriteSet sprites
    ) {
        super(level, x, y, z, 0.0, 0.0, 0.0);
        this.sprites = sprites;

        this.friction = 0.96F;
        this.gravity = 0.0F;
        this.speedUpWhenYMotionIsBlocked = true;

        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.xDir = xSpeed + random.nextGaussian() * 0.01;
        this.yDir = ySpeed + random.nextGaussian() * 0.01;
        this.zDir = zSpeed + random.nextGaussian() * 0.01;
        double speed = Math.sqrt(xSpeed * xSpeed + ySpeed * ySpeed + zSpeed * zSpeed);

        this.quadSize *= 8.0F * (float)speed;
        this.lifetime = (int) (33.0F / Mth.randomBetween(this.random, 0.75F, 1.0F));
        this.lifetime = Math.max(this.lifetime, 1);

        this.setSpriteFromAge(sprites);

        this.roll = this.random.nextFloat() * (float)Math.TAU;
        this.oRoll = this.roll;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public int getLightColor(float partialTick) {
        return 240;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        Vector3f originalLightningPointyVec = new Vector3f(1, 0, 0);
        Vector3f movementVec = new Vector3f((float) this.xDir, (float) this.yDir, (float) this.zDir).normalize();

        for(int i = 0; i < 4; i++) {

            Quaternionf quaternion = new Quaternionf();
            quaternion.rotateAxis(this.roll + (float)Math.TAU * i / 4, movementVec);
            quaternion.rotateTo(originalLightningPointyVec, movementVec);

            this.renderRotatedQuad(buffer, renderInfo, quaternion, partialTicks);
        }
    }

    @Override
    protected void renderRotatedQuad(VertexConsumer buffer, Quaternionf quaternion, float x, float y, float z, float partialTicks) {
        float quadSize = this.getQuadSize(partialTicks);
        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();
        int lightColor = this.getLightColor(partialTicks);

        // use different vertex positions to original function
        this.renderVertex(buffer, quaternion, x, y, z, 2.0F, -1.0F, quadSize, u1, v1, lightColor);
        this.renderVertex(buffer, quaternion, x, y, z, 2.0F, 1.0F, quadSize, u1, v0, lightColor);
        this.renderVertex(buffer, quaternion, x, y, z, 0.0F, 1.0F, quadSize, u0, v0, lightColor);
        this.renderVertex(buffer, quaternion, x, y, z, 0.0F, -1.0F, quadSize, u0, v1, lightColor);
    }

    // original function is private, this is just a copy
    private void renderVertex(
            VertexConsumer buffer,
            Quaternionf quaternion,
            float x,
            float y,
            float z,
            float xOffset,
            float yOffset,
            float quadSize,
            float u,
            float v,
            int packedLight
    ) {
        Vector3f vector3f = new Vector3f(xOffset, yOffset, 0.0F).rotate(quaternion).mul(quadSize).add(x, y, z);
        buffer.addVertex(vector3f.x(), vector3f.y(), vector3f.z())
                .setUv(u, v)
                .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
                .setLight(packedLight);
    }

    @Override
    public float getQuadSize(float scaleFactor) {
        float ageFraction = (this.age + scaleFactor) / this.lifetime;
        return this.quadSize * ageFraction;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(
                SimpleParticleType type,
                ClientLevel level,
                double x,
                double y,
                double z,
                double xSpeed,
                double ySpeed,
                double zSpeed
        ) {
            return new NucleoLightningParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
