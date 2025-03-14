package phanastrae.operation_starcleave.client.render.firmament;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;

public interface FirmamentActorRenderable {
    void render(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, float tickDelta, Camera camera);
}
