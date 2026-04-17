package phanastrae.operation_starcleave.client.render.firmament;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import phanastrae.operation_starcleave.client.compat.ClientCompat;
import phanastrae.operation_starcleave.client.duck.LevelRendererDuck;
import phanastrae.operation_starcleave.client.render.OperationStarcleaveRenderTypes;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.pos.RegionPos;

import static com.mojang.blaze3d.platform.GlConst.*;

public class FirmamentRenderer {

    private static final int REGIONS = 4; // regions per texture
    private static final int REGION_WIDTH = 512;
    private static final float REGION_UV_SIZE = 1.0F / REGIONS;
    private static final float FIRMAMENT_HEIGHT_RENDER_OFFSET = 1 / 16F;

    // we use this to get the position matrix in the post shader, in case somebody is messing with it in a weird way that needs copying
    // TODO consider moving the post shader inside of LevelRenderer so we can just grab this directly, and also have maybe-better compat
    public static Matrix4f LAST_POSITION_MATRIX = new Matrix4f();

    public static void render(Level level, Camera camera, Frustum frustum, LevelRenderer levelRenderer, Matrix4f projectionMatrix, Matrix4f positionMatrix) {
        Minecraft client = Minecraft.getInstance();

        // check the camera is the same camera as the actual client's main camera to try and avoid issues with mods that render the world twice
        // TODO test this with more mods, and maybe look for a better way to do this (ie moving this to LevelRenderer)
        if (client.gameRenderer.getMainCamera().equals(camera)) {
            // update the last position matrix used, for future use in the post renderer
            // this is needed for mods that apply additional transformations to the camera
            LAST_POSITION_MATRIX.set(positionMatrix);
        }

        if (frustum == null || camera == null) return;

        Firmament firmament = Firmament.fromLevel(level);
        if (firmament == null) return;

        ProfilerFiller profiler = client.getProfiler();
        profiler.push("starcleave_firmament");

        profiler.push("check");
        if (isFirmamentVisible(firmament, camera, frustum) && FirmamentTextureStorage.fromLevelRenderer(levelRenderer).isAnyFilledAndActive()) {
            profiler.popPush("sky");
            renderSky(levelRenderer, projectionMatrix, positionMatrix);

            profiler.popPush("fracture");
            if (!ClientCompat.useAltFractureRendering()) {
                renderFracture(levelRenderer, firmament, camera, projectionMatrix, positionMatrix);
            } else {
                renderFractureUsingBuffer(levelRenderer, firmament, camera, projectionMatrix, positionMatrix, profiler);
            }
        }
        profiler.pop();

        profiler.pop();
    }

    private static boolean isFirmamentVisible(Firmament firmament, Camera camera, Frustum frustum) {
        return frustum.isVisible(makeFirmamentBoundingBox(camera, firmament));
    }

    private static AABB makeFirmamentBoundingBox(Camera camera, Firmament firmament) {
        double camX = camera.getPosition().x;
        double camZ = camera.getPosition().z;
        double firmHeight = firmament.getY();
        return new AABB(camX - 512, firmHeight - 1, camZ - 512, camX + 512, firmHeight + 1, camZ + 512);
    }

    private static void renderSky(LevelRenderer levelRenderer, Matrix4f projectionMatrix, Matrix4f positionMatrix) {
        RenderTarget firmamentFrameBuffer = ((LevelRendererDuck) levelRenderer).operation_starcleave$getFirmamentSkyFramebuffer();
        firmamentFrameBuffer.setClearColor(0f, 0.08f, 0.08f, 1f);
        firmamentFrameBuffer.clear(Minecraft.ON_OSX);
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true); // make sure to set viewport again

        OperationStarcleaveRenderTypes.FIRMAMENT_SKY_TARGET.setupRenderState();

        PoseStack matrixStack = new PoseStack();
        matrixStack.mulPose(positionMatrix);
        FirmamentSkyRenderer.getInstance().renderFirmamentSky(matrixStack, projectionMatrix, System.currentTimeMillis());

