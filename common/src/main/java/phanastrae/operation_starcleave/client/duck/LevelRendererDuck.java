package phanastrae.operation_starcleave.client.duck;

import com.mojang.blaze3d.pipeline.RenderTarget;
import phanastrae.operation_starcleave.client.render.firmament.FirmamentTextureStorage;

public interface LevelRendererDuck {
    RenderTarget operation_starcleave$getDummyFramebuffer();
    RenderTarget operation_starcleave$getFirmamentSkyFramebuffer();

    FirmamentTextureStorage operation_starcleave$getFirmamentTextureStorage();
}
