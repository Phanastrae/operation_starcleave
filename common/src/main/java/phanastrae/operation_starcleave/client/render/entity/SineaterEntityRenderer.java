package phanastrae.operation_starcleave.client.render.entity;

import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.client.render.entity.layers.SineaterOuterLayer;
import phanastrae.operation_starcleave.client.render.entity.model.OperationStarcleaveEntityModelLayers;
import phanastrae.operation_starcleave.client.render.entity.model.SineaterEntityModel;
import phanastrae.operation_starcleave.entity.mob.SineaterEntity;

import java.util.Map;

public class SineaterEntityRenderer extends MobRenderer<SineaterEntity, SineaterEntityModel<SineaterEntity>> {
    private static final Map<SineaterEntity.SineaterType, ResourceLocation> TEXTURE_BY_TYPE = Util.make(
            Maps.newHashMap(),
            map -> {
                for (SineaterEntity.SineaterType variant : SineaterEntity.SineaterType.values()) {
                    map.put(
                            variant,
                            OperationStarcleave.id("textures/entity/sineater/type/" + variant.getName() + ".png")
                    );
                }
            }
    );

    public SineaterEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new SineaterEntityModel<>(context.bakeLayer(OperationStarcleaveEntityModelLayers.SINEATER)), 1.2f);
        this.addLayer(new SineaterOuterLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(SineaterEntity entity) {
        return TEXTURE_BY_TYPE.get(entity.getSineaterType());
    }
}
