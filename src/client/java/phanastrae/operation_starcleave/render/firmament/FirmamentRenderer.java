package phanastrae.operation_starcleave.render.firmament;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import org.joml.*;
import org.joml.Math;
import org.lwjgl.opengl.GL11;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;
import phanastrae.operation_starcleave.render.OperationStarcleaveRenderLayers;
import phanastrae.operation_starcleave.util.FrameBufferStencilAccess;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.FirmamentSubRegion;

public class FirmamentRenderer {
    public static void render(WorldRenderContext worldRenderContext) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        boolean debugMode_General = client.getDebugHud().shouldShowDebugHud() && player != null && player.getMainHandStack().isOf(OperationStarcleaveItems.FIRMAMENT_MANIPULATOR);

        if(debugMode_General) {
            doRender(worldRenderContext, debugMode_General);
            return;
        }

        Framebuffer frameBuffer = MinecraftClient.getInstance().getFramebuffer();
        if(!(frameBuffer instanceof FrameBufferStencilAccess FBSA)) return;

        if(!FBSA.operation_starcleave$stencilBufferEnabled()) {
            OperationStarcleave.LOGGER.info("Enabled stencil buffer");
            FBSA.operation_starcleave$setEnabled(true);
        }

        if(worldRenderContext.consumers() instanceof VertexConsumerProvider.Immediate immediate) {
            immediate.draw();

            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(GL11.GL_LESS);
            GL11.glEnable(GL11.GL_STENCIL_TEST);
            GL11.glStencilFunc(GL11.GL_NOTEQUAL, 1, 0xFF);
            RenderSystem.stencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

            GL11.glClearStencil(0);
            GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);

            RenderSystem.stencilMask(0);

            RenderSystem.stencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
            RenderSystem.stencilMask(0xFF);
            doRender(worldRenderContext, false);
            RenderSystem.colorMask(false, false, false, false);
            RenderSystem.depthMask(false);
            immediate.draw(OperationStarcleaveRenderLayers.getFracture());
            RenderSystem.colorMask(true, true, true, true);

            RenderSystem.stencilFunc(GL11.GL_EQUAL, 1, 0xFF);
            RenderSystem.stencilMask(0);
            RenderSystem.disableDepthTest();

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

            RenderSystem.stencilMask(0xFF);
            RenderSystem.stencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
            RenderSystem.enableDepthTest();
            GL11.glDisable(GL11.GL_STENCIL_TEST);

