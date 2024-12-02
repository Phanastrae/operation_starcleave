package phanastrae.operation_starcleave.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.entity.mob.SubcaelicDuxEntity;

import java.util.Arrays;
import java.util.List;

public class SubcaelicDuxEntityModel<T extends SubcaelicDuxEntity> extends SinglePartEntityModel<T> {
    public static final String BASE_TENTACLES = "base_tentacles";
    public static final String OUTER_TENTACLES = "outer_tentacles";
    public static final String HALO = "halo";

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart baseTentacleRoot;
    private final ModelPart[] baseTentacles = new ModelPart[7];
    private final ModelPart outerTentacleRoot;
    private final ModelPart[] outerTentacles = new ModelPart[7];
    private final ModelPart halo;
    private final ModelPart[] leftWing = new ModelPart[3];
    private final ModelPart[] rightWing = new ModelPart[3];
    private final List<ModelPart> glowingParts;

    public SubcaelicDuxEntityModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild(EntityModelPartNames.BODY);
        this.baseTentacleRoot = body.getChild(BASE_TENTACLES);
        Arrays.setAll(this.baseTentacles, index -> baseTentacleRoot.getChild(getBaseTentacleName(index)));
        this.outerTentacleRoot = body.getChild(OUTER_TENTACLES);
        Arrays.setAll(this.outerTentacles, index -> outerTentacleRoot.getChild(getOuterTentacleName(index)));
        this.halo = body.getChild(HALO);
        this.leftWing[0] = this.body.getChild(getLeftWingName(0));
        this.leftWing[1] = this.leftWing[0].getChild(getLeftWingName(1));
        this.leftWing[2] = this.leftWing[1].getChild(getLeftWingName(2));
        this.rightWing[0] = this.body.getChild(getRightWingName(0));
        this.rightWing[1] = this.rightWing[0].getChild(getRightWingName(1));
        this.rightWing[2] = this.rightWing[1].getChild(getRightWingName(2));

