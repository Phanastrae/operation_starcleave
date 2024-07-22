package phanastrae.operation_starcleave.world.firmament;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;
import phanastrae.operation_starcleave.render.OperationStarcleaveRenderLayers;
import phanastrae.operation_starcleave.render.firmament.FirmamentActorRenderable;

public class FirmamentDamageGlowActor extends FirmamentActor implements FirmamentActorRenderable {

    public FirmamentDamageGlowActor(Firmament firmament, int originX, int originZ) {
        super(firmament, originX, originZ);
    }

    int age = 0;

    public void tick() {
        age++;
        if(age > 100) {
            discard();
            return;
        }
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, float tickDelta, Camera camera) {
        double xOffset = this.originX + 0.5 - camera.getPos().x;
        double yOffset = this.firmament.getY() - camera.getPos().y;
        double zOffset = this.originZ + 0.5 - camera.getPos().z;

        double distance = Math.sqrt(xOffset*xOffset+zOffset*zOffset);
        if(distance > 512) return;
        float distanceMultiplier = (float)Math.min(1, Math.max(0, (512 - distance) / 256));
        distanceMultiplier *= distanceMultiplier;

        matrixStack.push();
        matrixStack.translate(xOffset, yOffset, zOffset);
        matrixStack.scale(8, 8, 8);

        float l = ((float)age + tickDelta) / 100.0F;
        matrixStack.scale(1+8*l*l, 1, 1 + 8*l*l);
        matrixStack.translate(0, (l+l*l), 0);
        float m = l * l;
        Random random = Random.create(432L);

        VertexConsumer vertexConsumer4 = vertexConsumerProvider.getBuffer(OperationStarcleaveRenderLayers.getSkyRay());

        float rayCount = (l + l * l) / 2.0F * 60.0F;
        rayCount *= 5;
        if(rayCount > 80) rayCount = 80;
        rayCount *= distanceMultiplier;
        for(int n = 0; (float)n < rayCount; ++n) {
            matrixStack.push();
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(random.nextFloat() * 360.0F + l * l * l * l * l * 8 * (random.nextFloat() + 2)));
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F + (90.0F - 60.0F * random.nextFloat()) * (l*l*l)));

            float radius = random.nextFloat() * 20.0F + 5.0F + m * 10.0F;
            float width = random.nextFloat() * 2.0F + 1.0F + m * 2.0F;
            Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
            int alpha = (int)(255.0F * (1.0F - m));
            alpha = (int)(alpha * (1-l*l) * distanceMultiplier * 0.1);

            float twopi = 2 * MathHelper.PI;
            float red = MathHelper.sin(n) * 0.2f + 0.8f;
            float green = MathHelper.sin(n + 1/3f * twopi) * 0.2f + 0.8f;
            float blue = MathHelper.sin( n + 2/3f * twopi) * 0.2f + 0.8f;

            int r = (int)(red * alpha) & 0xFF;
            int g = (int)(green * alpha) & 0xFF;
            int b = (int)(blue * alpha) & 0xFF;

            putDeathLightSourceVertex(vertexConsumer4, matrix4f, r, g, b);
            putDeathLightNegativeXTerminalVertex(vertexConsumer4, matrix4f, radius, width);
            putDeathLightPositiveXTerminalVertex(vertexConsumer4, matrix4f, radius, width);
            putDeathLightNegativeXTerminalVertex(vertexConsumer4, matrix4f, radius, width);

            putDeathLightSourceVertex(vertexConsumer4, matrix4f, r, g, b);
            putDeathLightNegativeXTerminalVertex(vertexConsumer4, matrix4f, radius, width);
            putDeathLightPositiveZTerminalVertex(vertexConsumer4, matrix4f, radius, width);
            putDeathLightNegativeXTerminalVertex(vertexConsumer4, matrix4f, radius, width);

            putDeathLightSourceVertex(vertexConsumer4, matrix4f, r, g, b);
            putDeathLightPositiveXTerminalVertex(vertexConsumer4, matrix4f, radius, width);
            putDeathLightPositiveZTerminalVertex(vertexConsumer4, matrix4f, radius, width);
            putDeathLightPositiveXTerminalVertex(vertexConsumer4, matrix4f, radius, width);

            matrixStack.pop();
        }

        matrixStack.pop();
    }

    private static final float HALF_SQRT_3 = (float)(Math.sqrt(3.0) / 2.0);


    private static void putDeathLightSourceVertex(VertexConsumer buffer, Matrix4f matrix, int r, int g, int b) {
        buffer.vertex(matrix, 0.0F, 0.0F, 0.0F).color(r, g, b, 255);
    }

    private static void putDeathLightNegativeXTerminalVertex(VertexConsumer buffer, Matrix4f matrix, float radius, float width) {
        buffer.vertex(matrix, -HALF_SQRT_3 * width, radius, -0.5F * width).color(0, 0, 0, 0);
    }

    private static void putDeathLightPositiveXTerminalVertex(VertexConsumer buffer, Matrix4f matrix, float radius, float width) {
        buffer.vertex(matrix, HALF_SQRT_3 * width, radius, -0.5F * width).color(0, 0, 0, 0);
    }

    private static void putDeathLightPositiveZTerminalVertex(VertexConsumer buffer, Matrix4f matrix, float radius, float width) {
        buffer.vertex(matrix, 0.0F, radius, 1.0F * width).color(0, 0, 0, 0);
    }
}
