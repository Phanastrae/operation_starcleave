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
import net.minecraft.util.Mth;
import phanastrae.operation_starcleave.entity.mob.TractorbloomEntity;

public class TractorbloomEntityModel<T extends TractorbloomEntity> extends EntityModel<T> {
    private final ModelPart body;
    private final ModelPart coreRoot;
    private final ModelPart core;
    private final ModelPart petals;
    private final ModelPart petalBack;
    private final ModelPart petalBackLeft;
    private final ModelPart petalBackRight;
    private final ModelPart petalLeft;
    private final ModelPart petalRight;
    private final ModelPart petalFrontLeft;
    private final ModelPart petalFrontRight;

    public TractorbloomEntityModel(ModelPart root) {
        this.body = root.getChild("body");

        this.coreRoot = this.body.getChild("core_root");
        this.core = this.coreRoot.getChild("core");

        this.petals = root.getChild("petals");
        this.petalBack = this.petals.getChild("petal_back");
        this.petalBackLeft = this.petals.getChild("petal_back_left");
        this.petalBackRight = this.petals.getChild("petal_back_right");
        this.petalLeft = this.petals.getChild("petal_left");
        this.petalRight = this.petals.getChild("petal_right");
        this.petalFrontLeft = this.petals.getChild("petal_front_left");
        this.petalFrontRight = this.petals.getChild("petal_front_right");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -10.0F, -8.0F, 16.0F, 8.0F, 16.0F)
                .texOffs(64, 0).addBox(-9.5F, -24.0F, -9.5F, 19.0F, 15.0F, 19.0F), PartPose.offset(0.0F, 26.0F, 0.0F));

        body.addOrReplaceChild("teeth", CubeListBuilder.create().texOffs(0, 24).addBox(-6.0F, -2.0F, -6.0F, 12.0F, 3.0F, 12.0F), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition coreRoot = body.addOrReplaceChild("core_root", CubeListBuilder.create().texOffs(116, 34).addBox(-2.0F, -3.0F, 0.5F, 4.0F, 5.0F, 1.0F), PartPose.offset(0.0F, -11.0F, -0.5F));

        coreRoot.addOrReplaceChild("core", CubeListBuilder.create().texOffs(92, 34).addBox(-3.0F, -6.0F, -2.5F, 6.0F, 6.0F, 6.0F)
                .texOffs(92, 46).addBox(-5.0F, -9.0F, -0.5F, 10.0F, 7.0F, 1.0F), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition petals = root.addOrReplaceChild("petals", CubeListBuilder.create(), PartPose.offset(0.0F, 20.0F, 0.0F));

        PartDefinition petalBack = petals.addOrReplaceChild("petal_back", CubeListBuilder.create().texOffs(0, 39).addBox(-9.0F, -3.634F, 3.8301F, 18.0F, 3.0F, 28.0F), PartPose.offsetAndRotation(0.0F, -1.5F, 0.0F, -0.5236F, 0.0F, 0.0F));

        PartDefinition petalBackLeft = petals.addOrReplaceChild("petal_back_left", CubeListBuilder.create().texOffs(0, 104).addBox(-4.0F, -2.0F, -22.0F, 8.0F, 3.0F, 23.0F), PartPose.offsetAndRotation(6.0F, 3.0F, 6.0F, 0.7854F, -2.3562F, 0.0F));

        PartDefinition petalBackRight = petals.addOrReplaceChild("petal_back_right", CubeListBuilder.create().texOffs(0, 104).mirror().addBox(-4.0F, -2.0F, -22.0F, 8.0F, 3.0F, 23.0F).mirror(false), PartPose.offsetAndRotation(-6.0F, 3.0F, 6.0F, 0.7854F, 2.3562F, 0.0F));

        PartDefinition petalLeft = petals.addOrReplaceChild("petal_left", CubeListBuilder.create().texOffs(0, 39).addBox(-9.0F, -3.634F, 3.8301F, 18.0F, 3.0F, 28.0F), PartPose.offsetAndRotation(0.0F, -1.5F, 0.0F, -0.5236F, 1.5708F, 0.0F));

        PartDefinition petalRight = petals.addOrReplaceChild("petal_right", CubeListBuilder.create().texOffs(0, 39).mirror().addBox(-9.0F, -3.634F, 3.8301F, 18.0F, 3.0F, 28.0F).mirror(false), PartPose.offsetAndRotation(0.0F, -1.5F, 0.0F, -0.5236F, -1.5708F, 0.0F));

        PartDefinition petalFrontLeft = petals.addOrReplaceChild("petal_front_left", CubeListBuilder.create().texOffs(0, 70).addBox(-6.0F, -2.0F, -30.0F, 12.0F, 3.0F, 31.0F), PartPose.offsetAndRotation(6.0F, 3.0F, -6.0F, 0.7854F, -0.7854F, 0.0F));

        PartDefinition petalFrontRight = petals.addOrReplaceChild("petal_front_right", CubeListBuilder.create().texOffs(0, 70).mirror().addBox(-6.0F, -2.0F, -30.0F, 12.0F, 3.0F, 31.0F).mirror(false), PartPose.offsetAndRotation(-6.0F, 3.0F, -6.0F, 0.7854F, 0.7854F, 0.0F));

        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(TractorbloomEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float headYaw = (float) Math.toRadians(netHeadYaw);
        this.coreRoot.yRot = headYaw * 0.25F;
        this.core.yRot = headYaw * 0.75F;
        this.core.xRot = (float) Math.toRadians(headPitch);
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        double petalSpinAngle = Math.toRadians(Mth.lerp(partialTick, entity.prevPetalSpinAngle, entity.petalSpinAngle));
        this.petalBack.yRot = (float) (petalSpinAngle);
        this.petalLeft.yRot = (float) (petalSpinAngle + Math.TAU / 3.0);
        this.petalRight.yRot = (float) (petalSpinAngle - Math.TAU / 3.0);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.body.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.petals.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
