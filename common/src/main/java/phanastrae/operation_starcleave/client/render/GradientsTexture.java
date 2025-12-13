package phanastrae.operation_starcleave.client.render;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.client.render.extras_baking.Iridescence;

public class GradientsTexture implements AutoCloseable {
    public static final ResourceLocation LOCATION = OperationStarcleave.id("gradients_texture");
    private static final int WIDTH = 128;
    private static final int GRADIENTS = 3;

    private final DynamicTexture texture;
    private final NativeImage pixels;
    private final ResourceLocation textureLocation;

    public GradientsTexture(Minecraft minecraft) {
        this.texture = new DynamicTexture(WIDTH, GRADIENTS, false);

        this.textureLocation = LOCATION;
        minecraft.getTextureManager().register(this.textureLocation, this.texture);

        this.pixels = this.texture.getPixels();

        updateTexture();
    }

    @Override
    public void close() {
        this.texture.close();
    }

    public void updateTexture() {
        for (int j = 0; j < GRADIENTS; j++) {
            for (int i = 0; i < WIDTH; i++) {
                float progress = i / (float) WIDTH;

                int color;
                if (j == Iridescence.getBismuthIridescenceId()) {
                    color = getBismuthIridescenceColorABGR(progress);
                } else if (j == Iridescence.getOpalIridescenceId()) {
                    color = getOpalIridescenceColorABGR(progress);
                } else {
                    // black and magenta missing gradient
                    color = (((i >> 2) & 0x1) == 0) ? 0x000000FF : 0xFF00FFFF;
                }

                this.pixels.setPixelRGBA(i, j, color);
            }
        }

        this.texture.upload();
    }

    private static int getBismuthIridescenceColorABGR(float progress) {
        double dot = getDotFromProgress(progress);
        double colorAngle = Math.TAU * -2. * (1. + (dot < 0 ? dot : dot * -0.125));

        double r = wave(colorAngle, 0.0, 0.2, 1.0);
        double g = wave(colorAngle, 1.0 / 3.0, 0.2, 1.0);
        double b = wave(colorAngle, -1.0 / 3.0, 0.2, 1.0);

        double a = (1. + Math.min(0., dot)); // dot = 1 => a = 0, dot <= 0 => a = 1

        return packABGR(a, b, g, r);
    }

    private static int getOpalIridescenceColorABGR(float progress) {
        double dot = getDotFromProgress(progress);
        double colorAngle = Math.TAU * -2. * (1. + (dot < 0 ? dot : dot * -0.125));

        double a = (1. + Math.min(0., dot)); // dot = 1 => a = 0, dot <= 0 => a = 1

        double r = wave(colorAngle, 0.0, 0.65, 1.0) * (0.65 + 0.35 * a);
        double g = wave(colorAngle, 4.0 / 9.0, 0.65, 1.0) * (0.65 + 0.35 * a);
        double b = wave(colorAngle, -1.0 / 9.0, 0.65, 1.0) * (1.0 - 0.4 * a);

        a = Math.sqrt(a);

        return packABGR(a, b, g, r);
    }

    private static double getDotFromProgress(float progress) {
        return 2 * progress - 1; // progress = 0 => dot = -1, progress = 1 => dot = +1
    }

    private static double wave(double colorAngle, double periodOffset, double min, double max) {
        double average = (max + min) * 0.5;
        double maxDifFromAverage = (max - min) * 0.5;
        return Math.sin(colorAngle + Math.TAU * periodOffset) * maxDifFromAverage + average;
    }

    private static int packABGR(double a, double b, double g, double r) {
        return FastColor.ABGR32.color(
                (int) (255 * a),
                (int) (255 * b),
                (int) (255 * g),
                (int) (255 * r)
        );
    }
}
