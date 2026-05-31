package phanastrae.operation_starcleave.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;

public class OperationStarcleaveEntityRenderers {

    public static void init(EntityRendererAcceptor r) {
        // mobs
        r.accept(OperationStarcleaveEntityTypes.STARCLEAVER_GOLEM, StarcleaverGolemEntityRenderer::new);
        r.accept(OperationStarcleaveEntityTypes.SUBCAELIC_TORPEDO, SubcaelicTorpedoEntityRenderer::new);
        r.accept(OperationStarcleaveEntityTypes.SUBCAELIC_DUX, SubcaelicDuxEntityRenderer::new);
        r.accept(OperationStarcleaveEntityTypes.SINEATER, SineaterEntityRenderer::new);
        r.accept(OperationStarcleaveEntityTypes.TRACTORBLOOM, TractorbloomEntityRenderer::new);
        r.accept(OperationStarcleaveEntityTypes.HAMMERTAIL_GOLEM, HammertailGolemEntityRenderer::new);
        r.accept(OperationStarcleaveEntityTypes.PREECHER, PreecherEntityRenderer::new);

        // projectiles
        r.accept(OperationStarcleaveEntityTypes.SPLASH_STARBLEACH, ThrownItemRenderer::new);
        r.accept(OperationStarcleaveEntityTypes.STARBLEACHED_PEARL, ThrownItemRenderer::new);
        r.accept(OperationStarcleaveEntityTypes.FIRMAMENT_REJUVENATOR, ThrownItemRenderer::new);
        r.accept(OperationStarcleaveEntityTypes.PHLOGISTIC_SPARK, InvisibleRenderer::new);
        r.accept(OperationStarcleaveEntityTypes.NUCLEAR_STARDROP, InvisibleRenderer::new);
        r.accept(OperationStarcleaveEntityTypes.BISMUTH_BLAST, BismuthBlastEntityRenderer::new);

        // misc
        r.accept(OperationStarcleaveEntityTypes.STARBLEACH_CHARGE, InvisibleRenderer::new);
        r.accept(OperationStarcleaveEntityTypes.NUCLEAR_STORMCLOUD, InvisibleRenderer::new);
        r.accept(OperationStarcleaveEntityTypes.NUCLEOSYNTHESEED, NucleosyntheseedEntityRenderer::new);
    }

    @FunctionalInterface
    public interface EntityRendererAcceptor {
        <T extends Entity> void accept(EntityType<? extends T> type, EntityRendererProvider<T> entityRendererProvider);
    }
}
