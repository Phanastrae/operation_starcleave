package phanastrae.operation_starcleave.client.render;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import phanastrae.operation_starcleave.OperationStarcleave;

public class GradientsTexture implements AutoCloseable {
    public static final ResourceLocation LOCATION = OperationStarcleave.id("gradients_texture");
    private static final int WIDTH = 128;
    private static final int GRADIENTS = 2;

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
                int color;
                if(j == 0) {
                    // black and magenta missing gradient
                    color = (((i >> 2) & 0x1) == 0) ? 0x000000FF : 0xFF00FFFF;
                } else {
                    color = getBismuthIridescenceColorABGR(i / (float) WIDTH);
                }

                this.pixels.setPixelRGBA(i, j, color);
            }
        }

        this.texture.upload();
    }

    private int getBismuthIridescenceColorABGR(float f) {
        double dot = 2 * f - 1; // f = 0 => dot = -1, f = 1 => dot = +1

        double angle = Math.TAU * -2. * (1. + (dot < 0 ? dot : dot * -0.125));

        double r = Math.sin(angle) * 0.4 + 0.6;
        double g = Math.sin(angle + Math.TAU / 3.0) * 0.4 + 0.6;
        double b = Math.sin(angle - Math.TAU / 3.0) * 0.4 + 0.6;

        double a = (1. + Math.min(0., dot)); // dot = 1 => a = 0, dot <= 0 => a = 1

        return FastColor.ABGR32.color(
                (int) (255 * a),
                (int) (255 * b),
                (int) (255 * g),
                (int) (255 * r)
        );
    }
}
