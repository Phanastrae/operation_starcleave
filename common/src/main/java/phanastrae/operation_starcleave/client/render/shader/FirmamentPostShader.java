package phanastrae.operation_starcleave.client.render.shader;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import phanastrae.operation_starcleave.client.duck.LevelRendererDuck;
import phanastrae.operation_starcleave.client.render.firmament.FirmamentRenderer;
import phanastrae.operation_starcleave.client.render.firmament.FirmamentTextureStorage;
import phanastrae.operation_starcleave.world.firmament.Firmament;

import static com.mojang.blaze3d.platform.GlConst.*;
import static net.minecraft.util.Mth.positiveModulo;

public class FirmamentPostShader {
    public static void draw() {
        Minecraft client = Minecraft.getInstance();

        ProfilerFiller profiler = client.getProfiler();
        profiler.push("starcleave_post_effect");

        if(client.levelRenderer instanceof LevelRendererDuck operationStarcleaveWorldRenderer) {
            RenderTarget mainBuffer = client.getMainRenderTarget();

            RenderTarget dummyBuffer = operationStarcleaveWorldRenderer.operation_starcleave$getDummyFramebuffer();

            if (dummyBuffer != null && canDraw()) {
                // this code should do nothing, but just in case the render state is messed up by other mods we reset it here to avoid problems
                RenderSystem.enableBlend();
                RenderSystem.disableBlend();

                // clear dummy
                //dummyBuffer.setClearColor(0, 0, 0, 0);
                //dummyBuffer.clear(Minecraft.ON_OSX);

                // clear dummy buffer, specifically using whatever the last used clear color was, to hopefully slightly reduce the chance of weird render bugs actually being visible
                dummyBuffer.bindWrite(true);
                RenderSystem.clear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);
                dummyBuffer.unbindWrite();

                // copy main to dummy
                dummyBuffer.bindWrite(true);
                RenderSystem.backupProjectionMatrix();
                mainBuffer.blitToScreen(client.getWindow().getWidth(), client.getWindow().getHeight(), false);
                RenderSystem.restoreProjectionMatrix();

                RenderSystem.enableBlend();
                RenderSystem.blendFuncSeparate(
                        GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE
                );
                dummyBuffer.copyDepthFrom(mainBuffer);

                // apply dummy with effect to main
                mainBuffer.bindWrite(true);
                draw2(client.getWindow().getWidth(), client.getWindow().getHeight(), false);

                RenderSystem.disableBlend();
                RenderSystem.defaultBlendFunc();
            }
        }

        profiler.pop();
    }

    public static boolean canDraw() {
        if(!FirmamentTextureStorage.getInstance().shouldRenderPostOnGraphicsMode()) {
            return false;
        }

        if(!FirmamentTextureStorage.getInstance().isAnyFilledAndActive()) {
            // don't render if there is nothing to render
            return false;
        }

        return true;
    }

    public static void draw2(int width, int height, boolean disableBlend) {
        RenderSystem.assertOnRenderThreadOrInit();
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> drawInternal(width, height, disableBlend));
        } else {
            drawInternal(width, height, disableBlend);
        }
    }

    private static void drawInternal(int width, int height, boolean disableBlend) {
        Minecraft client = Minecraft.getInstance();
        if(!(client.levelRenderer instanceof LevelRendererDuck operationStarcleaveWorldRenderer)) {
            return;
        }
        RenderTarget dummyBuffer = operationStarcleaveWorldRenderer.operation_starcleave$getDummyFramebuffer();
        if(dummyBuffer == null) return;
        Level world = client.level;
        if(world == null) {
            return;
        }
        Firmament firmament = Firmament.fromLevel(world);
        if(firmament == null) {
            return;
        }

        ShaderInstance shaderProgram = OperationStarcleaveShaders.getFracturePostShader();
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

        shaderProgram.setSampler("DiffuseSampler0", dummyBuffer.getColorTextureId());
        shaderProgram.setSampler("DiffuseSampler1", dummyBuffer.getDepthTextureId());

        DynamicTexture firmamentTex = FirmamentTextureStorage.getInstance().getTexture();
        RenderSystem.setShaderTexture(0, firmamentTex.getId());
        firmamentTex.bind();
        RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        for(int m = 0; m < 1; ++m) {
            int n = RenderSystem.getShaderTexture(m);
            shaderProgram.setSampler("Sampler" + m, n);
        }

        Uniform glUniform = shaderProgram.getUniform("IMat");
        if(glUniform != null) {
            PoseStack matrices = new PoseStack();

            // calculate matrices from camera
            //matrices.mulPose(Axis.XP.rotationDegrees(gameRenderer.getMainCamera().getXRot()));
            //matrices.mulPose(Axis.YP.rotationDegrees(gameRenderer.getMainCamera().getYRot() + 180.0F));

            // copy matrices from whatever was used to render the fracture itself
            matrices.mulPose(FirmamentRenderer.LAST_POSITION_MATRIX);

            Matrix4f mat = new Matrix4f();
            mat.mul(RenderSystem.getProjectionMatrix());
            mat.mul(matrices.last().pose());
            mat.invert();

            glUniform.set(mat);
        }
        glUniform = shaderProgram.getUniform("FirmamentPos");
        if(glUniform != null) {
            Vec3 camPos = client.gameRenderer.getMainCamera().getPosition();
            Vector3f firmamentPos = new Vector3f((float)(-positiveModulo(camPos.x, 2048)), (float)(firmament.getY() - camPos.y), (float)(-positiveModulo(camPos.z, 2048)));
            glUniform.set(firmamentPos);
        }

        Matrix4f matrix4f = new Matrix4f().setOrtho(0.0F, (float)width, (float)height, 0.0F, 1000.0F, 3000.0F);
        RenderSystem.setProjectionMatrix(matrix4f, VertexSorting.ORTHOGRAPHIC_Z);
        if (shaderProgram.MODEL_VIEW_MATRIX != null) {
            shaderProgram.MODEL_VIEW_MATRIX.set(new Matrix4f().translation(0.0F, 0.0F, -2000.0F));
        }

        if (shaderProgram.PROJECTION_MATRIX != null) {
            shaderProgram.PROJECTION_MATRIX.set(matrix4f);
        }

        if (shaderProgram.GAME_TIME != null) {
            shaderProgram.GAME_TIME.set(RenderSystem.getShaderGameTime());
        }

        shaderProgram.apply();
        float f = (float)width;
        float g = (float)height;
        float h = (float)dummyBuffer.viewWidth / (float)dummyBuffer.width;
        float i = (float)dummyBuffer.viewHeight / (float)dummyBuffer.height;

        Tesselator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferBuilder.addVertex(0F, g, 0F).setUv(0.0F, 0.0F).setColor(255, 255, 255, 255);
        bufferBuilder.addVertex(f, g, 0F).setUv(h, 0.0F).setColor(255, 255, 255, 255);
        bufferBuilder.addVertex(f, 0F, 0F).setUv(h, i).setColor(255, 255, 255, 255);
        bufferBuilder.addVertex(0F, 0F, 0F).setUv(0.0F, i).setColor(255, 255, 255, 255);
        BufferUploader.draw(bufferBuilder.buildOrThrow());
        shaderProgram.clear();
        GlStateManager._depthMask(true);
        GlStateManager._colorMask(true, true, true, true);
    }
}
