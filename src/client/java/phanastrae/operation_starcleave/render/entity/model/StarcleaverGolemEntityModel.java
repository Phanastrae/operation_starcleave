package phanastrae.operation_starcleave.render.entity.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import phanastrae.operation_starcleave.entity.mob.StarcleaverGolemEntity;

public class StarcleaverGolemEntityModel extends EntityModel<StarcleaverGolemEntity> {

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

    public static TexturedModelData getTexturedModelData() {
        ModelData meshdefinition = new ModelData();
        ModelPartData partdefinition = meshdefinition.getRoot();

        ModelPartData body = partdefinition.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-2.5F, -3.25F, -2.0F, 5.0F, 5.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 20.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData microthrusters = body.addChild("microthrusters", ModelPartBuilder.create().uv(14, 0).cuboid(-3.0F, -0.5F, -0.5F, 6.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.25F, 0.0F, 0.7854F, 0.0F, 0.0F));

        ModelPartData rocketpack = body.addChild("rocketpack", ModelPartBuilder.create().uv(18, 2).cuboid(-2.0F, -2.5F, -1.0F, 4.0F, 5.0F, 2.0F, new Dilation(0.0F))
                .uv(14, 9).cuboid(-1.5F, -0.75F, -1.5F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(10, 9).cuboid(0.5F, -0.75F, -1.5F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(22, 9).cuboid(-2.5F, -0.25F, -0.25F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(18, 9).cuboid(1.5F, -0.25F, -0.25F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -1.0F, -3.0F));

        ModelPartData door = body.addChild("door", ModelPartBuilder.create().uv(0, 9).cuboid(-2.0F, -4.0F, -0.75F, 4.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 1.25F, 2.0F));

        ModelPartData drill = partdefinition.addChild("drill", ModelPartBuilder.create().uv(0, 14).cuboid(-1.5F, -3.0F, -1.5F, 3.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 16.75F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData drillpivot = drill.addChild("drillpivot", ModelPartBuilder.create(), ModelTransform.of(0.0F, -2.25F, 0.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData drilltop = drillpivot.addChild("drilltop", ModelPartBuilder.create().uv(0, 24).cuboid(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData tip = drilltop.addChild("tip", ModelPartBuilder.create().uv(4, 18).cuboid(0.0F, -4.0F, -1.0F, 0.0F, 4.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 20).cuboid(-1.0F, -4.0F, 0.0F, 2.0F, 4.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -3.0F, 0.0F));

        ModelPartData head = tip.addChild("head", ModelPartBuilder.create().uv(8, 20).cuboid(-0.5F, -1.5F, -0.5F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.5F, 0.0F, 0.0F, 0.7854F, 0.0F));

        ModelPartData legs = partdefinition.addChild("legs", ModelPartBuilder.create(), ModelTransform.of(0.0F, 21.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData frontrightleg = legs.addChild("frontrightleg", ModelPartBuilder.create().uv(12, 14).mirrored().cuboid(-0.25F, -0.25F, -0.5F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(2.5F, 0.5F, 1.0F, 0.5236F, -0.9599F, -0.829F));

        ModelPartData backrightleg = legs.addChild("backrightleg", ModelPartBuilder.create().uv(12, 18).mirrored().cuboid(-0.25F, -0.25F, -0.5F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(2.5F, 0.5F, -1.0F, -0.5236F, 0.9599F, -0.829F));

        ModelPartData frontleftleg = legs.addChild("frontleftleg", ModelPartBuilder.create().uv(16, 14).cuboid(-0.75F, -0.25F, -0.5F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.5F, 0.5F, 1.0F, 0.5236F, 0.9599F, 0.829F));

        ModelPartData backleftleg = legs.addChild("backleftleg", ModelPartBuilder.create().uv(16, 18).cuboid(-0.75F, -0.25F, -0.5F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.5F, 0.5F, -1.0F, -0.5236F, -0.9599F, 0.829F));

        return TexturedModelData.of(meshdefinition, 32, 32);
    }

    @Override
    public void setAngles(StarcleaverGolemEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        float s = (float)entity.getVelocity().lengthSquared();
        s /= 0.2F;
        s *= s * s;

        if (s < 1.0F) {
            s = 1.0F;
        }

        legs.pitch = MathHelper.cos(limbAngle * 0.6662F) * limbDistance * 0.25F / s;
        legs.roll = MathHelper.cos(limbAngle * 1.3324F) * limbDistance * 0.125F / s;

        body.pitch = -headPitch * (float) (Math.PI / 180.0) * 0.2f + MathHelper.cos(limbAngle * 0.6662F) * 0.1F / s;

    }

    @Override
    public void animateModel(StarcleaverGolemEntity entity, float limbAngle, float limbDistance, float tickDelta) {
        drillPivot.pitch = MathHelper.lerpAngleDegrees(tickDelta, entity.prevDrillBasePitch, entity.drillBasePitch) * (float) (MathHelper.PI / 180.0);
        drillHead.yaw = -MathHelper.lerpAngleDegrees(tickDelta, entity.prevDrillHeadAngle, entity.drillHeadAngle) * (float) (MathHelper.PI / 180.0);
        drillTip.yaw = -MathHelper.lerpAngleDegrees(tickDelta, entity.prevDrillTipAngle, entity.drillTipAngle) * (float) (MathHelper.PI / 180.0);

        door.pitch = -MathHelper.sin(MathHelper.lerp(tickDelta, entity.prevDoorProgress, entity.doorProgress)) * MathHelper.PI / 2;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        body.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        drill.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        legs.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
