package phanastrae.operation_starcleave.render.entity;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;

public class OperationStarcleaveEntityRenderers {

    public static void init() {
        EntityRendererRegistry.register(OperationStarcleaveEntityTypes.STARCLEAVER_GOLEM, StarcleaverGolemEntityRenderer::new);
        EntityRendererRegistry.register(OperationStarcleaveEntityTypes.SUBCAELIC_TORPEDO, SubcaelicTorpedoEntityRenderer::new);
        EntityRendererRegistry.register(OperationStarcleaveEntityTypes.SUBCAELIC_DUX, SubcaelicDuxEntityRenderer::new);

        EntityRendererRegistry.register(OperationStarcleaveEntityTypes.SPLASH_STARBLEACH, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(OperationStarcleaveEntityTypes.STARBLEACHED_PEARL, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(OperationStarcleaveEntityTypes.FIRMAMENT_REJUVENATOR, FlyingItemEntityRenderer::new);
    }
}
