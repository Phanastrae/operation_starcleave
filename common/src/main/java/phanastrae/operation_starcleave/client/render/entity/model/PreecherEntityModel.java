package phanastrae.operation_starcleave.client.render.entity.model;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class PreecherEntityModel<T extends Entity> extends HierarchicalModel<T> {

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart cloak;
    private final ModelPart head;
    private final ModelPart right_front_leg;
    private final ModelPart left_front_leg;
    private final ModelPart right_back_leg;
    private final ModelPart left_back_leg;

    public PreecherEntityModel(ModelPart root) {
        this.root = root;

        this.body = root.getChild("body");

        this.cloak = this.body.getChild("cloak");
        this.head = this.body.getChild("head");

        this.right_front_leg = root.getChild("right_front_leg");
        this.left_front_leg = root.getChild("left_front_leg");
        this.right_back_leg = root.getChild("right_back_leg");
        this.left_back_leg = root.getChild("left_back_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(0, 16)
                        .addBox(-4.0F, -12.0F, -2.5F, 8.0F, 12.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 18.0F, 0.5F, 0.0873F, 0.0F, 0.0F)
        );

        PartDefinition cloak = body.addOrReplaceChild(
                "cloak",
                CubeListBuilder.create()
                        .texOffs(24, 16)
                        .addBox(-5.0F, 0.0F, -4.5F, 10.0F, 14.0F, 6.0F, new CubeDeformation(0.5F)),
                PartPose.offsetAndRotation(0.0F, -11.5F, 1.0F, 0.1309F, 0.0F, 0.0F)
        );

        PartDefinition head = body.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F),
                PartPose.offsetAndRotation(0.0F, -12.0F, -0.5F, -0.0873F, 0.0F, 0.0F)
        );
        PartDefinition head_cover = head.addOrReplaceChild(
                "head_cover",
                CubeListBuilder.create()
                        .texOffs(32, 0)
                        .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.25F, 0.0873F, 0.0F, 0.0F)
        );

        PartDefinition right_front_leg = root.addOrReplaceChild(
                "right_front_leg",
                CubeListBuilder.create()
                        .texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F),
                PartPose.offsetAndRotation(-3.0F, 18.0F, -2.0F, 0.0F, 0.0873F, 0.0F)
        );

        PartDefinition left_front_leg = root.addOrReplaceChild(
                "left_front_leg",
                CubeListBuilder.create()
                        .texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F),
                PartPose.offsetAndRotation(3.0F, 18.0F, -2.0F, 0.0F, -0.0873F, 0.0F)
        );

        PartDefinition right_back_leg = root.addOrReplaceChild(
                "right_back_leg",
                CubeListBuilder.create()
                        .texOffs(0, 32).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 6.0F, 4.0F),
                PartPose.offsetAndRotation(-3.0F, 18.0F, 2.0F, 0.0F, -0.0873F, 0.0F)
        );

        PartDefinition left_back_leg = root.addOrReplaceChild(
                "left_back_leg",
                CubeListBuilder.create()
                        .texOffs(0, 32).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 6.0F, 4.0F),
                PartPose.offsetAndRotation(3.0F, 18.0F, 2.0F, 0.0F, 0.0873F, 0.0F)
        );

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * (float) (Math.PI / 180.0);
        this.head.xRot = headPitch * (float) (Math.PI / 180.0) - this.body.xRot;

        this.right_back_leg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.left_back_leg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.right_front_leg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.left_front_leg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
    }
}
