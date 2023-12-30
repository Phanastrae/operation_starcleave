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
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import org.joml.*;
import org.joml.Math;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;
import phanastrae.operation_starcleave.render.OperationStarcleaveRenderLayers;
import phanastrae.operation_starcleave.render.OperationStarcleaveWorldRenderer;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.FirmamentSubRegion;
import phanastrae.operation_starcleave.world.firmament.RegionPos;
import phanastrae.operation_starcleave.world.firmament.SubRegionPos;

import java.util.concurrent.atomic.AtomicBoolean;

public class FirmamentRenderer {
    public static void render(WorldRenderContext worldRenderContext) {
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

            AtomicBoolean renderSkybox = new AtomicBoolean(false);
            FirmamentBuiltSubRegionStorage.getInstance().forEach((firmamentBuiltSubRegionHolder -> {
                if (!renderSkybox.get()) {
                    FirmamentBuiltSubRegion builtSubRegion = firmamentBuiltSubRegionHolder.getBuiltSubRegion();
                    if (builtSubRegion != null && frustum.isVisible(firmamentBuiltSubRegionHolder.box)) {
                        renderSkybox.set(true);
                    }
                }
            }));

            if(renderSkybox.get()) {
                profiler.swap("sky");
                Framebuffer firmamentFrameBuffer = ((OperationStarcleaveWorldRenderer)worldRenderContext.worldRenderer()).operation_starcleave$getFirmamentFramebuffer();
                firmamentFrameBuffer.setClearColor(1, 1, 1, 1);
                firmamentFrameBuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
                OperationStarcleaveRenderLayers.FIRMAMENT_SKY_TARGET.startDrawing();
                renderFirmamentSky(worldRenderContext);
                immediate.draw();
                OperationStarcleaveRenderLayers.FIRMAMENT_SKY_TARGET.endDrawing();

                profiler.swap("fracture");
                renderBakedSubRegions(worldRenderContext);
            }
            profiler.pop();
            profiler.pop();
        }
    }

    public static void renderFirmamentSky(WorldRenderContext worldRenderContext) {
            MatrixStack matrices = worldRenderContext.matrixStack();
            RenderSystem.depthMask(false);
            RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
            RenderSystem.setShaderTexture(0, new Identifier("textures/environment/end_sky.png"));
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();

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

            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(
                    GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO
            );

            float k2 = 150.0F;
            Matrix4f matrix4f2 = matrices.peek().getPositionMatrix();
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderTexture(0, new Identifier("textures/environment/sun.png"));
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            bufferBuilder.vertex(matrix4f2, -k2, 100.0F, -k2).texture(0.0F, 0.0F).next();
            bufferBuilder.vertex(matrix4f2, k2, 100.0F, -k2).texture(1.0F, 0.0F).next();
            bufferBuilder.vertex(matrix4f2, k2, 100.0F, k2).texture(1.0F, 1.0F).next();
            bufferBuilder.vertex(matrix4f2, -k2, 100.0F, k2).texture(0.0F, 1.0F).next();
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

            RenderSystem.defaultBlendFunc();
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
    }

    public static void doRender(WorldRenderContext worldRenderContext, boolean debugMode_General) {
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

                    float damage = Math.clamp(0, 1, firmamentSubRegion.getDamage(x, z));
                    if(debugMode_General) {
                        float weightedDrip = (float)Math.max(java.lang.Math.log1p(Math.abs(firmamentSubRegion.getDrip(x, z))), 0);

                        float displacementY = firmamentSubRegion.getDisplacement(x, z);

                        boolean updated = debugMode_Activity && firmamentSubRegion.shouldUpdate();
                        float f = 0.125f * damage;

                        float r = Math.clamp(0, 1, damage);
                        float g = Math.clamp(0, 1, updated ? 1 : 0);
                        float b = Math.clamp(0, 1, displacementY / -15);

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
                                    damageArray[i + 1][j + 1] = (int) (firmament.getDamage(worldX + i * tileSize, worldZ + j * tileSize) * 15);
                                }
                            }
                        } else {
                            for (int i = -1; i <= 1; i++) {
                                for (int j = -1; j <= 1; j++) {
                                    damageArray[i + 1][j + 1] = (int) (firmamentSubRegion.getDamage(x + i * tileSize, z + j * tileSize) * 15);
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
        vertexConsumer.vertex(positionMatrix, x1, y, z2).color(r, g, b, a).next();
        vertexConsumer.vertex(positionMatrix, x1, y, z1).color(r, g, b, a).next();
        vertexConsumer.vertex(positionMatrix, x2, y, z1).color(r, g, b, a).next();
        vertexConsumer.vertex(positionMatrix, x2, y, z2).color(r, g, b, a).next();
    }

    public static void renderQuadReal(Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertexConsumer, float x1, float z1, float x2, float z2, float y, float r, float g, float b, float a, float u1, float v1, float u2, float v2, int light, int ny) {
        Vector4f vec = new Vector4f(x1, y, z2, 1).mul(positionMatrix);
        Vector3f norm = new Vector3f(0, ny, 0).mul(normalMatrix);

        vertexConsumer.vertex(vec.x, vec.y, vec.z, r, g, b, a, u1, v2, 0, light, norm.x, norm.y, norm.z);
        vec = new Vector4f(x1, y, z1, 1).mul(positionMatrix);
        vertexConsumer.vertex(vec.x, vec.y, vec.z, r, g, b, a, u1, v1, 0, light, norm.x, norm.y, norm.z);
        vec = new Vector4f(x2, y, z1, 1).mul(positionMatrix);
        vertexConsumer.vertex(vec.x, vec.y, vec.z, r, g, b, a, u2, v1, 0, light, norm.x, norm.y, norm.z);
        vec = new Vector4f(x2, y, z2, 1).mul(positionMatrix);
        vertexConsumer.vertex(vec.x, vec.y, vec.z, r, g, b, a, u2, v2, 0, light, norm.x, norm.y, norm.z);
    }

    public static void renderBakedSubRegions(WorldRenderContext worldRenderContext) {
        Frustum frustum = worldRenderContext.frustum();
        if(frustum == null) return;

        Framebuffer firmamentFrameBuffer = ((OperationStarcleaveWorldRenderer)worldRenderContext.worldRenderer()).operation_starcleave$getFirmamentFramebuffer();
        int currentTexID = RenderSystem.getShaderTexture(0);
        int firmamentSkyTexID = firmamentFrameBuffer.getColorAttachment();

        int height = worldRenderContext.world().getTopY() + 16;

        MatrixStack modelViewStack = RenderSystem.getModelViewStack();
        RenderLayer renderLayer = OperationStarcleaveRenderLayers.getFracture();

        renderLayer.startDrawing();
        modelViewStack.push();
        modelViewStack.loadIdentity();
        modelViewStack.multiplyPositionMatrix(worldRenderContext.matrixStack().peek().getPositionMatrix());
        Vec3d camPos = worldRenderContext.camera().getPos();

        ShaderProgram shaderProgram = RenderSystem.getShader();

        if(shaderProgram != null) {
            RenderSystem.setShaderTexture(0, firmamentSkyTexID);

            int n = RenderSystem.getShaderTexture(0);
            shaderProgram.addSampler("Sampler" + 0, n);

            if (shaderProgram.modelViewMat != null) {
                shaderProgram.modelViewMat.set(modelViewStack.peek().getPositionMatrix());
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
            GlUniform glUniform = shaderProgram.chunkOffset;

            FirmamentBuiltSubRegionStorage.getInstance().forEach((firmamentBuiltSubRegionHolder -> {
                FirmamentBuiltSubRegion builtSubRegion = firmamentBuiltSubRegionHolder.getBuiltSubRegion();
                if(builtSubRegion != null && frustum.isVisible(firmamentBuiltSubRegionHolder.box)) {
                    SubRegionPos subRegionPos = new SubRegionPos(firmamentBuiltSubRegionHolder.id);
                    double dx = (subRegionPos.worldX + FirmamentSubRegion.SUBREGION_SIZE / 2f) - camPos.x;
                    double dz = (subRegionPos.worldZ + FirmamentSubRegion.SUBREGION_SIZE / 2f) - camPos.z;
                    double distSqr = dx*dx + dz*dz;
                    if(distSqr < 512*512) {
                        if (glUniform != null) {
                            glUniform.set((float)((double)subRegionPos.worldX - camPos.x), (float)((double)height - camPos.y), (float)((double)subRegionPos.worldZ - camPos.z));
                            glUniform.upload();
                        }

                        builtSubRegion.bind();
                        builtSubRegion.draw();
                    }
                }
            }));
            if (glUniform != null) {
                glUniform.set(0.0F, 0.0F, 0.0F);
            }

            VertexBuffer.unbind();

            shaderProgram.unbind();
        }

        modelViewStack.pop();
        renderLayer.endDrawing();

        RenderSystem.setShaderTexture(0, currentTexID);
    }
}
