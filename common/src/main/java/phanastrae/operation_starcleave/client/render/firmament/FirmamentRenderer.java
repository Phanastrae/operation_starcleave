package phanastrae.operation_starcleave.client.render.firmament;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.client.duck.LevelRendererDuck;
import phanastrae.operation_starcleave.client.render.OperationStarcleaveRenderLayers;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.FirmamentSubRegion;
import phanastrae.operation_starcleave.world.firmament.RegionPos;

import java.lang.Math;

import static com.mojang.blaze3d.platform.GlConst.*;

public class FirmamentRenderer {

    @Nullable
    private static VertexBuffer STARS_BUFFER;
    @Nullable
    private static VertexBuffer LIGHT_SKY_BUFFER;

    public static void close() {
        closeIfNotNull(STARS_BUFFER);
        closeIfNotNull(LIGHT_SKY_BUFFER);
    }

    private static void closeIfNotNull(VertexBuffer vertexBuffer) {
        if(vertexBuffer != null) {
            vertexBuffer.close();
        }
    }

    private static void createStars() {
        closeIfNotNull(STARS_BUFFER);

        STARS_BUFFER = new VertexBuffer(VertexBuffer.Usage.STATIC);
        STARS_BUFFER.bind();
        STARS_BUFFER.upload(createStars(Tesselator.getInstance()));
        VertexBuffer.unbind();
    }

    private static void createLightSky() {
        closeIfNotNull(LIGHT_SKY_BUFFER);

        LIGHT_SKY_BUFFER = new VertexBuffer(VertexBuffer.Usage.STATIC);
        LIGHT_SKY_BUFFER.bind();
        LIGHT_SKY_BUFFER.upload(createLightSky(Tesselator.getInstance(), 16.0F));
        VertexBuffer.unbind();
    }

    private static MeshData createStars(Tesselator tessellator) {
        RandomSource random = RandomSource.create(1025);
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        int STAR_COUNT = 6120;
        for(int j = 0; j < STAR_COUNT; ++j) {
            float x = random.nextFloat() * 2.0F - 1.0F;
            float y = random.nextFloat() * 2.0F - 1.0F;
            float z = random.nextFloat() * 2.0F - 1.0F;
            float m = Mth.lengthSquared(x, y, z);
            if (!(m <= 0.010000001F) && !(m >= 1.0F)) {
                Vector3f vector3f = new Vector3f(x, y, z).normalize(100.0F);
                float zAngle = (float)(random.nextDouble() * (float) Math.PI * 2.0);
                Quaternionf quaternionf = new Quaternionf().rotateTo(new Vector3f(0.0F, 0.0F, -1.0F), vector3f).rotateZ(zAngle);

                float phase = random.nextFloat();
                float red = 0.5F + 0.3F * (float)Math.sin(Mth.TWO_PI * phase);
                float green = 0.5F + 0.3F * (float)Math.sin(Mth.TWO_PI * (phase + 1/3F));
                float blue = 0.5F + 0.3F * (float)Math.sin(Mth.TWO_PI * (phase - 1/3F));

                float l = 0.15F + random.nextFloat() * 0.1F;
                bufferBuilder.addVertex(vector3f.add(new Vector3f(l, -l, 0.0F).rotate(quaternionf))).setColor(red, green, blue, 1);
                bufferBuilder.addVertex(vector3f.add(new Vector3f(l, l, 0.0F).rotate(quaternionf))).setColor(red, green, blue, 1);
                bufferBuilder.addVertex(vector3f.add(new Vector3f(-l, l, 0.0F).rotate(quaternionf))).setColor(red, green, blue, 1);
                bufferBuilder.addVertex(vector3f.add(new Vector3f(-l, -l, 0.0F).rotate(quaternionf))).setColor(red, green, blue, 1);
            }
        }

        return bufferBuilder.buildOrThrow();
    }

    private static MeshData createLightSky(Tesselator tessellator, float f) {
        float g = Math.signum(f) * 512.0F;
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
        bufferBuilder.addVertex(0.0F, f, 0.0F);

        for(int i = -180; i <= 180; i += 45) {
            bufferBuilder.addVertex(g * Mth.cos((float)i * (float) (Math.PI / 180.0)), f, 512.0F * Mth.sin((float)i * (float) (Math.PI / 180.0)));
        }

        return bufferBuilder.buildOrThrow();
    }

