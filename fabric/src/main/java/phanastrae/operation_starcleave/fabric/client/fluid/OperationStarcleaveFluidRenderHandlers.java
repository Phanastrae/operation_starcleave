package phanastrae.operation_starcleave.fabric.client.fluid;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.world.level.material.Fluid;
import phanastrae.operation_starcleave.client.fluid.OperationStarcleaveClientFluids;

public class OperationStarcleaveFluidRenderHandlers {

    public static void init() {
        OperationStarcleaveClientFluids.init();

        OperationStarcleaveClientFluids.forEachXPGCF(xpgcf -> {
            Fluid still = xpgcf.getStill();
            Fluid flow = xpgcf.getFlow();
            FluidRenderHandler handler = getFluidRenderHandler(xpgcf);

            FluidRenderHandlerRegistry.INSTANCE.register(still, handler);
            if (flow != null) {
                FluidRenderHandlerRegistry.INSTANCE.register(flow, handler);
            }
        });
    }

    public static FluidRenderHandler getFluidRenderHandler(OperationStarcleaveClientFluids.XPlatGenericClientFluid xpgcf) {
        return new SimpleFluidRenderHandler(
                xpgcf.getStillTexture(),
                xpgcf.getFlowTexture(),
                xpgcf.getOverlayTexture(),
                xpgcf.getTint()
        );
    }
}
