package phanastrae.operation_starcleave.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.client.render.entity.layers.BismuthIridescenceLayer;
import phanastrae.operation_starcleave.client.render.entity.layers.GlowLayer;
import phanastrae.operation_starcleave.client.render.entity.model.HammertailGolemEntityModel;
import phanastrae.operation_starcleave.client.render.entity.model.OperationStarcleaveEntityModelLayers;
import phanastrae.operation_starcleave.entity.mob.HammertailGolemEntity;

public class HammertailGolemEntityRenderer extends MobRenderer<HammertailGolemEntity, HammertailGolemEntityModel<HammertailGolemEntity>> {
    public static final ResourceLocation TEXTURE = OperationStarcleave.id("textures/entity/hammertail_golem/hammertail_golem.png");
    public static final ResourceLocation GLOW_TEXTURE = OperationStarcleave.id("textures/entity/hammertail_golem/hammertail_golem_glow.png");
    public static final ResourceLocation IRIDESCENCE_TEXTURE = OperationStarcleave.id("textures/entity/hammertail_golem/hammertail_golem_iridescence.png");

    public HammertailGolemEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new HammertailGolemEntityModel<>(context.bakeLayer(OperationStarcleaveEntityModelLayers.HAMMERTAIL_GOLEM)), 0.3f);
        this.addLayer(new BismuthIridescenceLayer<>(this, IRIDESCENCE_TEXTURE, HammertailGolemEntityModel::getIridescentParts));
        this.addLayer(new GlowLayer<>(this, GLOW_TEXTURE, HammertailGolemEntityModel::getGlowingParts));
    }

    @Override
    public ResourceLocation getTextureLocation(HammertailGolemEntity entity) {
        return TEXTURE;
    }
}
