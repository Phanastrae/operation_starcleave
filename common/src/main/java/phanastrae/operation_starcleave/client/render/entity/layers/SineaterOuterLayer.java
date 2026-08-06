package phanastrae.operation_starcleave.client.render.entity.layers;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.client.render.entity.model.SineaterEntityModel;
import phanastrae.operation_starcleave.entity.mob.SineaterEntity;

import java.util.Map;

public class SineaterOuterLayer<T extends SineaterEntity, M extends SineaterEntityModel<T>> extends RenderLayer<T, M> {
    private static final Map<SineaterEntity.SineaterCoat, ResourceLocation> TEXTURE_BY_TYPE = Util.make(
            Maps.newHashMap(),
            map -> {
                for (SineaterEntity.SineaterCoat variant : SineaterEntity.SineaterCoat.values()) {
                    map.put(
                            variant,
                            OperationStarcleave.id("textures/entity/sineater/coat/" + variant.getName() + ".png")
                    );
                }
            }
    );

    public SineaterOuterLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T sineater, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!sineater.isInvisible()) {
            M model = this.getParentModel();

            model.setBodyVisbile(false);
            model.setOuterVisible(true);

            ResourceLocation location = this.getTextureLocation(sineater);
            renderColoredCutoutModel(model, location, poseStack, bufferSource, packedLight, sineater, -1);

            model.setBodyVisbile(true);
            model.setOuterVisible(false);
        }
    }

    @Override
    protected ResourceLocation getTextureLocation(T entity) {
        return TEXTURE_BY_TYPE.get(entity.getSineaterCoat());
    }
}
