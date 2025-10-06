package phanastrae.operation_starcleave.client.render.firmament;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import phanastrae.operation_starcleave.OperationStarcleave;

public class FirmamentSkyRenderer {

    public static final int STAR_COLORS = 3;
    public static final int STAR_SIZES = 4;
    public static final int STAR_ITERATIONS = STAR_COLORS * STAR_SIZES;
    public static final int STAR_COUNT = 6120;
    public static final float[] STAR_REDS = new float[]{0.8f, 1f, 1f};
    public static final float[] STAR_GREENS = new float[]{1f, 0.8f, 1f};
    public static final float[] STAR_BLUES = new float[]{1f, 1f, 0.8f};
    public static final int BASE_STAR_PERIOD_MILLIS = 20000;
    public static int DIVINITY_CONSTANT = 7;
    public static final float STARRY_EYE_QUAD_RADIUS = 50.0F;
    public static final ResourceLocation STARRY_EYE_LIGHT = OperationStarcleave.id("textures/environment/starry_eye_light.png");
    public static final ResourceLocation STARRY_EYE_BODY = OperationStarcleave.id("textures/environment/starry_eye.png");
    public static final int STARRY_EYE_LIGHT_PERIOD_MILLIS = 150000;
    public static final int STARRY_EYE_BODY_PERIOD_MILLIS = 300000;
    public static final int STARRY_EYE_COLOR_PERIOD_MILLIS = 4000;

    private static final FirmamentSkyRenderer INSTANCE = new FirmamentSkyRenderer();

    private FirmamentSkyRenderer() {
    }

    public static FirmamentSkyRenderer getInstance() {
        return INSTANCE;
    }

    @Nullable
    private VertexBuffer starsBuffer;
    @Nullable
    private VertexBuffer lightSkyBuffer;

    public void close() {
        closeIfNotNull(this.starsBuffer);
        closeIfNotNull(this.lightSkyBuffer);
    }

    private static void closeIfNotNull(VertexBuffer vertexBuffer) {
        if (vertexBuffer != null) {
            vertexBuffer.close();
        }
    }

    private void createStars() {
        closeIfNotNull(this.starsBuffer);

        this.starsBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        this.starsBuffer.bind();
        this.starsBuffer.upload(createStarsData(Tesselator.getInstance()));
        VertexBuffer.unbind();
    }

    private void createLightSky() {
        closeIfNotNull(this.lightSkyBuffer);

        this.lightSkyBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        this.lightSkyBuffer.bind();
        this.lightSkyBuffer.upload(createLightSkyData(Tesselator.getInstance(), 16.0F));
        VertexBuffer.unbind();
    }

    private static MeshData createStarsData(Tesselator tessellator) {
        RandomSource random = RandomSource.create(1025);
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        Vector3f negZVec = new Vector3f(0.0F, 0.0F, -1.0F);

        for (int i = 0; i < STAR_COUNT; i++) {
            float x = random.nextFloat() * 2.0F - 1.0F;
            float y = random.nextFloat() * 2.0F - 1.0F;
            float z = random.nextFloat() * 2.0F - 1.0F;
            float dist = Mth.lengthSquared(x, y, z);
            if (0.01F < dist && dist < 1.0F) {
                float rotation = (float) (random.nextDouble() * Mth.TWO_PI);
                float phase = random.nextFloat();
                float radius = 0.15F + random.nextFloat() * 0.1F;

                float red = getSinValue(phase, 0.3F, 0.5F);
                float green = getSinValue(phase + 1 / 3F, 0.3F, 0.5F);
                float blue = getSinValue(phase + 2 / 3F, 0.3F, 0.5F);

                Vector3f starPos = new Vector3f(x, y, z).normalize(100.0F);
                Quaternionf quaternionf = new Quaternionf().rotateTo(negZVec, starPos).rotateZ(rotation);

                addStarVertex(bufferBuilder, starPos, radius, -radius, quaternionf, red, green, blue);
                addStarVertex(bufferBuilder, starPos, radius, radius, quaternionf, red, green, blue);
                addStarVertex(bufferBuilder, starPos, -radius, radius, quaternionf, red, green, blue);
                addStarVertex(bufferBuilder, starPos, -radius, -radius, quaternionf, red, green, blue);
            }
        }

        return bufferBuilder.buildOrThrow();
    }

