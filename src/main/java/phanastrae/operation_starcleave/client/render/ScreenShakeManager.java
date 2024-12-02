package phanastrae.operation_starcleave.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

public class ScreenShakeManager {
    private static final ScreenShakeManager instance = new ScreenShakeManager();
    public static ScreenShakeManager getInstance() {
        return instance;
    }

    public float targetShakeAmount = 0;
    public float shakeAmount = 0;
    public float prevShakeAmount = 0;

    public void update() {
        this.targetShakeAmount -= targetShakeAmount * targetShakeAmount * targetShakeAmount * 0.01 + 0.01;
        if(this.targetShakeAmount <= 0) this.targetShakeAmount = 0;

        this.prevShakeAmount = this.shakeAmount;
        this.shakeAmount += (this.targetShakeAmount - this.shakeAmount) * 0.2;
        if(this.shakeAmount < 0.01) {
            this.shakeAmount = 0;
        }
    }

    public void setShakeAmount(int shakeAmount) {
        this.targetShakeAmount = shakeAmount;
    }

    public float getCurrentShakeAmount(float tickDelta) {
        return this.prevShakeAmount + (this.shakeAmount - this.prevShakeAmount) * tickDelta;
    }

    public void updateScreenMatrices(MatrixStack matrixStack, float tickDelta) {
        float shake = this.getCurrentShakeAmount(tickDelta);
        if(shake > 0) {
            float t2 = getTime() / 20f + tickDelta;
            double tpi = 2 * Math.PI;
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)Math.sin(3 * t2 * tpi) * shake));
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float)Math.sin(2 * t2 * tpi + 0.5) * shake));
        }

    }

    public long getTime() {
        World world = MinecraftClient.getInstance().world;
        if(world == null) return 0;
        return world.getTime();
    }
}
