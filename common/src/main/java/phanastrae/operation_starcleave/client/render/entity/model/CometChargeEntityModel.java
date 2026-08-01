package phanastrae.operation_starcleave.client.render.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import phanastrae.operation_starcleave.entity.projectile.CometChargeEntity;

public class CometChargeEntityModel<T extends CometChargeEntity> extends EntityModel<T> {
    private final ModelPart body;
    private final ModelPart tail;

    public CometChargeEntityModel(ModelPart root) {
        this.body = root.getChild("body");
        this.tail = root.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F),
                PartPose.offset(0.0F, 21.0F, 0.0F)
        );

        PartDefinition tail = root.addOrReplaceChild("tail", CubeListBuilder.create(),
                PartPose.offset(0.0F, 21.0F, 3.0F)
        );
        tail.addOrReplaceChild("tail1", CubeListBuilder.create()
                        .texOffs(0, 12).addBox(0.0F, -2.0F, 0.0F, 0.0F, 4.0F, 8.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7854F)
        );
        tail.addOrReplaceChild("tail2", CubeListBuilder.create()
                        .texOffs(0, 12).addBox(0.0F, -2.0F, 0.0F, 0.0F, 4.0F, 8.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F)
        );

        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.body.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.tail.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