        this.glowingParts = ImmutableList.of(this.leftWing[0], this.leftWing[1], this.leftWing[2], this.rightWing[0], this.rightWing[1], this.rightWing[2], this.halo);
    }

    private static String getBaseTentacleName(int index) {
        return "baseTentacle" + index;
    }

    private static String getOuterTentacleName(int index) {
        return "outerTentacle" + index;
    }

    private static String getLeftWingName(int index) {
        return "leftWing" + index;
    }

    private static String getRightWingName(int index) {
        return "rightWing" + index;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        ModelPartData body = root.addChild(
                EntityModelPartNames.BODY,
                ModelPartBuilder.create().uv(0, 0).cuboid(-7.0F, -12.0F, -7.0F, 14.0F, 25.0F, 14.0F)
                .uv(56, 0).cuboid(-8.0F, -16.0F, 2.0F, 16.0F, 20.0F, 6.0F)
                .uv(100, 0).cuboid(-4.0F, -13.5F, -4.0F, 8.0F, 3.0F, 6.0F)
                .uv(100, 9).cuboid(5.5F, -14.0F, -6.0F, 2.0F, 14.0F, 8.0F)
                .uv(100, 9).mirrored().cuboid(-7.5F, -14.0F, -6.0F, 2.0F, 14.0F, 8.0F).mirrored(false),
                ModelTransform.pivot(0.0F, 11.0F, 0.0F));

        ModelPartData halo = body.addChild(
                HALO,
                ModelPartBuilder.create().uv(0, 39).cuboid(-10.0F, -10.0F, -0.5F, 20.0F, 20.0F, 1.0F),
                ModelTransform.pivot(0.0F, -11.0F, 13.5F));

        ModelPartData leftDoor = body.addChild(
                "leftDoor",
                ModelPartBuilder.create().uv(42, 39).cuboid(-5.5F, -10.5F, -0.75F, 6.0F, 21.0F, 1.0F),
                ModelTransform.pivot(5.5F, 0.5F, -7.0F));
        ModelPartData rightDoor = body.addChild(
                "rightDoor",
                ModelPartBuilder.create().uv(42, 39).mirrored().cuboid(-0.5F, -10.5F, -0.75F, 6.0F, 21.0F, 1.0F).mirrored(false),
                ModelTransform.pivot(-5.5F, 0.5F, -7.0F));

        ModelPartData leftWing0 = body.addChild(
                getLeftWingName(0),
                ModelPartBuilder.create().uv(72, 34).cuboid(-1.5F, -11.5F, -0.5F, 9.0F, 23.0F, 1.0F),
                ModelTransform.pivot(7.5F, 2.5F, 2.0F));
        ModelPartData leftWing1 = leftWing0.addChild(
                getLeftWingName(1),
                ModelPartBuilder.create().uv(92, 33).cuboid(0.0F, -13.75F, -0.5F, 8.0F, 24.0F, 1.0F),
                ModelTransform.pivot(7.5F, 5.25F, 0.0F));
        ModelPartData leftWing2 = leftWing1.addChild(
                getLeftWingName(2),
                ModelPartBuilder.create().uv(110, 32).cuboid(0.0F, -12.5F, -0.5F, 8.0F, 25.0F, 1.0F),
                ModelTransform.pivot(8.0F, 1.75F, 0.0F));

        ModelPartData rightWing0 = body.addChild(
                getRightWingName(0),
                ModelPartBuilder.create().uv(72, 34).mirrored().cuboid(-7.5F, -11.5F, -0.5F, 9.0F, 23.0F, 1.0F).mirrored(false),
                ModelTransform.pivot(-7.5F, 2.5F, 2.0F));
        ModelPartData rightWing1 = rightWing0.addChild(
                getRightWingName(1),
                ModelPartBuilder.create().uv(92, 33).mirrored().cuboid(-8.0F, -13.75F, -0.5F, 8.0F, 24.0F, 1.0F).mirrored(false),
                ModelTransform.pivot(-7.5F, 5.25F, 0.0F));
        ModelPartData rightWing2 = rightWing1.addChild(
                getRightWingName(2),
                ModelPartBuilder.create().uv(110, 32).mirrored().cuboid(-8.0F, -12.5F, -0.5F, 8.0F, 25.0F, 1.0F).mirrored(false),
                ModelTransform.pivot(-8.0F, 1.75F, 0.0F));

        ModelPartData baseTentacles = body.addChild(
                BASE_TENTACLES,
                ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 13.0F, 0.0F));

        ModelPartData outerTentacles = body.addChild(
                OUTER_TENTACLES,
                ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 13.0F, 0.0F));

        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(56, 26).cuboid(-1.5F, 0.0F, -1.5F, 3.0F, 26.0F, 3.0F);
        for(int k = 0; k < 7; ++k) {
            double angle = Math.PI * 2.0 * k / 7.0;
            float x = (float)Math.sin(angle) * -6.0F;
            float z = (float)Math.cos(angle) * -6.0F;

            double angle2 = Math.PI * 2.0 * k / 7.0;
            baseTentacles.addChild(
                    getBaseTentacleName(k),
                    modelPartBuilder,
                    ModelTransform.of(x, 0.0F, z, 0.0F, (float)angle2, 0.0F));
        }

        modelPartBuilder = ModelPartBuilder.create().uv(120, 9).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 21.0F, 2.0F);
        for(int k = 0; k < 7; ++k) {
            double angle = Math.PI * 2.0 * k / 7.0;
            float x = (float)Math.sin(angle) * 9.5F;
            float z = (float)Math.cos(angle) * 9.5F;

            double angle2 = Math.PI * 2.0 * k / 7.0;
            outerTentacles.addChild(
                    getOuterTentacleName(k),
                    modelPartBuilder,
                    ModelTransform.of(x, 0.0F, z, 0.0F, (float)angle2, 0.0F));
        }

        return TexturedModelData.of(modelData, 128, 64);
    }

    @Override
    public void setAngles(SubcaelicDuxEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        for(ModelPart tentacle : this.baseTentacles) {
            tentacle.pitch = -0.15f * animationProgress;
        }
        for(ModelPart tentacle : this.outerTentacles) {
            tentacle.pitch = 0.3f * animationProgress;
        }
    }

    @Override
    public void animateModel(SubcaelicDuxEntity entity, float limbAngle, float limbDistance, float tickDelta) {
        float f = (float)Math.toRadians(MathHelper.lerpAngleDegrees(tickDelta, entity.prevTentacleRollAngle, entity.tentacleRollAngle));
        this.baseTentacleRoot.yaw = -f;
        this.outerTentacleRoot.yaw = 2f * f;

        float g = 0.4f * (float)Math.sin(f);
        for(int i = 0; i < 3; i++) {
            this.leftWing[i].yaw = g;
            this.rightWing[i].yaw = -g;
        }

        this.halo.roll = (float)Math.toRadians(MathHelper.lerpAngleDegrees(tickDelta, entity.prevHaloAngle, entity.haloAngle));
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }

    public List<ModelPart> getGlowingParts() {
        return this.glowingParts;
    }
}
