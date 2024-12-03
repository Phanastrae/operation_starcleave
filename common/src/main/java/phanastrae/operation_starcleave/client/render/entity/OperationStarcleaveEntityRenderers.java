package phanastrae.operation_starcleave.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;

public class OperationStarcleaveEntityRenderers {

    public static void init(EntityRendererAcceptor r) {
        r.accept(OperationStarcleaveEntityTypes.STARCLEAVER_GOLEM, StarcleaverGolemEntityRenderer::new);
        r.accept(OperationStarcleaveEntityTypes.SUBCAELIC_TORPEDO, SubcaelicTorpedoEntityRenderer::new);
        r.accept(OperationStarcleaveEntityTypes.SUBCAELIC_DUX, SubcaelicDuxEntityRenderer::new);

        r.accept(OperationStarcleaveEntityTypes.SPLASH_STARBLEACH, ThrownItemRenderer::new);
        r.accept(OperationStarcleaveEntityTypes.STARBLEACHED_PEARL, ThrownItemRenderer::new);
        r.accept(OperationStarcleaveEntityTypes.FIRMAMENT_REJUVENATOR, ThrownItemRenderer::new);
    }

    @FunctionalInterface
    public interface EntityRendererAcceptor {
        <T extends Entity> void accept(EntityType<? extends T> type, EntityRendererProvider<T> entityRendererProvider);
    }
}