        OperationStarcleaveRenderTypes.FIRMAMENT_SKY_TARGET.clearRenderState();
    }

    private static boolean useDebugMode() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if(player == null) {
            return false;
        } else {
            return !minecraft.options.hideGui && minecraft.getDebugOverlay().showDebugScreen() && !player.isSpectator() && player.getMainHandItem().is(OperationStarcleaveItems.FIRMAMENT_MANIPULATOR);
        }
    }

    private static void renderFracture(LevelRenderer levelRenderer, Firmament firmament, Camera camera, Matrix4f projectionMatrix, Matrix4f positionMatrix) {
        boolean isDebugMode = useDebugMode();

        RenderType renderLayer = OperationStarcleaveRenderTypes.getFracture();
        renderLayer.setupRenderState();

        ShaderInstance shaderProgram = RenderSystem.getShader();
        if (shaderProgram != null) {
            // setup fog
            Minecraft minecraft = Minecraft.getInstance();
            float renderDistance = minecraft.gameRenderer.getRenderDistance();
            float farDistance = minecraft.gameRenderer.getDepthFar();

            float fogEnd = RenderSystem.getShaderFogEnd();
            float fogStart = RenderSystem.getShaderFogStart();
            FogShape fogShape = RenderSystem.getShaderFogShape();
            // if fog is distant then make it super distant, but if fog is close then leave it unchanged
            if (fogEnd >= renderDistance) {
                RenderSystem.setShaderFogEnd(farDistance);
                RenderSystem.setShaderFogStart(farDistance * 0.9F);
                RenderSystem.setShaderFogShape(FogShape.SPHERE);
            } else if (camera.getFluidInCamera() == FogType.WATER) {
                // make fog go somewhat further when in water
                RenderSystem.setShaderFogEnd(farDistance * 0.65F);
            }

            // setup firmament data texture
            int currentTexID0 = RenderSystem.getShaderTexture(0);

            DynamicTexture firmamentTex = FirmamentTextureStorage.fromLevelRenderer(levelRenderer).getTexture();
            RenderSystem.setShaderTexture(0, firmamentTex.getId());
            firmamentTex.bind();
            if(!isDebugMode) {
                RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
                RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            } else {
                RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            }

            // setup firmament sky texture
            int currentTexID1 = RenderSystem.getShaderTexture(1);

            RenderTarget firmamentFrameBuffer = ((LevelRendererDuck) levelRenderer).operation_starcleave$getFirmamentSkyFramebuffer();
            int firmamentSkyTexID = firmamentFrameBuffer.getColorTextureId();
            RenderSystem.setShaderTexture(1, firmamentSkyTexID);

            // setup uniforms
            float fadeOutEnd = Math.min(farDistance - 4, 500);
            float fadeOutStart = fadeOutEnd * 0.8F;

            Uniform uniform = shaderProgram.getUniform("FadeOutEnd");
            if(uniform != null) {
                uniform.set(fadeOutEnd);
            }
            uniform = shaderProgram.getUniform("FadeOutStart");
            if(uniform != null) {
                uniform.set(fadeOutStart);
            }
            uniform = shaderProgram.getUniform("IsDebugMode");
            if(uniform != null) {
                uniform.set(isDebugMode ? 1F : 0F);
            }

            // render firmament fractures
            shaderProgram.setDefaultUniforms(VertexFormat.Mode.QUADS, positionMatrix, projectionMatrix, Minecraft.getInstance().getWindow());
            shaderProgram.apply();

            MeshData meshData = createMeshData(camera, firmament);
            BufferUploader.draw(meshData);
            VertexBuffer.unbind();

            shaderProgram.clear();

            RenderSystem.setShaderTexture(0, currentTexID0);
            RenderSystem.setShaderTexture(1, currentTexID1);

            RenderSystem.setShaderFogEnd(fogEnd);
            RenderSystem.setShaderFogStart(fogStart);
            RenderSystem.setShaderFogShape(fogShape);
        }

        renderLayer.clearRenderState();
    }

    private static MeshData createMeshData(Camera camera, Firmament firmament) {
        Vec3 camPos = camera.getPosition();

        int height = firmament.getY();
        float y = (float) ((height + FIRMAMENT_HEIGHT_RENDER_OFFSET - camPos.y));

        RegionPos regionPos = RegionPos.fromWorldCoords(Mth.floor(camPos.x), Mth.floor(camPos.z));
        double relX = (regionPos.minWorldX - camPos.x);
        double relZ = (regionPos.minWorldZ - camPos.z);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                float x1 = (float) relX + (REGION_WIDTH * i);
                float x2 = x1 + REGION_WIDTH;
                float z1 = (float) relZ + (REGION_WIDTH * j);
                float z2 = z1 + REGION_WIDTH;

                float u1 = ((regionPos.rx + i) % REGIONS) * REGION_UV_SIZE;
                float v1 = ((regionPos.rz + j) % REGIONS) * REGION_UV_SIZE;
                float u2 = u1 + REGION_UV_SIZE;
                float v2 = v1 + REGION_UV_SIZE;

                drawFractureQuad(bufferBuilder, x1, x2, y, z1, z2, u1, u2, v1, v2);
                drawFractureQuad(bufferBuilder, x1, x2, y, z2, z1, u1, u2, v2, v1);
            }
        }

        return bufferBuilder.buildOrThrow();
    }

    private static void drawFractureQuad(BufferBuilder bufferBuilder, float x1, float x2, float y, float z1, float z2, float u1, float u2, float v1, float v2) {
        drawFractureVertex(bufferBuilder, x1, y, z1, u1, v1);
        drawFractureVertex(bufferBuilder, x2, y, z1, u2, v1);
        drawFractureVertex(bufferBuilder, x2, y, z2, u2, v2);
        drawFractureVertex(bufferBuilder, x1, y, z2, u1, v2);
    }

    private static void drawFractureVertex(BufferBuilder bufferBuilder, float x, float y, float z, float u, float v) {
        // TODO consider removing normal and lightmap data, as they don't seem to actually get used at all
        bufferBuilder.addVertex(x, y, z).setColor(255, 255, 255, 255).setUv(u, v).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 0, 0);
    }

    private static void renderFractureUsingBuffer(LevelRenderer levelRenderer, Firmament firmament, Camera camera, Matrix4f projectionMatrix, Matrix4f positionMatrix, ProfilerFiller profiler) {
        profiler.push("fracture_setup");
        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget mainTarget = minecraft.getMainRenderTarget();

        // write output to dummy buffer
        RenderTarget dummyBuffer = ((LevelRendererDuck) levelRenderer).operation_starcleave$getDummyFramebuffer();
        dummyBuffer.setClearColor(0f, 0f, 0f, 0f);
        dummyBuffer.clear(Minecraft.ON_OSX);
        dummyBuffer.bindWrite(true);

        dummyBuffer.copyDepthFrom(mainTarget);
        dummyBuffer.bindWrite(true);

        profiler.popPush("fracture");
        renderFracture(levelRenderer, firmament, camera, projectionMatrix, positionMatrix);

        profiler.popPush("copy_depth");
        mainTarget.bindWrite(true); // make sure to set viewport again

        mainTarget.copyDepthFrom(dummyBuffer);
        mainTarget.bindWrite(true);

        profiler.popPush("draw_texture");
        // render texture as world geometry
        RenderSystem.setShader(GameRenderer::getParticleShader);
        Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();

        ShaderInstance shaderProgram = RenderSystem.getShader();
        if (shaderProgram != null) {
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);

            // setup fog
            float fogEnd = RenderSystem.getShaderFogEnd();
            float fogStart = RenderSystem.getShaderFogStart();
            RenderSystem.setShaderFogStart(Float.MAX_VALUE);
            RenderSystem.setShaderFogEnd(Float.MAX_VALUE);

            // setup firmament sky texture
            int currentTexID0 = RenderSystem.getShaderTexture(1);

            int dummyBufTexID = dummyBuffer.getColorTextureId();
            RenderSystem.setShaderTexture(0, dummyBufTexID);

            // render geometry
            shaderProgram.setDefaultUniforms(VertexFormat.Mode.QUADS, positionMatrix, projectionMatrix, Minecraft.getInstance().getWindow());
            shaderProgram.apply();

            MeshData meshData = createProjectionMeshData(projectionMatrix, positionMatrix);
            BufferUploader.draw(meshData);
            VertexBuffer.unbind();

            shaderProgram.clear();

            RenderSystem.setShaderTexture(0, currentTexID0);

            RenderSystem.setShaderFogEnd(fogEnd);
            RenderSystem.setShaderFogStart(fogStart);

            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
        }

        RenderSystem.setShader(() -> null);
        Minecraft.getInstance().gameRenderer.lightTexture().turnOffLightLayer();
        profiler.pop();
    }

    private static MeshData createProjectionMeshData(Matrix4f projectionMatrix, Matrix4f positionMatrix) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);

        Matrix4f invertedProjectionMatrix = new Matrix4f();
        projectionMatrix.invert(invertedProjectionMatrix);

        Matrix4f invertedPositionMatrix = new Matrix4f();
        positionMatrix.invert(invertedPositionMatrix);

        drawProjectionVertex(bufferBuilder, invertedProjectionMatrix, invertedPositionMatrix, 0, 0);
        drawProjectionVertex(bufferBuilder, invertedProjectionMatrix, invertedPositionMatrix, 1, 0);
        drawProjectionVertex(bufferBuilder, invertedProjectionMatrix, invertedPositionMatrix, 1, 1);
        drawProjectionVertex(bufferBuilder, invertedProjectionMatrix, invertedPositionMatrix, 0, 1);

        return bufferBuilder.buildOrThrow();
    }

    private static void drawProjectionVertex(BufferBuilder bufferBuilder, Matrix4f invertedProjectionMatrix, Matrix4f invertedPositionMatrix, float u, float v) {
        float x = u * 2 - 1;
        float y = v * 2 - 1;

        Vector4f vec = new Vector4f(x, y, -0.25F, 1F);

        vec.mul(invertedProjectionMatrix);
        vec.div(vec.w());

        vec.mul(invertedPositionMatrix);

        drawProjectionFractureVertex(bufferBuilder, vec.x, vec.y, vec.z, u, v);
    }

    private static void drawProjectionFractureVertex(BufferBuilder bufferBuilder, float x, float y, float z, float u, float v) {
        bufferBuilder.addVertex(x, y, z).setUv(u, v).setColor(255, 255, 255, 255).setLight(LightTexture.FULL_BRIGHT);
    }
}
