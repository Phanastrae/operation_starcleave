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
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityAttachment;

public class PegasusWingsModel<T extends AbstractHorse> extends EntityModel<T> {

    private final ModelPart body;
    private final ModelPart left_wing;
    private final ModelPart left_wing_end;
    private final ModelPart right_wing;
    private final ModelPart right_wing_end;

    public PegasusWingsModel(ModelPart root) {
        this.body = root.getChild("body");
        this.left_wing = this.body.getChild("left_wing");
        this.left_wing_end = this.left_wing.getChild("left_wing_end");
        this.right_wing = this.body.getChild("right_wing");
        this.right_wing_end = this.right_wing.getChild("right_wing_end");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition
                .addOrReplaceChild("body",
                        CubeListBuilder.create(),
                        PartPose.offset(0.0F, 11.0F, 5.0F));

        PartDefinition left_wing = body
                .addOrReplaceChild("left_wing",
                        CubeListBuilder.create()
                                .texOffs(0, 0)
                                .addBox(0.0F, 0.0F, -10.0F, 12.0F, 1.0F, 25.0F),
                        PartPose.offsetAndRotation(4.0F, -5.0F, -3.0F, 0.0F, 0.0F, -1.309F));

        PartDefinition left_wing_end = left_wing
                .addOrReplaceChild("left_wing_end",
                        CubeListBuilder.create()
                                .texOffs(0, 26)
                                .addBox(0.0F, -0.5F, -10.0F, 18.0F, 1.0F, 25.0F),
                        PartPose.offsetAndRotation(12.0F, 0.5F, 0.0F, 0.0F, 0.0F, 2.3562F));

        PartDefinition right_wing = body
                .addOrReplaceChild("right_wing",
                        CubeListBuilder.create()
                                .texOffs(0, 0).mirror()
                                .addBox(-12.0F, 0.0F, -10.0F, 12.0F, 1.0F, 25.0F).mirror(false),
                        PartPose.offsetAndRotation(-4.0F, -5.0F, -3.0F, 0.0F, 0.0F, 1.309F));

        PartDefinition right_wing_end = right_wing
                .addOrReplaceChild("right_wing_end",
                        CubeListBuilder.create()
                                .texOffs(0, 26).mirror()
                                .addBox(-18.0F, -0.5F, -10.0F, 18.0F, 1.0F, 25.0F).mirror(false),
                        PartPose.offsetAndRotation(-12.0F, 0.5F, 0.0F, 0.0F, 0.0F, -2.3562F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);

        float standAnim = entity.getStandAnim(partialTick);
        this.body.xRot = standAnim * (float) (-Math.PI / 4);

        OperationStarcleaveEntityAttachment osea = OperationStarcleaveEntityAttachment.fromEntity(entity);
        float wingSpread = Mth.lerp(partialTick, osea.getPrevPegasusWingSpread(), osea.getPegasusWingSpread());
        float wingFlap = Mth.lerp(partialTick, osea.getPrevPegasusWingFlap(), osea.getPegasusWingFlap());

        wingSpread = Math.max(wingSpread, wingFlap * 0.25F);

        float age = entity.tickCount + partialTick;
        float groundedRot = Mth.lerp(wingSpread, 0.08F, 0F) * Mth.sin(0.2F * limbSwing)
                + Mth.lerp(wingSpread, 0.02F, 0F) * Mth.sin(age / 40);
        float glidingMainRot = Mth.lerp(wingSpread, 0F, 0.14F) * Mth.sin(age / 20) + Mth.lerp(wingFlap, 0F, 0.2F) * Mth.sin(age / 2);
        float glidingEndRot = Mth.lerp(wingSpread, 0F, 0.14F) * Mth.sin(age / 20 + 0.2F) + Mth.lerp(wingFlap, 0F, 0.2F) * Mth.sin(age / 2 + 0.15F);

        float mainWingRot = Mth.lerp(wingSpread, -0.9F, -0.25F) + groundedRot + glidingMainRot;
        float endWingRot = Mth.lerp(wingSpread, 2.4F, 0.65F) - 2 * groundedRot + glidingEndRot;

        this.left_wing.zRot = mainWingRot;
        this.right_wing.zRot = -mainWingRot;

        this.left_wing_end.zRot = endWingRot;
        this.right_wing_end.zRot = -endWingRot;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.body.y = 11.0F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        this.body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