    private static void addStarVertex(BufferBuilder bufferBuilder, Vector3f starPos, float x, float y, Quaternionf quaternion, float red, float green, float blue) {
        bufferBuilder.addVertex(starPos.add(new Vector3f(x, y, 0.0F).rotate(quaternion))).setColor(red, green, blue, 1F);
    }

    private static MeshData createLightSkyData(Tesselator tessellator, float y) {
        float sign = Math.signum(y);
        float dist = 512.0F;
        float distWithSign = sign * dist;

        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);

        bufferBuilder.addVertex(0.0F, y, 0.0F);
        for (int deg = -180; deg <= 180; deg += 45) {
            float angle = (float) (deg * Math.PI / 180.0);

            bufferBuilder.addVertex(
                    Mth.cos(angle) * distWithSign,
                    y,
                    Mth.sin(angle) * dist
            );
        }

        return bufferBuilder.buildOrThrow();
    }

    public void renderFirmamentSky(PoseStack matrices, Matrix4f projectionMatrix, long timeMillis) {
        if (this.starsBuffer == null) {
            createStars();
        }
        if (this.lightSkyBuffer == null) {
            createLightSky();
        }

        this.renderLightSky(matrices, projectionMatrix);

        float fogStart = RenderSystem.getShaderFogStart();
        RenderSystem.setShaderFogStart(Float.MAX_VALUE);

        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        );

        this.renderStars(matrices, projectionMatrix, timeMillis);
        renderStarryEye(matrices, timeMillis);

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);

        RenderSystem.setShaderFogStart(fogStart);
    }

    private void renderLightSky(PoseStack matrices, Matrix4f projectionMatrix) {
        VertexBuffer vb = this.lightSkyBuffer;
        if (vb != null && !vb.isInvalid()) {
            float[] fogColor = RenderSystem.getShaderFogColor().clone(); // make sure to clone, since original will get modified
            RenderSystem.setShaderFogColor(0f, 0.08f, 0.08f, 1f);

            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(
                    GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
            );

            vb.bind();
            matrices.pushPose();

            // render yellowish sky
            matrices.translate(0, 20, 0);
            RenderSystem.setShaderColor(0.15f, 0.12f, 0.08f, 1f);
            vb.drawWithShader(matrices.last().pose(), projectionMatrix, GameRenderer.getPositionShader());

            // render cyanish sky
            matrices.translate(0, -30, 0);
            RenderSystem.setShaderColor(0, 0.08f, 0.08f, 1f);
            vb.drawWithShader(matrices.last().pose(), projectionMatrix, GameRenderer.getPositionShader());

            RenderSystem.setShaderColor(1, 1, 1, 1);

            matrices.popPose();
            VertexBuffer.unbind();

            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);

            RenderSystem.setShaderFogColor(fogColor[0], fogColor[1], fogColor[2], fogColor[3]);
        }
    }

    private void renderStars(PoseStack matrices, Matrix4f projectionMatrix, long timeMillis) {
        VertexBuffer vb = this.starsBuffer;
        if (vb != null && !vb.isInvalid()) {
            for (int i = 0; i < STAR_ITERATIONS; i++) {
                float progress = i / (float) STAR_ITERATIONS;

                int starColorId = i % STAR_COLORS; // takes values 0, 1, 2, then repeats
                int starScale = i / STAR_COLORS; // takes values 0, 1, 2, 3, each taken 3 times

                int periodMillis = BASE_STAR_PERIOD_MILLIS * (1 + starScale);
                float periodProgress = getPeriodProgress(timeMillis, periodMillis);

                float angle = (periodProgress + progress) * Mth.TWO_PI;
                float sinAngle = Mth.sin(angle);
                float verticalOffset = sinAngle * 20 * starScale;
                float secondaryAngle = sinAngle * 0.2f * starScale;

                matrices.pushPose();
                matrices.translate(0, verticalOffset, 0);
                matrices.mulPose(new Quaternionf().rotateY(angle).rotateZ(secondaryAngle));

                RenderSystem.setShaderColor(STAR_REDS[starColorId], STAR_GREENS[starColorId], STAR_BLUES[starColorId], 0.75f);
                vb.bind();
                vb.drawWithShader(matrices.last().pose(), projectionMatrix, GameRenderer.getPositionColorShader());

                matrices.popPose();
            }
            RenderSystem.setShaderColor(1, 1, 1, 1);
            VertexBuffer.unbind();
        }
    }

    private static void renderStarryEye(PoseStack matrices, long timeMillis) {
        Tesselator tessellator = Tesselator.getInstance();

        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        RenderSystem.setShaderColor(0.3f, 0.3f, 0.3f, 1);
        drawStarryEyeLight(matrices, tessellator, STARRY_EYE_QUAD_RADIUS, timeMillis);
        RenderSystem.setShaderColor(0.5f, 0.5f, 0.5f, 1);
        drawStarryEyeBody(matrices, tessellator, STARRY_EYE_QUAD_RADIUS, timeMillis);
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    private static void drawStarryEyeLight(PoseStack matrices, Tesselator tessellator, float quadRadius, long timeMillis) {
        RenderSystem.setShaderTexture(0, STARRY_EYE_LIGHT);
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        for (int i = 0; i < DIVINITY_CONSTANT; i++) {
            float progress = i / (float) DIVINITY_CONSTANT;

            float periodProgress = getPeriodProgress(timeMillis, STARRY_EYE_LIGHT_PERIOD_MILLIS);
            float angle = (periodProgress + progress) * Mth.TWO_PI;
            int col = getStarryEyeColor(timeMillis, progress);

            matrices.pushPose();

            matrices.translate(0, -50, 0);
            matrices.mulPose(new Quaternionf().rotateY(-angle));
            matrices.translate(-17, 0, -17);
            matrices.mulPose(new Quaternionf().rotateX(0.35F));

            drawQuad(matrices, bufferBuilder, quadRadius, col);

            matrices.popPose();
        }
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
    }

    private static void drawStarryEyeBody(PoseStack matrices, Tesselator tessellator, float quadRadius, long timeMillis) {
        RenderSystem.setShaderTexture(0, STARRY_EYE_BODY);
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        for (int i = 0; i < DIVINITY_CONSTANT; i++) {
            float progress = i / (float) DIVINITY_CONSTANT;

            float periodProgress = getPeriodProgress(timeMillis, STARRY_EYE_BODY_PERIOD_MILLIS);
            float angle = (periodProgress + progress) * Mth.TWO_PI;
            int col = getStarryEyeColor(timeMillis, progress);

            matrices.pushPose();

            matrices.mulPose(new Quaternionf().rotateY(-angle));
            matrices.translate(13, 0, 0);

            drawQuad(matrices, bufferBuilder, quadRadius, col);
            matrices.popPose();
        }
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
    }

    private static void drawQuad(PoseStack matrices, BufferBuilder bufferBuilder, float quadRadius, int col) {
        Matrix4f matrix = matrices.last().pose();
        bufferBuilder.addVertex(matrix, -quadRadius, 100.0F, -quadRadius).setUv(0.0F, 0.0F).setColor(col);
        bufferBuilder.addVertex(matrix, quadRadius, 100.0F, -quadRadius).setUv(1.0F, 0.0F).setColor(col);
        bufferBuilder.addVertex(matrix, quadRadius, 100.0F, quadRadius).setUv(1.0F, 1.0F).setColor(col);
        bufferBuilder.addVertex(matrix, -quadRadius, 100.0F, quadRadius).setUv(0.0F, 1.0F).setColor(col);
    }

    private static int getStarryEyeColor(long timeMillis, float progress) {
        float periodProgress = getPeriodProgress(timeMillis, STARRY_EYE_COLOR_PERIOD_MILLIS) + progress;

        float red = getStarryEyeColorValue(periodProgress);
        float green = getStarryEyeColorValue(periodProgress + 1 / 3F);
        float blue = getStarryEyeColorValue(periodProgress + 2 / 3F);

        return FastColor.ARGB32.colorFromFloat(1F, red, green, blue);
    }

    private static float getStarryEyeColorValue(float progress) {
        return getSinValue(progress, 0.2F, 0.8F);
    }

    private static float getSinValue(float progress, float colorStrength, float whiteStrength) {
        return Mth.sin(progress * Mth.TWO_PI) * colorStrength + whiteStrength;
    }

    private static float getPeriodProgress(long time, long period) {
        return (time % period) / (float) (period);
    }
}
