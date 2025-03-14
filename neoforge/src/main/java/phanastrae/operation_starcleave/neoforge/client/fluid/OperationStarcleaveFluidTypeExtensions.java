package phanastrae.operation_starcleave.neoforge.client.fluid;

import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import phanastrae.operation_starcleave.client.fluid.OperationStarcleaveClientFluids;

import java.util.function.BiConsumer;

public class OperationStarcleaveFluidTypeExtensions {

    public static void init(BiConsumer<IClientFluidTypeExtensions, FluidType[]> biConsumer) {
        OperationStarcleaveClientFluids.init();

        OperationStarcleaveClientFluids.forEachXPGCF(xpgcf -> {
            biConsumer.accept(getICFTE(xpgcf), new FluidType[]{xpgcf.getStill().getFluidType()});
        });
    }

    public static IClientFluidTypeExtensions getICFTE(OperationStarcleaveClientFluids.XPlatGenericClientFluid xpgcf) {
        ResourceLocation stillTexture = xpgcf.getStillTexture();
        ResourceLocation flowingTexture = xpgcf.getFlowTexture();
        ResourceLocation overlayTexture = xpgcf.getOverlayTexture();
        ResourceLocation screenEffectTexture = xpgcf.getScreenEffectTexture();
        int tint = xpgcf.getTint();

        return new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return stillTexture;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return flowingTexture;
            }

            @Override
            @Nullable
            public ResourceLocation getOverlayTexture() {
                return overlayTexture;
            }

            @Override
            @Nullable
            public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
                return screenEffectTexture;
            }

            @Override
            public int getTintColor() {
                return tint;
            }

            @Override
            public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
                xpgcf.setupFog(farDistance);
            }

            @Override
            public Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
                return xpgcf.getFogColor();
            }
        };
    }
}