    public static void render(Level level, MultiBufferSource vertexConsumerProvider, PoseStack WRCmatrixStack, Camera camera, Frustum frustum, LevelRenderer levelRenderer, PoseStack matrixStack, Matrix4f projectionMatrix) {
        if(frustum == null || camera == null) return;

        Firmament firmament = Firmament.fromLevel(level);
        if(firmament == null) return;

        double camx = camera.getPosition().x;
        double camz = camera.getPosition().z;
        double firmHeight = firmament.getY();
        AABB box = new AABB(camx - 512, firmHeight - 1, camz - 512, camx + 512, firmHeight + 1, camz + 512);
        if(!frustum.isVisible(box)) {
            return;
        }

        Minecraft client = Minecraft.getInstance();
        ProfilerFiller profiler = client.getProfiler();
        /*
        Player player = client.player;
        boolean debugMode_General = client.getDebugOverlay().showDebugScreen() && player != null && player.getMainHandItem().is(OperationStarcleaveItems.FIRMAMENT_MANIPULATOR);
        if(debugMode_General) {
            profiler.push("starcleave_firmament");
            profiler.push("debug");
            // TODO serverside firmament regions broke most of debug, either fix or remove this at some point
            // TODO apparently i'm passing the wrong matrix and it crashes, i'm just disabling this for now
            //doRender(level, vertexConsumerProvider, WRCmatrixStack, camera, debugMode_General);
            profiler.pop();
            profiler.pop();
            return;
        }
        */

        profiler.push("starcleave_firmament");
        profiler.push("check");
        boolean renderSkybox = FirmamentTextureStorage.getInstance().isAnyFilledAndActive();
        if(renderSkybox) {
            profiler.popPush("sky");
            RenderTarget firmamentFrameBuffer = ((LevelRendererDuck)levelRenderer).operation_starcleave$getFirmamentFramebuffer();
            firmamentFrameBuffer.setClearColor(0f, 0.08f, 0.08f, 1f);
            firmamentFrameBuffer.clear(Minecraft.ON_OSX);

            OperationStarcleaveRenderLayers.FIRMAMENT_SKY_TARGET.setupRenderState();
            renderFirmamentSky(matrixStack, projectionMatrix);
            OperationStarcleaveRenderLayers.FIRMAMENT_SKY_TARGET.clearRenderState();

            profiler.popPush("fracture");
            renderBakedSubRegions(matrixStack, frustum, levelRenderer, level, camera);
        }
        profiler.pop();
        profiler.pop();
    }

    public static void renderFirmamentSky(PoseStack matrices, Matrix4f projectionMatrix) {
        if(STARS_BUFFER == null) {
            createStars();
        }
        if(LIGHT_SKY_BUFFER == null) {
            createLightSky();
        }

        VertexBuffer vb1 = LIGHT_SKY_BUFFER;
        if(vb1 != null && !vb1.isInvalid()) {
            float[] fogColor = RenderSystem.getShaderFogColor();
            RenderSystem.setShaderFogColor(0f, 0.08f, 0.08f, 1f);

            matrices.pushPose();
            matrices.translate(0, 20, 0);

            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(
                    GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
            );

            RenderSystem.setShaderColor(0.15f, 0.12f, 0.08f, 1f);
            vb1.bind();
            vb1.drawWithShader(matrices.last().pose(), projectionMatrix, GameRenderer.getPositionShader());

            matrices.translate(0, -30, 0);
            RenderSystem.setShaderColor(0, 0.08f, 0.08f, 1f);
            vb1.drawWithShader(matrices.last().pose(), projectionMatrix, GameRenderer.getPositionShader());
            VertexBuffer.unbind();

            RenderSystem.setShaderColor(1, 1, 1, 1);

            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);

            matrices.popPose();

            RenderSystem.setShaderFogColor(fogColor[0], fogColor[1], fogColor[2], fogColor[3]);
        }

        float fogStart = RenderSystem.getShaderFogStart();
        RenderSystem.setShaderFogStart(Float.MAX_VALUE);

