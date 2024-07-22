package phanastrae.operation_starcleave.render.firmament;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import org.joml.Math;
import org.joml.*;
import phanastrae.operation_starcleave.duck.WorldRendererDuck;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;
import phanastrae.operation_starcleave.mixin.client.WorldRendererAccessor;
import phanastrae.operation_starcleave.render.OperationStarcleaveRenderLayers;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.FirmamentSubRegion;
import phanastrae.operation_starcleave.world.firmament.RegionPos;

public class FirmamentRenderer {
    public static void render(MatrixStack matrixStack, WorldRenderContext worldRenderContext) {
        Frustum frustum = worldRenderContext.frustum();
        Camera camera = worldRenderContext.camera();
        World world = worldRenderContext.world();
        if(frustum == null || camera == null) return;

        double camx = camera.getPos().x;
        double camz = camera.getPos().z;
        double firmHeight = world.getTopY() + 16;
        Box box = new Box(camx - 512, firmHeight - 1, camz - 512, camx + 512, firmHeight + 1, camz + 512);
        if(!frustum.isVisible(box)) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        Profiler profiler = client.getProfiler();
        PlayerEntity player = client.player;
        boolean debugMode_General = client.getDebugHud().shouldShowDebugHud() && player != null && player.getMainHandStack().isOf(OperationStarcleaveItems.FIRMAMENT_MANIPULATOR);

        if(debugMode_General) {
            profiler.push("starcleave_firmament");
            profiler.push("debug");
            // TODO serverside firmament regions broke most of debug, either fix or remove this at some point
            doRender(worldRenderContext, debugMode_General);
            profiler.pop();
            profiler.pop();
            return;
        }

        if(worldRenderContext.consumers() instanceof VertexConsumerProvider.Immediate immediate) {
            immediate.draw();
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
                profiler.swap("sky");
                Framebuffer firmamentFrameBuffer = ((WorldRendererDuck)worldRenderContext.worldRenderer()).operation_starcleave$getFirmamentFramebuffer();
                firmamentFrameBuffer.setClearColor(0, 0.08f, 0.08f, 1f);
                firmamentFrameBuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
                OperationStarcleaveRenderLayers.FIRMAMENT_SKY_TARGET.startDrawing();
                renderFirmamentSky(matrixStack, worldRenderContext);
                immediate.draw();
                OperationStarcleaveRenderLayers.FIRMAMENT_SKY_TARGET.endDrawing();

                profiler.swap("fracture");
                renderBakedSubRegions(matrixStack, worldRenderContext);
            }
            profiler.pop();
            profiler.pop();
        }
    }

    public static void renderFirmamentSky(MatrixStack matrices, WorldRenderContext worldRenderContext) {
        Matrix4f projectionMatrix = worldRenderContext.projectionMatrix();
        WorldRenderer worldRenderer = worldRenderContext.worldRenderer();
        WorldRendererAccessor worldRendererAccessor = (WorldRendererAccessor)worldRenderer;
        World world = worldRenderContext.world();
        float tickDelta = worldRenderContext.tickCounter().getTickDelta(false);

        float fogStart = RenderSystem.getShaderFogStart();
        RenderSystem.setShaderFogStart(Float.MAX_VALUE);

        VertexBuffer vb1 = worldRendererAccessor.getLightSkyBuffer();
        if(vb1 != null && !vb1.isClosed()) {
            matrices.push();
            matrices.translate(0, 20, 0);

            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(
                    GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO
            );

            RenderSystem.setShaderColor(0.15f, 0.12f, 0.08f, 1f);
            vb1.bind();
            vb1.draw(matrices.peek().getPositionMatrix(), projectionMatrix, GameRenderer.getPositionProgram());

            matrices.translate(0, -30, 0);
            RenderSystem.setShaderColor(0, 0.08f, 0.08f, 1f);
            vb1.draw(matrices.peek().getPositionMatrix(), projectionMatrix, GameRenderer.getPositionProgram());
            VertexBuffer.unbind();

            RenderSystem.setShaderColor(1, 1, 1, 1);

            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);

            matrices.pop();
        }

        float[] rs = new float[]{0.8f, 1f, 1f};
        float[] gs = new float[]{1f, 0.8f, 1f};
        float[] bs = new float[]{1f, 1f, 0.8f};

        VertexBuffer vb = worldRendererAccessor.getStarsBuffer();
        if(vb != null && !vb.isClosed()) {
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(
                    GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO
            );

            for(int i = 0; i < 12; i++) {
                int j = i % 3;
                int k = i / 3;

                int n = 20000 * (k + 1);
                matrices.push();
                float angle = ((System.currentTimeMillis() % n) / (float)(n) + i / 12f) * 2 * MathHelper.PI;
                matrices.translate(0, Math.sin(angle) * 20 * k, 0);
                matrices.multiply(new Quaternionf().rotateY(angle).rotateZ(Math.sin(angle) * 0.2f * k));

                RenderSystem.setShaderColor(rs[j], gs[j], bs[j], 0.75f);
                vb.bind();
                vb.draw(matrices.peek().getPositionMatrix(), projectionMatrix, GameRenderer.getPositionProgram());

                matrices.pop();
            }
            RenderSystem.setShaderColor(1, 1, 1, 1);
            VertexBuffer.unbind();

            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
        }

        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.setShaderTexture(0, Identifier.of("textures/environment/end_sky.png"));
        Tessellator tessellator = Tessellator.getInstance();

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
                GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO
        );

        float k2 = 50.0F;
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, Identifier.of("textures/environment/sun.png"));
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        for(int i = 0; i < 7; i++) {
            matrices.push();
            int n = 300000;
            float angle = ((System.currentTimeMillis() % n) / (float)n + (i / 7f)) * MathHelper.PI * 2;
            matrices.multiply(new Quaternionf().rotateY(-angle));
            matrices.translate(13, 0, 0);

            Matrix4f matrix4f2 = matrices.peek().getPositionMatrix();
            bufferBuilder.vertex(matrix4f2, -k2, 100.0F, -k2).texture(0.0F, 0.0F);
            bufferBuilder.vertex(matrix4f2, k2, 100.0F, -k2).texture(1.0F, 0.0F);
            bufferBuilder.vertex(matrix4f2, k2, 100.0F, k2).texture(1.0F, 1.0F);
            bufferBuilder.vertex(matrix4f2, -k2, 100.0F, k2).texture(0.0F, 1.0F);
            matrices.pop();
        }

        RenderSystem.setShaderColor(0.5f, 0.5f, 0.5f, 1);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
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

        MinecraftClient client = MinecraftClient.getInstance();

        VertexConsumerProvider vertexConsumerProvider = worldRenderContext.consumers();
        if(vertexConsumerProvider == null) return;
        Entity e = client.cameraEntity;
        if(e == null) return;

        boolean debugMode_Activity = client.getEntityRenderDispatcher().shouldRenderHitboxes();

        MatrixStack matrixStack = worldRenderContext.matrixStack();
        VertexConsumer vertexConsumer = debugMode_General ? vertexConsumerProvider.getBuffer(RenderLayer.getDebugQuads()) : vertexConsumerProvider.getBuffer(OperationStarcleaveRenderLayers.getFracture());

        int ex = e.getBlockX();
        int ez = e.getBlockZ();

        Vec3d camPos = worldRenderContext.camera().getPos();
        matrixStack.push();
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

                    float damage = Math.clamp(0, 7, firmamentSubRegion.getDamage(x, z)) / 7f;
                    if(debugMode_General) {
                        float drip = Math.clamp(0, 7, firmamentSubRegion.getDrip(x, z)) / 7f;

                        float displacementY = -firmamentSubRegion.getDisplacement(x, z);

                        boolean updated = debugMode_Activity && firmamentSubRegion.shouldUpdate();
                        float f = 0.125f * damage;

                        float r = Math.clamp(0, 1, damage);
                        float g = Math.clamp(0, 1, updated ? 1 : 0);
                        float b = Math.clamp(0, 1, drip);

                        renderQuadDebug(matrixStack.peek().getPositionMatrix(),
                                vertexConsumer,
                                worldX - ex + f, worldZ - ez + f,
                                worldX - ex + tileSize - f, worldZ - ez + tileSize - f,
                                e.getWorld().getTopY() + 16 + displacementY,
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

                        renderQuadReal(matrixStack.peek().getPositionMatrix(),
                                matrixStack.peek().getNormalMatrix(),
                                vertexConsumer,
                                (worldX - ex), (worldZ - ez),
                                (worldX - ex) + tileSize, (worldZ - ez) + tileSize,
                                e.getWorld().getTopY() + 16,
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

        matrixStack.pop();
    }

    public static void renderQuadDebug(Matrix4f positionMatrix, VertexConsumer vertexConsumer, float x1, float z1, float x2, float z2, float y, float r, float g, float b, float a) {
        vertexConsumer.vertex(positionMatrix, x1, y, z2).color(r, g, b, a);
        vertexConsumer.vertex(positionMatrix, x1, y, z1).color(r, g, b, a);
        vertexConsumer.vertex(positionMatrix, x2, y, z1).color(r, g, b, a);
        vertexConsumer.vertex(positionMatrix, x2, y, z2).color(r, g, b, a);
    }

    public static void renderQuadReal(Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertexConsumer, float x1, float z1, float x2, float z2, float y, float r, float g, float b, float a, float u1, float v1, float u2, float v2, int light, int ny) {
        Vector4f vec = new Vector4f(x1, y, z2, 1).mul(positionMatrix);
        Vector3f norm = new Vector3f(0, ny, 0).mul(normalMatrix);

        int color = ColorHelper.Argb.fromFloats(r, g, b, a);
        vertexConsumer.vertex(vec.x, vec.y, vec.z, color, u1, v2, 0, light, norm.x, norm.y, norm.z);
        vec = new Vector4f(x1, y, z1, 1).mul(positionMatrix);
        vertexConsumer.vertex(vec.x, vec.y, vec.z, color, u1, v1, 0, light, norm.x, norm.y, norm.z);
        vec = new Vector4f(x2, y, z1, 1).mul(positionMatrix);
        vertexConsumer.vertex(vec.x, vec.y, vec.z, color, u2, v1, 0, light, norm.x, norm.y, norm.z);
        vec = new Vector4f(x2, y, z2, 1).mul(positionMatrix);
        vertexConsumer.vertex(vec.x, vec.y, vec.z, color, u2, v2, 0, light, norm.x, norm.y, norm.z);
    }

    public static void renderBakedSubRegions(MatrixStack matrices, WorldRenderContext worldRenderContext) {
        Frustum frustum = worldRenderContext.frustum();
        if(frustum == null) return;

        Framebuffer firmamentFrameBuffer = ((WorldRendererDuck)worldRenderContext.worldRenderer()).operation_starcleave$getFirmamentFramebuffer();
        int currentTexID = RenderSystem.getShaderTexture(0);
        int firmamentSkyTexID = firmamentFrameBuffer.getColorAttachment();

        Firmament firmament = Firmament.fromWorld(worldRenderContext.world());
        if(firmament == null) return;
        int height = firmament.getY();

        Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
        RenderLayer renderLayer = OperationStarcleaveRenderLayers.getFracture();

        renderLayer.startDrawing();
        modelViewStack.pushMatrix();
        modelViewStack.identity();
        modelViewStack.mul(matrices.peek().getPositionMatrix());
        Vec3d camPos = worldRenderContext.camera().getPos();

        ShaderProgram shaderProgram = RenderSystem.getShader();

        if(shaderProgram != null) {
            int id = FirmamentTextureStorage.getInstance().texture.getGlId();
            RenderSystem.setShaderTexture(0, id);

            RenderSystem.setShaderTexture(1, firmamentSkyTexID);

            for(int m = 0; m < 2; ++m) {
                int n = RenderSystem.getShaderTexture(m);
                shaderProgram.addSampler("Sampler" + m, n);
            }

            if (shaderProgram.modelViewMat != null) {
                shaderProgram.modelViewMat.set(modelViewStack);
            }

            if (shaderProgram.projectionMat != null) {
                shaderProgram.projectionMat.set(RenderSystem.getProjectionMatrix());
            }

            if (shaderProgram.gameTime != null) {
                shaderProgram.gameTime.set(RenderSystem.getShaderGameTime());
            }

            if (shaderProgram.screenSize != null) {
                Window window = MinecraftClient.getInstance().getWindow();
                shaderProgram.screenSize.set((float)window.getFramebufferWidth(), (float)window.getFramebufferHeight());
            }

            shaderProgram.bind();
            GlUniform glUniform = shaderProgram.getUniform("ActiveRegions");
            if(glUniform != null) {
                float[] activeRegions = FirmamentTextureStorage.getInstance().getActiveRegions();
                glUniform.set(activeRegions);
            }

            Tessellator tessellator = Tessellator.getInstance();

            matrices.push();

            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);

            RegionPos regionPos = RegionPos.fromWorldCoords((int)Math.floor(camPos.x), (int)Math.floor(camPos.z));

            matrices.translate(regionPos.worldX-camPos.x, height-camPos.y, regionPos.worldZ-camPos.z);
            for(int i = -1; i <= 1; i++) {
                for(int j = -1; j <= 1; j++) {
                    int ox = 512 * i;
                    int oz = 512 * j;
                    float u1 = ((regionPos.rx + i) % 4) / 4f;
                    float v1 = ((regionPos.rz + j) % 4) / 4f;
                    float u2 = u1 + 0.25f;
                    float v2 = v1 + 0.25f;
                    bufferBuilder.vertex(matrix4f, ox, 0, oz).color(255, 255, 255, 255).texture(u1, v1).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(0, 0, 0);
                    bufferBuilder.vertex(matrix4f, ox + 512, 0, oz).color(255, 255, 255, 255).texture(u2, v1).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(0, 0, 0);
                    bufferBuilder.vertex(matrix4f, ox + 512, 0, oz + 512).color(255, 255, 255, 255).texture(u2, v2).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(0, 0, 0);
                    bufferBuilder.vertex(matrix4f, ox, 0, oz + 512).color(255, 255, 255, 255).texture(u1, v2).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(0, 0, 0);

                    bufferBuilder.vertex(matrix4f, ox, 0, oz + 512).color(255, 255, 255, 255).texture(u1, v2).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(0, 0, 0);
                    bufferBuilder.vertex(matrix4f, ox + 512, 0, oz + 512).color(255, 255, 255, 255).texture(u2, v2).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(0, 0, 0);
                    bufferBuilder.vertex(matrix4f, ox + 512, 0, oz).color(255, 255, 255, 255).texture(u2, v1).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(0, 0, 0);
                    bufferBuilder.vertex(matrix4f, ox, 0, oz).color(255, 255, 255, 255).texture(u1, v1).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(0, 0, 0);
                }
            }
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
            matrices.pop();




            VertexBuffer.unbind();

            shaderProgram.unbind();
        }

        modelViewStack.popMatrix();
        renderLayer.endDrawing();

        RenderSystem.setShaderTexture(0, currentTexID);
    }
}