            RenderSystem.enableBlend();
            doRender(worldRenderContext, false);
            immediate.draw(OperationStarcleaveRenderLayers.getFracture());
            RenderSystem.disableBlend();
        }
    }

    public static void doRender(WorldRenderContext worldRenderContext, boolean debugMode_General) {
        MinecraftClient client = MinecraftClient.getInstance();

        VertexConsumerProvider vertexConsumerProvider = worldRenderContext.consumers();
        if(vertexConsumerProvider == null) return;
        Entity e = client.cameraEntity;
        if(e == null) return;

        Profiler profiler = client.getProfiler();
        profiler.push("starcleave_fracture");

        PlayerEntity player = client.player;
        boolean debugMode_Activity = client.getEntityRenderDispatcher().shouldRenderHitboxes();

        MatrixStack matrixStack = worldRenderContext.matrixStack();
        VertexConsumer vertexConsumer = debugMode_General ? vertexConsumerProvider.getBuffer(RenderLayer.getSolid()) : vertexConsumerProvider.getBuffer(OperationStarcleaveRenderLayers.getFracture());
        Vec3d camPos = worldRenderContext.camera().getPos();
        Sprite texture = client.getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(new Identifier("block/white_concrete"));

        int ex = e.getBlockX();
        int ez = e.getBlockZ();

        matrixStack.push();
        matrixStack.translate(-camPos.x, -camPos.y, -camPos.z);


        Firmament firmament = Firmament.getInstance();
        int tileSize = FirmamentSubRegion.TILE_SIZE;
        firmament.forEachRegion((firmamentRegion -> {
            int rdx = firmamentRegion.x + 256 - ex;
            int rdz = firmamentRegion.z + 256 - ez;
            if(rdx*rdx + rdz*rdz > 1024*1024) {
                return;
            }
            firmamentRegion.forEachSubRegion((firmamentSubRegion -> {
                int srdx = firmamentSubRegion.x + 4 - ex;
                int srdz = firmamentSubRegion.z + 4 - ez;
                if(srdx*srdx + srdz*srdz > 512*512) {
                    return;
                }

                firmamentSubRegion.forEachPosition((x, z) -> {
                    int worldX = x + firmamentSubRegion.x;
                    int worldZ = z + firmamentSubRegion.z;

                    float damage = Math.clamp(0, 1, firmamentSubRegion.getDamage(x, z));
                    if(debugMode_General) {
                        float weightedDrip = (float)Math.max(java.lang.Math.log1p(Math.abs(firmamentSubRegion.getDrip(x, z))), 0);


                        float displacementY = firmamentSubRegion.getDisplacement(x, z);

                        boolean updated = debugMode_Activity && firmamentSubRegion.shouldUpdate();
                        float f = 0.0625f * damage;

                        float r = Math.clamp(0, 1, damage);
                        float g = Math.clamp(0, 1, updated ? 1 : 0);
                        float b = Math.clamp(0, 1, displacementY / -15);

                        renderQuad(matrixStack.peek().getPositionMatrix(),
                                matrixStack.peek().getNormalMatrix(),
                                vertexConsumer,
                                worldX + f, worldZ + f,
                                worldX + tileSize - f, worldZ + tileSize - f,
                                e.getWorld().getTopY() + 16 + displacementY,
                                r,
                                g,
                                b,
                                1,
                                texture.getMinU(), texture.getMinV(), texture.getMaxU(), texture.getMaxV(),
                                LightmapTextureManager.MAX_LIGHT_COORDINATE, -1);
                    } else {
                        boolean dam = false;
                        int[][] damageArray = new int[3][3];
                        for(int i = -1; i <= 1; i++) {
                            for(int j = -1; j <= 1; j++) {
                                int ldam = (int)(firmament.getDamage(worldX+i*tileSize, worldZ+j*tileSize)*15);
                                if(ldam != 0) {
                                    damageArray[i + 1][j + 1] = ldam;
                                    dam = true;
                                }
                            }
                        }
                        if(!dam) return;

                        int rbyte = (damageArray[0][0] & 0xF) | ((damageArray[0][1] & 0xF) << 4);
                        int gbyte = (damageArray[0][2] & 0xF) | ((damageArray[1][0] & 0xF) << 4);
                        int bbyte = (damageArray[1][1] & 0xF) | ((damageArray[1][2] & 0xF) << 4);
                        int abyte = (damageArray[2][0] & 0xF) | ((damageArray[2][1] & 0xF) << 4);
                        int lbyte = (damageArray[2][2] & 0xF);

                        renderQuad2(matrixStack.peek().getPositionMatrix(),
                                matrixStack.peek().getNormalMatrix(),
                                vertexConsumer,
                                worldX, worldZ,
                                worldX + tileSize, worldZ + tileSize,
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

        profiler.pop();
    }

    public static void renderQuad(Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertexConsumer, float x1, float z1, float x2, float z2, float y, float r, float g, float b, float a, float u1, float v1, float u2, float v2, int light, int ny) {
        Vector4f vec = new Vector4f(x1, y, z2, 1).mul(positionMatrix);
        Vector3f norm = new Vector3f(0, ny, 0).mul(normalMatrix);

        vertexConsumer.vertex(vec.x, vec.y, vec.z, r, g, b, a, u1, v1, 0, light, norm.x, norm.y, norm.z);
        vec = new Vector4f(x1, y, z1, 1).mul(positionMatrix);
        vertexConsumer.vertex(vec.x, vec.y, vec.z, r, g, b, a, u1, v2, 0, light, norm.x, norm.y, norm.z);
        vec = new Vector4f(x2, y, z1, 1).mul(positionMatrix);
        vertexConsumer.vertex(vec.x, vec.y, vec.z, r, g, b, a, u2, v2, 0, light, norm.x, norm.y, norm.z);
        vec = new Vector4f(x2, y, z2, 1).mul(positionMatrix);
        vertexConsumer.vertex(vec.x, vec.y, vec.z, r, g, b, a, u2, v1, 0, light, norm.x, norm.y, norm.z);
    }

    public static void renderQuad2(Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertexConsumer, float x1, float z1, float x2, float z2, float y, float r, float g, float b, float a, float u1, float v1, float u2, float v2, int light, int ny) {
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
}
