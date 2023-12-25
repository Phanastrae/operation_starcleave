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
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import org.joml.*;
import org.joml.Math;
import org.lwjgl.opengl.GL11;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;
import phanastrae.operation_starcleave.util.FrameBufferStencilAccess;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.FirmamentSubRegion;

public class FirmamentRenderer {
    public static void render(WorldRenderContext worldRenderContext) {
        if(MinecraftClient.getInstance().getDebugHud().shouldShowDebugHud()) {
            doRender(worldRenderContext);
            return;
        }

        Framebuffer frameBuffer = MinecraftClient.getInstance().getFramebuffer();
        if(!(frameBuffer instanceof FrameBufferStencilAccess FBSA)) return;

        if(!FBSA.operation_starcleave$stencilBufferEnabled()) {
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
            doRender(worldRenderContext);
            RenderSystem.colorMask(false, false, false, false);
            RenderSystem.depthMask(false);
            immediate.draw(RenderLayer.getSolid());
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

            doRender(worldRenderContext);
            RenderSystem.colorMask(false, false, false, true);
            immediate.draw(RenderLayer.getSolid());
            RenderSystem.colorMask(true, true, true, true);
        }
    }

    public static void doRender(WorldRenderContext worldRenderContext) {
        VertexConsumerProvider vertexConsumerProvider = worldRenderContext.consumers();
        if(vertexConsumerProvider == null) return;
        Entity e = MinecraftClient.getInstance().cameraEntity;
        if(e == null) return;
        int ex = e.getBlockX();
        int ez = e.getBlockZ();

        Profiler profiler = MinecraftClient.getInstance().getProfiler();
        profiler.push("starcleave_fracture");

        Sprite texture = MinecraftClient.getInstance().getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(new Identifier("block/white_concrete"));

        MatrixStack matrixStack = worldRenderContext.matrixStack();

        matrixStack.push();
        Vec3d camPos = worldRenderContext.camera().getPos();
        matrixStack.translate(-camPos.x, -camPos.y, -camPos.z);

        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getSolid());

        float tileSize = FirmamentSubRegion.TILE_SIZE;

        Firmament firmament = Firmament.getInstance();
        firmament.forEachRegion((firmamentRegion -> {
            int rdx = firmamentRegion.x + 256 - ex;
            int rdz = firmamentRegion.z + 256 - ez;
            if(rdx*rdx + rdz*rdz > 512*512) {
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

                    double dx = worldX - e.getPos().x;
                    double dz = worldZ - e.getPos().z;
                    double dist = Math.sqrt(dx*dx + dz*dz);

                    float damage = Math.clamp(0, 1, firmamentSubRegion.getDamage(x, z));

                    float displacementY = firmamentSubRegion.getDisplacement(x, z);

                    float hOffset = (float)(-dist * dist * dist) / 1000;

                    float weightedDrip = (float)Math.max(java.lang.Math.log1p(Math.abs(firmamentSubRegion.getDrip(x, z))), 0);

                    boolean updated = firmamentSubRegion.shouldUpdate() && MinecraftClient.getInstance().getEntityRenderDispatcher().shouldRenderHitboxes();
                    float f = 0.0625f * damage;

                    float r = Math.clamp(0, 1, damage);
                    float g = Math.clamp(0, 1, updated ? 1 : 0);
                    float b = Math.clamp(0, 1, displacementY / -15);

                    if(!MinecraftClient.getInstance().getDebugHud().shouldShowDebugHud() || MinecraftClient.getInstance().player == null || !MinecraftClient.getInstance().player.getMainHandStack().isOf(OperationStarcleaveItems.FIRMAMENT_MANIPULATOR)) {
                        g = r;
                        b = 0;
                        f = (1 - damage) * 0.5f;
                        displacementY = 0;
                        if (f == 0.5f) return;
                    }

                    renderQuad(matrixStack.peek().getPositionMatrix(),
                            matrixStack.peek().getNormalMatrix(),
                            vertexConsumer,
                            worldX + f, worldZ + f,
                            worldX + tileSize - f, worldZ + tileSize - f,
                            e.getWorld().getTopY() + 65 + displacementY,
                            r,
                            g,
                            b,
                            1,
                            texture.getMinU(), texture.getMinV(), texture.getMaxU(), texture.getMaxV(),
                            LightmapTextureManager.MAX_LIGHT_COORDINATE, -1);
                });
            }));
        }));

        matrixStack.pop();

        profiler.pop();
    }

    public static void renderQuad(Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertexConsumer, float x1, float z1, float x2, float z2, float y, float r, float g, float b, float a, float u1, float v1, float u2, float v2, int light, int ny) {
        Vector4f vec = new Vector4f(x1, y, z2, 1).mul(positionMatrix);

        vertexConsumer.vertex(vec.x, vec.y, vec.z, r, g, b, a, u1, v1, 0, light, 0, ny, 0);
        vec = new Vector4f(x1, y, z1, 1).mul(positionMatrix);
        vertexConsumer.vertex(vec.x, vec.y, vec.z, r, g, b, a, u1, v2, 0, light, 0, ny, 0);
        vec = new Vector4f(x2, y, z1, 1).mul(positionMatrix);
        vertexConsumer.vertex(vec.x, vec.y, vec.z, r, g, b, a, u2, v2, 0, light, 0, ny, 0);
        vec = new Vector4f(x2, y, z2, 1).mul(positionMatrix);
        vertexConsumer.vertex(vec.x, vec.y, vec.z, r, g, b, a, u2, v1, 0, light, 0, ny, 0);

        // normals uhhh
    }
}
