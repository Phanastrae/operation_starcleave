package phanastrae.operation_starcleave.client.render.firmament;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.*;
import phanastrae.operation_starcleave.client.duck.WorldRendererDuck;
import phanastrae.operation_starcleave.client.render.OperationStarcleaveRenderLayers;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;
import phanastrae.operation_starcleave.mixin.client.LevelRendererAccessor;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.FirmamentSubRegion;
import phanastrae.operation_starcleave.world.firmament.RegionPos;

public class FirmamentRenderer {
    public static void render(PoseStack matrixStack, WorldRenderContext worldRenderContext) {
        Frustum frustum = worldRenderContext.frustum();
        Camera camera = worldRenderContext.camera();
        Level world = worldRenderContext.world();
        if(frustum == null || camera == null) return;

        double camx = camera.getPosition().x;
        double camz = camera.getPosition().z;
        double firmHeight = world.getMaxBuildHeight() + 16;
        AABB box = new AABB(camx - 512, firmHeight - 1, camz - 512, camx + 512, firmHeight + 1, camz + 512);
        if(!frustum.isVisible(box)) {
            return;
        }

        Minecraft client = Minecraft.getInstance();
        ProfilerFiller profiler = client.getProfiler();
        Player player = client.player;
        boolean debugMode_General = client.getDebugOverlay().showDebugScreen() && player != null && player.getMainHandItem().is(OperationStarcleaveItems.FIRMAMENT_MANIPULATOR);

        if(debugMode_General) {
            profiler.push("starcleave_firmament");
            profiler.push("debug");
            // TODO serverside firmament regions broke most of debug, either fix or remove this at some point
            doRender(worldRenderContext, debugMode_General);
            profiler.pop();
            profiler.pop();
            return;
        }

        if(worldRenderContext.consumers() instanceof MultiBufferSource.BufferSource immediate) {
            immediate.endBatch();
            profiler.push("starcleave_firmament");
            profiler.push("check");

            boolean renderSkybox = false;
            for(int i = 0; i < 4 && !renderSkybox; i++) {
                for(int j = 0; j < 4 && !renderSkybox; j++) {
                    FirmamentTextureStorage fts = FirmamentTextureStorage.getInstance();
                    if(!fts.active[i][j] || !fts.filled[i][j]) continue;
                    renderSkybox = true;
                }
            }

            if(renderSkybox) {
                profiler.popPush("sky");
                RenderTarget firmamentFrameBuffer = ((WorldRendererDuck)worldRenderContext.worldRenderer()).operation_starcleave$getFirmamentFramebuffer();
                firmamentFrameBuffer.setClearColor(0, 0.08f, 0.08f, 1f);
                firmamentFrameBuffer.clear(Minecraft.ON_OSX);
                OperationStarcleaveRenderLayers.FIRMAMENT_SKY_TARGET.setupRenderState();
                renderFirmamentSky(matrixStack, worldRenderContext);
                immediate.endBatch();
                OperationStarcleaveRenderLayers.FIRMAMENT_SKY_TARGET.clearRenderState();

                profiler.popPush("fracture");
                renderBakedSubRegions(matrixStack, worldRenderContext);
            }
            profiler.pop();
            profiler.pop();
        }
    }

