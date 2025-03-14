package phanastrae.operation_starcleave.client.render.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import phanastrae.operation_starcleave.entity.mob.StarcleaverGolemEntity;

public class StarcleaverGolemEntityModel<T extends StarcleaverGolemEntity> extends EntityModel<T> {

    private final ModelPart body;
    private final ModelPart drill;
    private final ModelPart legs;
    private final ModelPart drillPivot;
    private final ModelPart drillHead;
    private final ModelPart drillTip;
    private final ModelPart door;

    public StarcleaverGolemEntityModel(ModelPart root) {
        this.body = root.getChild("body");
        this.drill = root.getChild("drill");
        this.legs = root.getChild("legs");

        this.drillPivot = drill.getChild("drillpivot");
        this.drillHead = drillPivot.getChild("drilltop");
        this.drillTip = drillHead.getChild("tip");

        this.door = body.getChild("door");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();

        PartDefinition body = modelPartData.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -3.25F, -2.0F, 5.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 20.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition microthrusters = body.addOrReplaceChild("microthrusters", CubeListBuilder.create().texOffs(14, 0).addBox(-3.0F, -0.5F, -0.5F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.25F, 0.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition rocketpack = body.addOrReplaceChild("rocketpack", CubeListBuilder.create().texOffs(18, 2).addBox(-2.0F, -2.5F, -1.0F, 4.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(14, 9).addBox(-1.5F, -0.75F, -1.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(10, 9).addBox(0.5F, -0.75F, -1.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(22, 9).addBox(-2.5F, -0.25F, -0.25F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(18, 9).addBox(1.5F, -0.25F, -0.25F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -3.0F));

        PartDefinition door = body.addOrReplaceChild("door", CubeListBuilder.create().texOffs(0, 9).addBox(-2.0F, -4.0F, -0.75F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.25F, 2.0F));

        PartDefinition drill = modelPartData.addOrReplaceChild("drill", CubeListBuilder.create().texOffs(0, 14).addBox(-1.5F, -3.0F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 16.75F, 0.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition drillpivot = drill.addOrReplaceChild("drillpivot", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.25F, 0.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition drilltop = drillpivot.addOrReplaceChild("drilltop", CubeListBuilder.create().texOffs(0, 24).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition tip = drilltop.addOrReplaceChild("tip", CubeListBuilder.create().texOffs(4, 18).addBox(0.0F, -4.0F, -1.0F, 0.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 20).addBox(-1.0F, -4.0F, 0.0F, 2.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, 0.0F));

        PartDefinition head = tip.addOrReplaceChild("head", CubeListBuilder.create().texOffs(8, 20).addBox(-0.5F, -1.5F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.5F, 0.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition legs = modelPartData.addOrReplaceChild("legs", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 21.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition frontrightleg = legs.addOrReplaceChild("frontrightleg", CubeListBuilder.create().texOffs(12, 14).mirror().addBox(-0.25F, -0.25F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.5F, 0.5F, 1.0F, 0.5236F, -0.9599F, -0.829F));

        PartDefinition backrightleg = legs.addOrReplaceChild("backrightleg", CubeListBuilder.create().texOffs(12, 18).mirror().addBox(-0.25F, -0.25F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.5F, 0.5F, -1.0F, -0.5236F, 0.9599F, -0.829F));

        PartDefinition frontleftleg = legs.addOrReplaceChild("frontleftleg", CubeListBuilder.create().texOffs(16, 14).addBox(-0.75F, -0.25F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, 0.5F, 1.0F, 0.5236F, 0.9599F, 0.829F));

        PartDefinition backleftleg = legs.addOrReplaceChild("backleftleg", CubeListBuilder.create().texOffs(16, 18).addBox(-0.75F, -0.25F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, 0.5F, -1.0F, -0.5236F, -0.9599F, 0.829F));

        return LayerDefinition.create(modelData, 32, 32);
    }

    @Override
    public void setupAnim(StarcleaverGolemEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        float s = (float)entity.getDeltaMovement().lengthSqr();
        s /= 0.2F;
        s *= s * s;

        if (s < 1.0F) {
            s = 1.0F;
        }

        legs.xRot = Mth.cos(limbAngle * 0.6662F) * limbDistance * 0.25F / s;
        legs.zRot = Mth.cos(limbAngle * 1.3324F) * limbDistance * 0.125F / s;

        body.xRot = -headPitch * (float) (Math.PI / 180.0) * 0.2f + Mth.cos(limbAngle * 0.6662F) * 0.1F / s;
    }

    @Override
    public void prepareMobModel(StarcleaverGolemEntity entity, float limbAngle, float limbDistance, float tickDelta) {
        drillPivot.xRot = Mth.rotLerp(tickDelta, entity.prevDrillBasePitch, entity.drillBasePitch) * (float) (Mth.PI / 180.0);
        drillHead.yRot = -Mth.rotLerp(tickDelta, entity.prevDrillHeadAngle, entity.drillHeadAngle) * (float) (Mth.PI / 180.0);
        drillTip.yRot = -Mth.rotLerp(tickDelta, entity.prevDrillTipAngle, entity.drillTipAngle) * (float) (Mth.PI / 180.0);

        door.xRot = -Mth.sin(Mth.lerp(tickDelta, entity.prevDoorProgress, entity.doorProgress)) * Mth.PI / 2;
    }

    @Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        body.render(matrices, vertices, light, overlay, color);
        drill.render(matrices, vertices, light, overlay, color);
        legs.render(matrices, vertices, light, overlay, color);
    }
}