        float[] rs = new float[]{0.8f, 1f, 1f};
        float[] gs = new float[]{1f, 0.8f, 1f};
        float[] bs = new float[]{1f, 1f, 0.8f};

        VertexBuffer vb = STARS_BUFFER;
        if(vb != null && !vb.isInvalid()) {
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(
                    GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
            );

            for(int i = 0; i < 12; i++) {
                int j = i % 3;
                int k = i / 3;

                int n = 20000 * (k + 1);
                matrices.pushPose();
                float angle = ((System.currentTimeMillis() % n) / (float)(n) + i / 12f) * 2 * Mth.PI;
                matrices.translate(0, Mth.sin(angle) * 20 * k, 0);
                matrices.mulPose(new Quaternionf().rotateY(angle).rotateZ(Mth.sin(angle) * 0.2f * k));

                RenderSystem.setShaderColor(rs[j], gs[j], bs[j], 0.75f);
                vb.bind();
                vb.drawWithShader(matrices.last().pose(), projectionMatrix, GameRenderer.getPositionColorShader());

                matrices.popPose();
            }
            RenderSystem.setShaderColor(1, 1, 1, 1);
            VertexBuffer.unbind();

            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
        }

        RenderSystem.depthMask(false);
        Tesselator tessellator = Tesselator.getInstance();

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        );

        float k2 = 50.0F;
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        long time = System.currentTimeMillis();

        RenderSystem.setShaderColor(0.3f, 0.3f, 0.3f, 1);
        RenderSystem.setShaderTexture(0, OperationStarcleave.id("textures/environment/starry_eye_light.png"));
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        for(int i = 0; i < 7; i++) {
            matrices.pushPose();
            int n = 300000;
            float angle = (((time * 2) % n) / (float)n + (i / 7f)) * Mth.PI * 2;
            matrices.translate(0, -50, 0);
            matrices.mulPose(new Quaternionf().rotateY(-angle));
            matrices.translate(-17, 0, -17);
            matrices.mulPose(new Quaternionf().rotateX(0.35F));

            float fl = (time % 4000) / 4000f + (i / 7F);
            float red = Mth.sin(fl * Mth.TWO_PI) * 0.2f + 0.8f;
            float green = Mth.sin((fl + 1/3f) * Mth.TWO_PI) * 0.2f + 0.8f;
            float blue = Mth.sin((fl + 2/3f) * Mth.TWO_PI) * 0.2f + 0.8f;
            int col = FastColor.ARGB32.colorFromFloat(1F, red, green, blue);

            Matrix4f matrix4f2 = matrices.last().pose();
            bufferBuilder.addVertex(matrix4f2, -k2, 100.0F, -k2).setUv(0.0F, 0.0F).setColor(col);
            bufferBuilder.addVertex(matrix4f2, k2, 100.0F, -k2).setUv(1.0F, 0.0F).setColor(col);
            bufferBuilder.addVertex(matrix4f2, k2, 100.0F, k2).setUv(1.0F, 1.0F).setColor(col);
            bufferBuilder.addVertex(matrix4f2, -k2, 100.0F, k2).setUv(0.0F, 1.0F).setColor(col);
            matrices.popPose();
        }
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

        RenderSystem.setShaderColor(0.5f, 0.5f, 0.5f, 1);
        RenderSystem.setShaderTexture(0, OperationStarcleave.id("textures/environment/starry_eye.png"));
        bufferBuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        for(int i = 0; i < 7; i++) {
            matrices.pushPose();
            int n = 300000;
            float angle = ((time % n) / (float)n + (i / 7f)) * Mth.PI * 2;
            matrices.mulPose(new Quaternionf().rotateY(-angle));
            matrices.translate(13, 0, 0);

            float fl = (time % 4000) / 4000f + (i / 7F);
            float red = Mth.sin(fl * Mth.TWO_PI) * 0.2f + 0.8f;
            float green = Mth.sin((fl + 1/3f) * Mth.TWO_PI) * 0.2f + 0.8f;
            float blue = Mth.sin((fl + 2/3f) * Mth.TWO_PI) * 0.2f + 0.8f;
            int col = FastColor.ARGB32.colorFromFloat(1F, red, green, blue);

            Matrix4f matrix4f2 = matrices.last().pose();
            bufferBuilder.addVertex(matrix4f2, -k2, 100.0F, -k2).setUv(0.0F, 0.0F).setColor(col);
            bufferBuilder.addVertex(matrix4f2, k2, 100.0F, -k2).setUv(1.0F, 0.0F).setColor(col);
            bufferBuilder.addVertex(matrix4f2, k2, 100.0F, k2).setUv(1.0F, 1.0F).setColor(col);
            bufferBuilder.addVertex(matrix4f2, -k2, 100.0F, k2).setUv(0.0F, 1.0F).setColor(col);
            matrices.popPose();
        }
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

        RenderSystem.setShaderColor(1, 1, 1, 1);

        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();

        RenderSystem.setShaderFogStart(fogStart);
    }

    public static void doRender(Level level, MultiBufferSource vertexConsumerProvider, PoseStack matrixStack, Camera camera, boolean debugMode_General) {
        // TODO tidy up, this is mostly unused now
        Firmament firmament = Firmament.fromLevel(level);
        if(firmament == null) return;

        Minecraft client = Minecraft.getInstance();

        if(vertexConsumerProvider == null) return;
        Entity e = client.cameraEntity;
        if(e == null) return;

        boolean debugMode_Activity = client.getEntityRenderDispatcher().shouldRenderHitBoxes();

        VertexConsumer vertexConsumer = debugMode_General ? vertexConsumerProvider.getBuffer(RenderType.debugQuads()) : vertexConsumerProvider.getBuffer(OperationStarcleaveRenderLayers.getFracture());

        int ex = e.getBlockX();
        int ez = e.getBlockZ();

        Vec3 camPos = camera.getPosition();
        matrixStack.pushPose();
        matrixStack.translate(ex - camPos.x, - camPos.y, ez - camPos.z);

        int tileSize = FirmamentSubRegion.TILE_SIZE;
        RegionPos camRegionPos = RegionPos.fromWorldCoords(ex, ez);
        firmament.forEachRegion((firmamentRegion -> {
            RegionPos regionPos = RegionPos.fromWorldCoords(firmamentRegion.x, firmamentRegion.z);
            int drx = regionPos.rx - camRegionPos.rx;
            int drz = regionPos.rz - camRegionPos.rz;
            if(drx*drx > 1 || drz*drz > 1) {{
                // only render the 3x3 region are around the player
                return;
            }}
            int[][] damageArray = new int[3][3];
            firmamentRegion.forEachSubRegion((firmamentSubRegion -> {
                if(!firmamentSubRegion.hadDamageLastCheck()) {
                    return;
                }

                int srdx = firmamentSubRegion.x + 4 - ex;
                int srdz = firmamentSubRegion.z + 4 - ez;
                if(srdx*srdx + srdz*srdz > 512*512) {
                    return;
                }

                firmamentSubRegion.forEachPosition((x, z, onBorder) -> {
                    int worldX = x + firmamentSubRegion.x;
                    int worldZ = z + firmamentSubRegion.z;

                    float damage = Mth.clamp(0, 7, firmamentSubRegion.getDamage(x, z)) / 7f;
                    if(debugMode_General) {
                        float drip = Mth.clamp(0, 7, firmamentSubRegion.getDrip(x, z)) / 7f;

                        float displacementY = -firmamentSubRegion.getDisplacement(x, z);

                        boolean updated = debugMode_Activity && firmamentSubRegion.shouldUpdate();
                        float f = 0.125f * damage;

                        float r = Mth.clamp(0, 1, damage);
                        float g = Mth.clamp(0, 1, updated ? 1 : 0);
                        float b = Mth.clamp(0, 1, drip);

                        renderQuadDebug(matrixStack.last().pose(),
                                vertexConsumer,
                                worldX - ex + f, worldZ - ez + f,
                                worldX - ex + tileSize - f, worldZ - ez + tileSize - f,
                                e.level().getMaxBuildHeight() + 16 + displacementY,
                                r,
                                g,
                                b,
                                1f);
                    } else {
                        if(onBorder) {
                            for (int i = -1; i <= 1; i++) {
                                for (int j = -1; j <= 1; j++) {
                                    damageArray[i + 1][j + 1] = (firmament.getDamage(worldX + i * tileSize, worldZ + j * tileSize) * 15);
                                }
                            }
                        } else {
                            for (int i = -1; i <= 1; i++) {
                                for (int j = -1; j <= 1; j++) {
                                    damageArray[i + 1][j + 1] = (firmamentSubRegion.getDamage(x + i * tileSize, z + j * tileSize) * 15);
                                }
                            }
                        }

                        boolean dam = false;
                        for(int i = 0; i < 3 && !dam; i++) {
                            for(int j = 0; j < 3; j++) {
                                if(damageArray[i][j] != 0) {
                                    dam = true;
                                    break;
                                }
                            }
                        }
                        if(!dam) return;

                        int rbyte = (damageArray[0][0] & 0xF) | ((damageArray[0][1] & 0xF) << 4);
                        int gbyte = (damageArray[0][2] & 0xF) | ((damageArray[1][0] & 0xF) << 4);
                        int bbyte = (damageArray[1][1] & 0xF) | ((damageArray[1][2] & 0xF) << 4);
                        int abyte = (damageArray[2][0] & 0xF) | ((damageArray[2][1] & 0xF) << 4);
                        int lbyte = (damageArray[2][2] & 0xF);

                        renderQuadReal(matrixStack.last().pose(),
                                matrixStack.last().normal(),
                                vertexConsumer,
                                (worldX - ex), (worldZ - ez),
                                (worldX - ex) + tileSize, (worldZ - ez) + tileSize,
                                e.level().getMaxBuildHeight() + 16,
                                rbyte / 255f,
                                gbyte / 255f,
                                bbyte / 255f,
                                abyte / 255f,
                                0, 0, 1, 1,
                                lbyte, -1);
                    }
                });
            }));
        }));

        matrixStack.popPose();
    }

    public static void renderQuadDebug(Matrix4f positionMatrix, VertexConsumer vertexConsumer, float x1, float z1, float x2, float z2, float y, float r, float g, float b, float a) {
        vertexConsumer.addVertex(positionMatrix, x1, y, z2).setColor(r, g, b, a);
        vertexConsumer.addVertex(positionMatrix, x1, y, z1).setColor(r, g, b, a);
        vertexConsumer.addVertex(positionMatrix, x2, y, z1).setColor(r, g, b, a);
        vertexConsumer.addVertex(positionMatrix, x2, y, z2).setColor(r, g, b, a);
    }

    public static void renderQuadReal(Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertexConsumer, float x1, float z1, float x2, float z2, float y, float r, float g, float b, float a, float u1, float v1, float u2, float v2, int light, int ny) {
        Vector4f vec = new Vector4f(x1, y, z2, 1).mul(positionMatrix);
        Vector3f norm = new Vector3f(0, ny, 0).mul(normalMatrix);

        int color = FastColor.ARGB32.colorFromFloat(r, g, b, a);
        vertexConsumer.addVertex(vec.x, vec.y, vec.z, color, u1, v2, 0, light, norm.x, norm.y, norm.z);
        vec = new Vector4f(x1, y, z1, 1).mul(positionMatrix);
        vertexConsumer.addVertex(vec.x, vec.y, vec.z, color, u1, v1, 0, light, norm.x, norm.y, norm.z);
        vec = new Vector4f(x2, y, z1, 1).mul(positionMatrix);
        vertexConsumer.addVertex(vec.x, vec.y, vec.z, color, u2, v1, 0, light, norm.x, norm.y, norm.z);
        vec = new Vector4f(x2, y, z2, 1).mul(positionMatrix);
        vertexConsumer.addVertex(vec.x, vec.y, vec.z, color, u2, v2, 0, light, norm.x, norm.y, norm.z);
    }

    public static void renderBakedSubRegions(PoseStack matrices, Frustum frustum, LevelRenderer levelRenderer, Level level, Camera camera) {
        if(frustum == null) return;

        RenderTarget firmamentFrameBuffer = ((LevelRendererDuck)levelRenderer).operation_starcleave$getFirmamentFramebuffer();
        int currentTexID = RenderSystem.getShaderTexture(0);
        int firmamentSkyTexID = firmamentFrameBuffer.getColorTextureId();

        Firmament firmament = Firmament.fromLevel(level);
        if(firmament == null) return;
        int height = firmament.getY();

        Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
        RenderType renderLayer = OperationStarcleaveRenderLayers.getFracture();

        renderLayer.setupRenderState();
        modelViewStack.pushMatrix();
        modelViewStack.identity();
        modelViewStack.mul(matrices.last().pose());
        Vec3 camPos = camera.getPosition();

        ShaderInstance shaderProgram = RenderSystem.getShader();

        if(shaderProgram != null) {
            DynamicTexture firmamentTex = FirmamentTextureStorage.getInstance().getTexture();
            RenderSystem.setShaderTexture(0, firmamentTex.getId());
            firmamentTex.bind();
            RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            RenderSystem.setShaderTexture(1, firmamentSkyTexID);

            for(int m = 0; m < 2; ++m) {
                int n = RenderSystem.getShaderTexture(m);
                shaderProgram.setSampler("Sampler" + m, n);
            }

            if (shaderProgram.MODEL_VIEW_MATRIX != null) {
                shaderProgram.MODEL_VIEW_MATRIX.set(modelViewStack);
            }

            if (shaderProgram.PROJECTION_MATRIX != null) {
                shaderProgram.PROJECTION_MATRIX.set(RenderSystem.getProjectionMatrix());
            }

            if (shaderProgram.GAME_TIME != null) {
                shaderProgram.GAME_TIME.set(RenderSystem.getShaderGameTime());
            }

            if (shaderProgram.SCREEN_SIZE != null) {
                Window window = Minecraft.getInstance().getWindow();
                shaderProgram.SCREEN_SIZE.set((float)window.getWidth(), (float)window.getHeight());
            }

            shaderProgram.apply();

            Tesselator tessellator = Tesselator.getInstance();

            matrices.pushPose();

            Matrix4f matrix4f = matrices.last().pose();
            BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);

            RegionPos regionPos = RegionPos.fromWorldCoords(Mth.floor(camPos.x), Mth.floor(camPos.z));

            matrices.translate(regionPos.worldX - camPos.x, height + (1 / 16F) - camPos.y, regionPos.worldZ - camPos.z);
            for(int i = -1; i <= 1; i++) {
                for(int j = -1; j <= 1; j++) {
                    int ox = 512 * i;
                    int oz = 512 * j;
                    float u1 = ((regionPos.rx + i) % 4) / 4f;
                    float v1 = ((regionPos.rz + j) % 4) / 4f;
                    float u2 = u1 + 0.25f;
                    float v2 = v1 + 0.25f;
                    bufferBuilder.addVertex(matrix4f, ox, 0, oz).setColor(255, 255, 255, 255).setUv(u1, v1).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 0, 0);
                    bufferBuilder.addVertex(matrix4f, ox + 512, 0, oz).setColor(255, 255, 255, 255).setUv(u2, v1).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 0, 0);
                    bufferBuilder.addVertex(matrix4f, ox + 512, 0, oz + 512).setColor(255, 255, 255, 255).setUv(u2, v2).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 0, 0);
                    bufferBuilder.addVertex(matrix4f, ox, 0, oz + 512).setColor(255, 255, 255, 255).setUv(u1, v2).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 0, 0);

                    bufferBuilder.addVertex(matrix4f, ox, 0, oz + 512).setColor(255, 255, 255, 255).setUv(u1, v2).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 0, 0);
                    bufferBuilder.addVertex(matrix4f, ox + 512, 0, oz + 512).setColor(255, 255, 255, 255).setUv(u2, v2).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 0, 0);
                    bufferBuilder.addVertex(matrix4f, ox + 512, 0, oz).setColor(255, 255, 255, 255).setUv(u2, v1).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 0, 0);
                    bufferBuilder.addVertex(matrix4f, ox, 0, oz).setColor(255, 255, 255, 255).setUv(u1, v1).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 0, 0);
                }
            }
            BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
            matrices.popPose();




            VertexBuffer.unbind();

            shaderProgram.clear();
        }

        modelViewStack.popMatrix();
        renderLayer.clearRenderState();

        RenderSystem.setShaderTexture(0, currentTexID);
    }
}
