package phanastrae.operation_starcleave.render.entity.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import phanastrae.operation_starcleave.entity.mob.SubcaelicTorpedoEntity;

import java.util.Arrays;

public class SubcaelicTorpedoEntityModel<T extends SubcaelicTorpedoEntity> extends EntityModel<T> {
    public static final String TENTACLE_ROOT = "tentacle_root";

    private final ModelPart root;
    private final ModelPart tentacleRoot;
    private final ModelPart[] tentacles = new ModelPart[7];

    public SubcaelicTorpedoEntityModel(ModelPart root) {
        this.root = root;
        this.tentacleRoot = root.getChild(TENTACLE_ROOT);
        Arrays.setAll(this.tentacles, index -> tentacleRoot.getChild(getTentacleName(index)));
    }

    private static String getTentacleName(int index) {
        return "tentacle" + index;
    }

    public static TexturedModelData getOverlayTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        root.addChild(
                EntityModelPartNames.BODY,
                ModelPartBuilder.create().uv(0, 0).cuboid(-6.0F, -8.0F, -6.0F, 12.0F, 16.0F, 12.0F, new Dilation(0.52F)),
                ModelTransform.pivot(0.0F, 8.0F, 0.0F)
        );

        ModelPartData tentacleRoot = root.addChild(
                TENTACLE_ROOT,
                ModelPartBuilder.create(),
                ModelTransform.NONE
        );

        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(48, 0).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 18.0F, 2.0F, new Dilation(0.5F));
        for(int k = 0; k < 7; ++k) {
            double d1 = (double)k * Math.PI * 2.0 / 7.0;
            float f = (float)Math.cos(d1) * 5.0F;
            float h = (float)Math.sin(d1) * 5.0F;

            double d2 = (double)k * Math.PI * -2.0 / 7.0 + (Math.PI / 2);
            float l = (float)d2;
            tentacleRoot.addChild(getTentacleName(k), modelPartBuilder, ModelTransform.of(f, 15.0F, h, 0.0F, l, 0.0F));
        }

        return TexturedModelData.of(modelData, 64, 32);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        root.addChild(
                EntityModelPartNames.BODY,
                ModelPartBuilder.create().uv(0, 0).cuboid(-6.0F, -8.0F, -6.0F, 12.0F, 16.0F, 12.0F, new Dilation(0.02F)),
                ModelTransform.pivot(0.0F, 8.0F, 0.0F)
        );

        ModelPartData tentacleRoot = root.addChild(
                TENTACLE_ROOT,
                ModelPartBuilder.create(),
                ModelTransform.NONE
        );

        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(48, 0).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 18.0F, 2.0F);
        for(int k = 0; k < 7; ++k) {
            double d1 = (double)k * Math.PI * 2.0 / 7.0;
            float f = (float)Math.cos(d1) * 5.0F;
            float h = (float)Math.sin(d1) * 5.0F;

            double d2 = (double)k * Math.PI * -2.0 / 7.0 + (Math.PI / 2);
            float l = (float)d2;
            tentacleRoot.addChild(getTentacleName(k), modelPartBuilder, ModelTransform.of(f, 15.0F, h, 0.0F, l, 0.0F));
        }

        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void setAngles(SubcaelicTorpedoEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        for(ModelPart tentacle : this.tentacles) {
            tentacle.pitch = 0.1f * animationProgress;
        }
    }

    @Override
    public void animateModel(SubcaelicTorpedoEntity entity, float limbAngle, float limbDistance, float tickDelta) {
        this.tentacleRoot.yaw = -(float)Math.toRadians(MathHelper.lerpAngleDegrees(tickDelta, entity.prevTentacleRollAngle, entity.tentacleRollAngle));
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        this.root.render(matrices, vertices, light, overlay, color);
    }
}
