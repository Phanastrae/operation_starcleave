package phanastrae.operation_starcleave.client.render.shader;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
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

        if (client.levelRenderer instanceof LevelRendererDuck renderer) {
            RenderTarget mainBuffer = client.getMainRenderTarget();

            RenderTarget dummyBuffer = renderer.operation_starcleave$getDummyFramebuffer();

            FirmamentTextureStorage firmamentTextureStorage = renderer.operation_starcleave$getFirmamentTextureStorage();

            if (dummyBuffer != null && canDraw(firmamentTextureStorage)) {
                // this code should do nothing, but just in case the render state is messed up by other mods we reset it here to avoid problems
                RenderSystem.enableBlend();
                RenderSystem.disableBlend();

                // disable depth test, this also happens inside blitToScreen but also put it here for clarity
                RenderSystem.disableDepthTest();

                // clear dummy
                //dummyBuffer.setClearColor(0, 0, 0, 0);
                //dummyBuffer.clear(Minecraft.ON_OSX);

                // clear dummy buffer, specifically using whatever the last used clear color was, to hopefully slightly reduce the chance of weird render bugs actually being visible
                dummyBuffer.bindWrite(true);
                RenderSystem.clear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);
                dummyBuffer.unbindWrite();

                // copy main to dummy
                dummyBuffer.bindWrite(true);
                mainBuffer.blitToScreen(client.getWindow().getWidth(), client.getWindow().getHeight(), false);
                dummyBuffer.copyDepthFrom(mainBuffer);

                // apply dummy with effect to main
                mainBuffer.bindWrite(true);
                drawEffect(dummyBuffer, firmamentTextureStorage);

                // restore initial depth test state
                RenderSystem.enableDepthTest();
            }
        }

        profiler.pop();
    }

    public static boolean canDraw(FirmamentTextureStorage firmamentTextureStorage) {
        if (!firmamentTextureStorage.shouldRenderPostOnGraphicsMode()) {
            return false;
        }

        if (!firmamentTextureStorage.isAnyFilledAndActive()) {
            // don't render if there is nothing to render
            return false;
        }

        return true;
    }

    private static void drawEffect(RenderTarget dummyBuffer, FirmamentTextureStorage firmamentTextureStorage) {
        Minecraft client = Minecraft.getInstance();

        Level level = client.level;
        if (level == null) {
            return;
        }
        Firmament firmament = Firmament.fromLevel(level);
        if (firmament == null) {
            return;
        }
        ShaderInstance shaderProgram = OperationStarcleaveShaders.getFracturePostShader();
        if (shaderProgram == null) {
            return;
        }

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE
        );

        RenderSystem.colorMask(true, true, true, false);
        RenderSystem.depthMask(false);

        DynamicTexture firmamentTex = firmamentTextureStorage.getTexture();
        firmamentTex.bind();
        RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        shaderProgram.setSampler("DiffuseSampler", dummyBuffer.getColorTextureId());
        shaderProgram.setSampler("DepthSampler", dummyBuffer.getDepthTextureId());
        shaderProgram.setSampler("FirmamentSampler", firmamentTex.getId());

        Uniform glUniform = shaderProgram.getUniform("IMat");
        if (glUniform != null) {
            PoseStack matrices = new PoseStack();

            // copy matrices from whatever was used to render the fracture itself
            matrices.mulPose(FirmamentRenderer.LAST_POSITION_MATRIX);

            Matrix4f iMat = new Matrix4f();
            iMat.mul(RenderSystem.getProjectionMatrix());
            iMat.mul(matrices.last().pose());
            iMat.invert();

            glUniform.set(iMat);
        }

        glUniform = shaderProgram.getUniform("FirmamentPos");
        if (glUniform != null) {
            Vec3 camPos = client.gameRenderer.getMainCamera().getPosition();
            Vector3f firmamentPos = new Vector3f(
                    (float) (-positiveModulo(camPos.x, 2048)),
                    (float) (firmament.getY() - camPos.y),
                    (float) (-positiveModulo(camPos.z, 2048))
            );
            glUniform.set(firmamentPos);
        }

        shaderProgram.setDefaultUniforms(VertexFormat.Mode.QUADS, RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), client.getWindow());
        shaderProgram.apply();

        Tesselator tesselator = RenderSystem.renderThreadTesselator();

        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLIT_SCREEN);
        bufferBuilder.addVertex(0.0F, 0.0F, 0.0F);
        bufferBuilder.addVertex(1.0F, 0.0F, 0.0F);
        bufferBuilder.addVertex(1.0F, 1.0F, 0.0F);
        bufferBuilder.addVertex(0.0F, 1.0F, 0.0F);
        BufferUploader.draw(bufferBuilder.buildOrThrow());

        shaderProgram.clear();

        RenderSystem.depthMask(true);
        RenderSystem.colorMask(true, true, true, true);

        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    }
}
