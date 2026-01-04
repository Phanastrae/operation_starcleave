package phanastrae.operation_starcleave.client.render.entity;

import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.client.render.entity.model.OperationStarcleaveEntityModelLayers;
import phanastrae.operation_starcleave.client.render.entity.model.SineaterEntityModel;
import phanastrae.operation_starcleave.entity.mob.SineaterEntity;

import java.util.Locale;
import java.util.Map;

public class SineaterEntityRenderer extends MobRenderer<SineaterEntity, SineaterEntityModel<SineaterEntity>> {
    private static final Map<SineaterEntity.Variant, ResourceLocation> TEXTURE_BY_TYPE = Util.make(
            Maps.newHashMap(),
            map -> {
                for (SineaterEntity.Variant variant : SineaterEntity.Variant.values()) {
                    map.put(
                            variant,
                            OperationStarcleave.id(String.format(Locale.ROOT, "textures/entity/sineater/sineater_%s.png", variant.getName()))
                    );
                }
            }
    );

    public SineaterEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new SineaterEntityModel<>(context.bakeLayer(OperationStarcleaveEntityModelLayers.SINEATER)), 1.2f);
    }

    @Override
    public ResourceLocation getTextureLocation(SineaterEntity entity) {
        return TEXTURE_BY_TYPE.get(entity.getVariant());
    }
}
