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
import phanastrae.operation_starcleave.entity.projectile.BismuthBlastEntity;

public class BismuthBlastEntityModel<T extends BismuthBlastEntity> extends EntityModel<T> {
    private final ModelPart outer;
    private final ModelPart inner;

    public BismuthBlastEntityModel(ModelPart root) {
        this.outer = root.getChild("outer");
        this.inner = root.getChild("inner");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition outer = root.addOrReplaceChild("outer", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.0F, -4.0F, -4.0F, 4.0F, 4.0F, 5.0F), PartPose.offset(0.0F, 24.0F, 0.0F)
        );
        PartDefinition inner = root.addOrReplaceChild("inner", CubeListBuilder.create()
                .texOffs(0, 9).addBox(-1.5F, -3.5F, -2.0F, 3.0F, 3.0F, 6.0F), PartPose.offset(0.0F, 24.0F, 0.0F)
        );

        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.outer.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.inner.render(poseStack, buffer, packedLight, packedOverlay, color);
    }

    public ModelPart getOuter() {
        return this.outer;
    }

    public ModelPart getInner() {
        return this.inner;
    }
}
