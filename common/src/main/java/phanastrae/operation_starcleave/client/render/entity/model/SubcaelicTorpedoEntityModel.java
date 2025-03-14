package phanastrae.operation_starcleave.client.render.entity.model;

import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import phanastrae.operation_starcleave.entity.mob.SubcaelicTorpedoEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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

    public static LayerDefinition getOverlayTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition root = modelData.getRoot();

        root.addOrReplaceChild(
                PartNames.BODY,
                CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -8.0F, -6.0F, 12.0F, 16.0F, 12.0F, new CubeDeformation(0.52F)),
                PartPose.offset(0.0F, 8.0F, 0.0F)
        );

        PartDefinition tentacleRoot = root.addOrReplaceChild(
                TENTACLE_ROOT,
                CubeListBuilder.create(),
                PartPose.ZERO
        );

        CubeListBuilder modelPartBuilder = CubeListBuilder.create().texOffs(48, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 18.0F, 2.0F, new CubeDeformation(0.5F));
        for(int k = 0; k < 7; ++k) {
            double d1 = (double)k * Math.PI * 2.0 / 7.0;
            float f = (float)Math.cos(d1) * 5.0F;
            float h = (float)Math.sin(d1) * 5.0F;

            double d2 = (double)k * Math.PI * -2.0 / 7.0 + (Math.PI / 2);
            float l = (float)d2;
            tentacleRoot.addOrReplaceChild(getTentacleName(k), modelPartBuilder, PartPose.offsetAndRotation(f, 15.0F, h, 0.0F, l, 0.0F));
        }

        return LayerDefinition.create(modelData, 64, 32);
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition root = modelData.getRoot();

        root.addOrReplaceChild(
                PartNames.BODY,
                CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -8.0F, -6.0F, 12.0F, 16.0F, 12.0F, new CubeDeformation(0.02F)),
                PartPose.offset(0.0F, 8.0F, 0.0F)
        );

        PartDefinition tentacleRoot = root.addOrReplaceChild(
                TENTACLE_ROOT,
                CubeListBuilder.create(),
                PartPose.ZERO
        );

        CubeListBuilder modelPartBuilder = CubeListBuilder.create().texOffs(48, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 18.0F, 2.0F);
        for(int k = 0; k < 7; ++k) {
            double d1 = (double)k * Math.PI * 2.0 / 7.0;
            float f = (float)Math.cos(d1) * 5.0F;
            float h = (float)Math.sin(d1) * 5.0F;

            double d2 = (double)k * Math.PI * -2.0 / 7.0 + (Math.PI / 2);
            float l = (float)d2;
            tentacleRoot.addOrReplaceChild(getTentacleName(k), modelPartBuilder, PartPose.offsetAndRotation(f, 15.0F, h, 0.0F, l, 0.0F));
        }

        return LayerDefinition.create(modelData, 64, 32);
    }

    @Override
    public void setupAnim(SubcaelicTorpedoEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        for(ModelPart tentacle : this.tentacles) {
            tentacle.xRot = 0.1f * animationProgress;
        }
    }

    @Override
    public void prepareMobModel(SubcaelicTorpedoEntity entity, float limbAngle, float limbDistance, float tickDelta) {
        this.tentacleRoot.yRot = -(float)Math.toRadians(Mth.rotLerp(tickDelta, entity.prevTentacleRollAngle, entity.tentacleRollAngle));
    }

    @Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        this.root.render(matrices, vertices, light, overlay, color);
    }
}