    public static void renderFirmamentSky(PoseStack matrices, WorldRenderContext worldRenderContext) {
        Matrix4f projectionMatrix = worldRenderContext.projectionMatrix();
        LevelRenderer worldRenderer = worldRenderContext.worldRenderer();
        LevelRendererAccessor levelRendererAccessor = (LevelRendererAccessor)worldRenderer;
        Level world = worldRenderContext.world();
        float tickDelta = worldRenderContext.tickCounter().getGameTimeDeltaPartialTick(false);

        float fogStart = RenderSystem.getShaderFogStart();
        RenderSystem.setShaderFogStart(Float.MAX_VALUE);

        VertexBuffer vb1 = levelRendererAccessor.getSkyBuffer();
        if(vb1 != null && !vb1.isInvalid()) {
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
        }

        float[] rs = new float[]{0.8f, 1f, 1f};
        float[] gs = new float[]{1f, 0.8f, 1f};
        float[] bs = new float[]{1f, 1f, 0.8f};

        VertexBuffer vb = levelRendererAccessor.getStarBuffer();
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
                vb.drawWithShader(matrices.last().pose(), projectionMatrix, GameRenderer.getPositionShader());

                matrices.popPose();
            }
            RenderSystem.setShaderColor(1, 1, 1, 1);
            VertexBuffer.unbind();

            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
        }

        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, ResourceLocation.parse("textures/environment/end_sky.png"));
        Tesselator tessellator = Tesselator.getInstance();

        /*
        for(int i = 0; i < 6; ++i) {
            matrices.push();
            if (i == 1) {
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
            }

            if (i == 2) {
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
            }

            if (i == 3) {
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
            }

            if (i == 4) {
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
            }

            if (i == 5) {
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-90.0F));
            }

            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).texture(0.0F, 0.0F).color(255, 255, 63, 255).next();
            bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).texture(0.0F, 16.0F).color(255, 255, 63, 255).next();
            bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).texture(16.0F, 16.0F).color(255, 255, 63, 255).next();
            bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).texture(16.0F, 0.0F).color(255, 255, 63, 255).next();
            tessellator.draw();
            matrices.pop();
        }

         */

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        );

        float k2 = 50.0F;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, ResourceLocation.parse("textures/environment/sun.png"));
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        for(int i = 0; i < 7; i++) {
            matrices.pushPose();
            int n = 300000;
            float angle = ((System.currentTimeMillis() % n) / (float)n + (i / 7f)) * Mth.PI * 2;
            matrices.mulPose(new Quaternionf().rotateY(-angle));
            matrices.translate(13, 0, 0);

            Matrix4f matrix4f2 = matrices.last().pose();
            bufferBuilder.addVertex(matrix4f2, -k2, 100.0F, -k2).setUv(0.0F, 0.0F);
            bufferBuilder.addVertex(matrix4f2, k2, 100.0F, -k2).setUv(1.0F, 0.0F);
            bufferBuilder.addVertex(matrix4f2, k2, 100.0F, k2).setUv(1.0F, 1.0F);
            bufferBuilder.addVertex(matrix4f2, -k2, 100.0F, k2).setUv(0.0F, 1.0F);
            matrices.popPose();
        }

        RenderSystem.setShaderColor(0.5f, 0.5f, 0.5f, 1);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
        RenderSystem.setShaderColor(1, 1, 1, 1);

        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();

        RenderSystem.setShaderFogStart(fogStart);
    }

    public static void doRender(WorldRenderContext worldRenderContext, boolean debugMode_General) {
        // TODO tidy up, this is mostly unused now
        Firmament firmament = Firmament.fromWorld(worldRenderContext.world());
        if(firmament == null) return;

        Minecraft client = Minecraft.getInstance();

        MultiBufferSource vertexConsumerProvider = worldRenderContext.consumers();
        if(vertexConsumerProvider == null) return;
        Entity e = client.cameraEntity;
        if(e == null) return;

        boolean debugMode_Activity = client.getEntityRenderDispatcher().shouldRenderHitBoxes();

        PoseStack matrixStack = worldRenderContext.matrixStack();
        VertexConsumer vertexConsumer = debugMode_General ? vertexConsumerProvider.getBuffer(RenderType.debugQuads()) : vertexConsumerProvider.getBuffer(OperationStarcleaveRenderLayers.getFracture());

        int ex = e.getBlockX();
        int ez = e.getBlockZ();

        Vec3 camPos = worldRenderContext.camera().getPosition();
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

    public static void renderBakedSubRegions(PoseStack matrices, WorldRenderContext worldRenderContext) {
        Frustum frustum = worldRenderContext.frustum();
        if(frustum == null) return;

        RenderTarget firmamentFrameBuffer = ((WorldRendererDuck)worldRenderContext.worldRenderer()).operation_starcleave$getFirmamentFramebuffer();
        int currentTexID = RenderSystem.getShaderTexture(0);
        int firmamentSkyTexID = firmamentFrameBuffer.getColorTextureId();

        Firmament firmament = Firmament.fromWorld(worldRenderContext.world());
        if(firmament == null) return;
        int height = firmament.getY();

        Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
        RenderType renderLayer = OperationStarcleaveRenderLayers.getFracture();

        renderLayer.setupRenderState();
        modelViewStack.pushMatrix();
        modelViewStack.identity();
        modelViewStack.mul(matrices.last().pose());
        Vec3 camPos = worldRenderContext.camera().getPosition();

        ShaderInstance shaderProgram = RenderSystem.getShader();

        if(shaderProgram != null) {
            int id = FirmamentTextureStorage.getInstance().texture.getId();
            RenderSystem.setShaderTexture(0, id);

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
            Uniform glUniform = shaderProgram.getUniform("ActiveRegions");
            if(glUniform != null) {
                float[] activeRegions = FirmamentTextureStorage.getInstance().getActiveRegions();
                glUniform.set(activeRegions);
            }

            Tesselator tessellator = Tesselator.getInstance();

            matrices.pushPose();

            Matrix4f matrix4f = matrices.last().pose();
            BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);

            RegionPos regionPos = RegionPos.fromWorldCoords(Mth.floor(camPos.x), Mth.floor(camPos.z));

            matrices.translate(regionPos.worldX-camPos.x, height-camPos.y, regionPos.worldZ-camPos.z);
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
