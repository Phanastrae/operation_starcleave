package phanastrae.operation_starcleave.render.firmament;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import org.joml.Math;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.FirmamentRegion;

public class FirmamentRenderer {
    public static void render(WorldRenderContext worldRenderContext) {
        VertexConsumerProvider vertexConsumerProvider = worldRenderContext.consumers();
        if(vertexConsumerProvider == null) return;

        Profiler profiler = MinecraftClient.getInstance().getProfiler();
        profiler.push("starcleave_fracture");

        Sprite texture = MinecraftClient.getInstance().getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(new Identifier("block/white_concrete"));

        MatrixStack matrixStack = worldRenderContext.matrixStack();

        matrixStack.push();
        Vec3d camPos = worldRenderContext.camera().getPos();
        matrixStack.translate(-camPos.x, -camPos.y, -camPos.z);

        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getSolid());

        FirmamentRegion fr = Firmament.getInstance().firmamentRegion;

        Entity e = MinecraftClient.getInstance().cameraEntity;
        if(e != null) {
            int ex = e.getBlockX();
            int ez = e.getBlockZ();
            fr.forEachPosition((x, z) -> {
                int dx = x - ex;
                int dz = z - ez;
                if(dx*dx+dz*dz>96*96) return;

                float damage = Math.clamp(0, 1, fr.getDamage(x, z));

                float displacementY = fr.getDisplacement(x, z);

                float weightedDrip = (float)Math.max(java.lang.Math.log1p(Math.abs(fr.getDrip(x, z))), 0);

                boolean updated = fr.getActive(x, z) && MinecraftClient.getInstance().getEntityRenderDispatcher().shouldRenderHitboxes();
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
                        x + f, z + f,
                        x + 1 - f, z + 1 - f,
                        256 + displacementY,
                        r,
                        g,
                        b,
                        1,
                        texture.getMinU(), texture.getMinV(), texture.getMaxU(), texture.getMaxV(),
                        LightmapTextureManager.MAX_LIGHT_COORDINATE, -1);
            });
        }

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
