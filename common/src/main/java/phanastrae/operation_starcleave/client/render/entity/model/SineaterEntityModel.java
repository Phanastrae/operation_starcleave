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
import phanastrae.operation_starcleave.entity.mob.SineaterEntity;

public class SineaterEntityModel<T extends SineaterEntity> extends EntityModel<T> {
    private final ModelPart floorRoot;
    private final ModelPart body;
    private final ModelPart coat;
    private final ModelPart head;
    private final ModelPart tail;
    private final ModelPart legFrontLeft;
    private final ModelPart legFrontRight;
    private final ModelPart legBackLeft;
    private final ModelPart legBackRight;
    private final ModelPart wingLeft;
    private final ModelPart wingRight;

    public SineaterEntityModel(ModelPart root) {
        this.floorRoot = root.getChild("floorRoot");
        this.body = this.floorRoot.getChild("body");
        this.coat = this.floorRoot.getChild("coat");
        this.head = this.floorRoot.getChild("head");
        this.tail = this.floorRoot.getChild("tail");
        this.legFrontLeft = this.floorRoot.getChild("legFrontLeft");
        this.legFrontRight = this.floorRoot.getChild("legFrontRight");
        this.legBackLeft = this.floorRoot.getChild("legBackLeft");
        this.legBackRight = this.floorRoot.getChild("legBackRight");
        this.wingLeft = this.floorRoot.getChild("wingLeft");
        this.wingRight = this.floorRoot.getChild("wingRight");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition floorRoot = root.addOrReplaceChild("floorRoot", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body = floorRoot.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 63).addBox(-12.0F, -25.0F, -19.0F, 24.0F, 25.0F, 38.0F), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition coat = floorRoot.addOrReplaceChild("coat", CubeListBuilder.create().texOffs(0, 0).addBox(-13.0F, -26.0F, -20.0F, 26.0F, 23.0F, 40.0F), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition head = floorRoot.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 126).addBox(-7.0F, -7.0F, -6.0F, 14.0F, 13.0F, 6.0F), PartPose.offset(0.0F, -9.0F, -19.0F));

