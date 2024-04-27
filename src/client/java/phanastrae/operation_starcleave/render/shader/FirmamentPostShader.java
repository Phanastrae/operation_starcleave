package phanastrae.operation_starcleave.render.shader;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import phanastrae.operation_starcleave.render.OperationStarcleaveWorldRenderer;
import phanastrae.operation_starcleave.render.firmament.FirmamentTextureStorage;
import phanastrae.operation_starcleave.world.firmament.Firmament;

import static net.minecraft.util.math.MathHelper.floorMod;

public class FirmamentPostShader {
    public static void draw() {
        MinecraftClient client = MinecraftClient.getInstance();
        if(client.worldRenderer instanceof OperationStarcleaveWorldRenderer operationStarcleaveWorldRenderer) {
            Framebuffer mainBuffer = client.getFramebuffer();

            Framebuffer dummyBuffer = operationStarcleaveWorldRenderer.operation_starcleave$getDummyFramebuffer();
            if(dummyBuffer == null) {
                return;
            }

            if (canDraw()) {
                // clear dummy
                dummyBuffer.setClearColor(0, 0, 0, 0);
                dummyBuffer.clear(MinecraftClient.IS_SYSTEM_MAC);

                // copy main to dummy
                dummyBuffer.beginWrite(false);
                RenderSystem.backupProjectionMatrix();
                mainBuffer.draw(client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight(), false);
                RenderSystem.restoreProjectionMatrix();

                RenderSystem.enableBlend();
                RenderSystem.blendFuncSeparate(
                        GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE
                );
                dummyBuffer.copyDepthFrom(mainBuffer);

                // apply dummy with effect to main
                mainBuffer.beginWrite(false);
                draw2(client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight(), false);

                RenderSystem.disableBlend();
                RenderSystem.defaultBlendFunc();
            }
        }
    }

    public static boolean canDraw() {
        return true; // TODO
    }

    public static void draw2(int width, int height, boolean disableBlend) {
        RenderSystem.assertOnGameThreadOrInit();
        if (!RenderSystem.isInInitPhase()) {
            RenderSystem.recordRenderCall(() -> drawInternal(width, height, disableBlend));
        } else {
            drawInternal(width, height, disableBlend);
        }
    }

    private static void drawInternal(int width, int height, boolean disableBlend) {
        MinecraftClient client = MinecraftClient.getInstance();
        if(!(client.worldRenderer instanceof OperationStarcleaveWorldRenderer operationStarcleaveWorldRenderer)) {
            return;
        }
        Framebuffer dummyBuffer = operationStarcleaveWorldRenderer.operation_starcleave$getDummyFramebuffer();
        if(dummyBuffer == null) return;
        World world = client.world;
        if(world == null) {
            return;
        }
        Firmament firmament = Firmament.fromWorld(world);
        if(firmament == null) {
            return;
        }

        ShaderProgram shaderProgram = OperationStarcleaveShaders.getFracturePostShader();
        if(shaderProgram == null) {
            return;
        }

        GameRenderer gameRenderer = client.gameRenderer;

        RenderSystem.assertOnRenderThread();
        GlStateManager._colorMask(true, true, true, false);
        GlStateManager._disableDepthTest();
        GlStateManager._depthMask(false);
        GlStateManager._viewport(0, 0, width, height);
        if (disableBlend) {
            GlStateManager._disableBlend();
        }

        Framebuffer mainBuffer = client.getFramebuffer();
        shaderProgram.addSampler("DiffuseSampler0", dummyBuffer.getColorAttachment());
        shaderProgram.addSampler("DiffuseSampler1", dummyBuffer.getDepthAttachment());

        int id = FirmamentTextureStorage.getInstance().getTexture().getGlId();
        RenderSystem.setShaderTexture(0, id);

        for(int m = 0; m < 1; ++m) {
            int n = RenderSystem.getShaderTexture(m);
            shaderProgram.addSampler("Sampler" + m, n);
        }

        GlUniform glUniform = shaderProgram.getUniform("IMat");
        if(glUniform != null) {
            MatrixStack matrices = new MatrixStack();
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(gameRenderer.getCamera().getPitch()));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(gameRenderer.getCamera().getYaw() + 180.0F));

            Matrix4f mat = new Matrix4f();
            mat.mul(RenderSystem.getProjectionMatrix());
            mat.mul(matrices.peek().getPositionMatrix());
            mat.invert();

            glUniform.set(mat);
        }
        glUniform = shaderProgram.getUniform("ActiveRegions");
        if(glUniform != null) {
            float[] activeRegions = FirmamentTextureStorage.getInstance().getActiveRegions();
            glUniform.set(activeRegions);
        }
        glUniform = shaderProgram.getUniform("FirmamentPos");
        if(glUniform != null) {
            Vec3d camPos = client.gameRenderer.getCamera().getPos();
            Vector3f firmamentPos = new Vector3f((float)(-floorMod(camPos.x, 2048)), (float)(firmament.getY() - camPos.y), (float)(-floorMod(camPos.z, 2048)));
            glUniform.set(firmamentPos);
        }

        Matrix4f matrix4f = new Matrix4f().setOrtho(0.0F, (float)width, (float)height, 0.0F, 1000.0F, 3000.0F);
        RenderSystem.setProjectionMatrix(matrix4f, VertexSorter.BY_Z);
        if (shaderProgram.modelViewMat != null) {
            shaderProgram.modelViewMat.set(new Matrix4f().translation(0.0F, 0.0F, -2000.0F));
        }

        if (shaderProgram.projectionMat != null) {
            shaderProgram.projectionMat.set(matrix4f);
        }

        if (shaderProgram.gameTime != null) {
            shaderProgram.gameTime.set(RenderSystem.getShaderGameTime());
        }

        shaderProgram.bind();
        float f = (float)width;
        float g = (float)height;
        float h = (float)dummyBuffer.viewportWidth / (float)dummyBuffer.textureWidth;
        float i = (float)dummyBuffer.viewportHeight / (float)dummyBuffer.textureHeight;

        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(0.0, (double)g, 0.0).texture(0.0F, 0.0F).color(255, 255, 255, 255).next();
        bufferBuilder.vertex((double)f, (double)g, 0.0).texture(h, 0.0F).color(255, 255, 255, 255).next();
        bufferBuilder.vertex((double)f, 0.0, 0.0).texture(h, i).color(255, 255, 255, 255).next();
        bufferBuilder.vertex(0.0, 0.0, 0.0).texture(0.0F, i).color(255, 255, 255, 255).next();
        BufferRenderer.draw(bufferBuilder.end());
        shaderProgram.unbind();
        GlStateManager._depthMask(true);
        GlStateManager._colorMask(true, true, true, true);
    }
}
