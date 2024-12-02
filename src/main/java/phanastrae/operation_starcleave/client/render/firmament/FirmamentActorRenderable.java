package phanastrae.operation_starcleave.client.render.firmament;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public interface FirmamentActorRenderable {
    void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, float tickDelta, Camera camera);
}