        PartDefinition tail = floorRoot.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(40, 126).addBox(-8.0F, -4.0F, -4.0F, 16.0F, 8.0F, 14.0F), PartPose.offsetAndRotation(0.0F, -6.0F, 22.0F, -0.1745F, 0.0F, 0.0F));

        PartDefinition legFrontLeft = floorRoot.addOrReplaceChild("legFrontLeft", CubeListBuilder.create(), PartPose.offsetAndRotation(12.0F, -3.0F, -13.0F, 0.0F, 0.3054F, 0.0F));
        legFrontLeft.addOrReplaceChild("leg_front_left_r1", CubeListBuilder.create().texOffs(0, 145).addBox(-1.0F, -3.0F, -2.0F, 2.0F, 12.0F, 4.0F), PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 1.0472F, 0.4363F, -0.7854F));

        PartDefinition legFrontRight = floorRoot.addOrReplaceChild("legFrontRight", CubeListBuilder.create(), PartPose.offset(-12.0F, -3.0F, -13.0F));
        legFrontRight.addOrReplaceChild("leg_front_right_r1", CubeListBuilder.create().texOffs(0, 145).mirror().addBox(-1.0F, -3.0F, -2.0F, 2.0F, 12.0F, 4.0F).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 1.0472F, -0.4363F, 0.7854F));

        PartDefinition legBackLeft = floorRoot.addOrReplaceChild("legBackLeft", CubeListBuilder.create(), PartPose.offsetAndRotation(12.0F, -3.0F, 10.0F, 0.0F, -0.0873F, 0.0F));
        legBackLeft.addOrReplaceChild("leg_back_left_r1", CubeListBuilder.create().texOffs(12, 145).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 9.0F, 3.0F), PartPose.offsetAndRotation(0.0F, 1.0F, 1.0F, 1.0472F, 0.4363F, -0.7854F));

        PartDefinition legBackRight = floorRoot.addOrReplaceChild("legBackRight", CubeListBuilder.create(), PartPose.offset(-12.0F, -3.0F, 10.0F));
        legBackRight.addOrReplaceChild("leg_back_right_r1", CubeListBuilder.create().texOffs(12, 145).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 9.0F, 3.0F).mirror(false), PartPose.offsetAndRotation(0.0F, 1.0F, 1.0F, 1.0472F, -0.4363F, 0.7854F));

        PartDefinition wingLeft = floorRoot.addOrReplaceChild("wingLeft", CubeListBuilder.create(), PartPose.offset(12.0F, -24.0F, 3.0F));
        wingLeft.addOrReplaceChild("wing_left_r1", CubeListBuilder.create().texOffs(22, 148).addBox(-1.0F, -1.0F, -1.0F, 1.0F, 10.0F, 18.0F), PartPose.offsetAndRotation(1.0F, 1.0F, -1.0F, 0.0F, 0.5236F, -0.829F));

        PartDefinition wingRight = floorRoot.addOrReplaceChild("wingRight", CubeListBuilder.create(), PartPose.offset(-12.0F, -24.0F, 3.0F));
        wingRight.addOrReplaceChild("wing_right_r1", CubeListBuilder.create().texOffs(22, 148).mirror().addBox(0.0F, -1.0F, -1.0F, 1.0F, 10.0F, 18.0F).mirror(false), PartPose.offsetAndRotation(-1.0F, 1.0F, -1.0F, 0.0F, -0.5236F, 0.829F));

        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(SineaterEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float frontLegYRot = (float) Math.sin(ageInTicks / 5.5 + limbSwing * 0.35) * 0.1F * (1.0F + 0.5F * limbSwingAmount);
        this.legFrontLeft.yRot = frontLegYRot;
        this.legFrontRight.yRot = -frontLegYRot;

        float backLegYRot = (float) Math.sin(ageInTicks / 5.5 + limbSwing * 0.35 - 2.0) * 0.15F * (1.0F + 0.5F * limbSwingAmount);
        this.legBackLeft.yRot = backLegYRot;
        this.legBackRight.yRot = -backLegYRot;

        float bodyScaleFactor = (float) Math.sin(ageInTicks / 8.5 + limbSwing * 0.4) * 0.05F;
        this.body.xScale = 1.0F + bodyScaleFactor;
        this.body.yScale = 1.0F - bodyScaleFactor;
        this.coat.xScale = 1.0F + bodyScaleFactor;
        this.coat.yScale = 1.0F - bodyScaleFactor;

        float wingXRot = (float) Math.sin(ageInTicks / 2.5 + limbSwing * 0.8) * 0.2F;
        this.wingLeft.xRot = wingXRot;
        this.wingRight.xRot = wingXRot;

        float wingX = -12.0F - bodyScaleFactor * 12.0F;
        this.wingLeft.x = -wingX;
        this.wingRight.x = wingX;

        float wingY = -24.0F + bodyScaleFactor * 24.0F;
        this.wingLeft.y = wingY;
        this.wingRight.y = wingY;

        float headScaleFactor = (float) Math.sin(ageInTicks / 5.0 + limbSwing * 0.1) * 0.09F;
        this.head.xScale = 1.0F + headScaleFactor;
        this.head.yScale = 1.0F + headScaleFactor;
        this.head.zScale = 1.0F - headScaleFactor;

        float tailScaleFactor = (float) Math.sin(ageInTicks / 6.0 + limbSwing * 0.1) * 0.07F;
        this.tail.xScale = 1.0F + tailScaleFactor;
        this.tail.yScale = 1.0F + tailScaleFactor;
        this.tail.zScale = 1.0F - tailScaleFactor;
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        this.setBodyVisbile(true);
        this.setOuterVisible(false);

        float fullBodyScaleFactor = Mth.lerp(partialTick, entity.prevSquishiness, entity.squishiness);
        this.floorRoot.xScale = 1.0F + fullBodyScaleFactor * 0.5F;
        this.floorRoot.yScale = 1.0F - fullBodyScaleFactor * 0.5F;
        this.floorRoot.zScale = 1.0F + fullBodyScaleFactor * 0.2F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        this.floorRoot.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    public void setBodyVisbile(boolean visible) {
        this.body.visible = visible;
        this.head.visible = visible;
        this.tail.visible = visible;
        this.legFrontLeft.visible = visible;
        this.legFrontRight.visible = visible;
        this.legBackLeft.visible = visible;
        this.legBackRight.visible = visible;
    }

    public void setOuterVisible(boolean visible) {
        this.coat.visible = visible;
        this.wingLeft.visible = visible;
        this.wingRight.visible = visible;
    }
}
