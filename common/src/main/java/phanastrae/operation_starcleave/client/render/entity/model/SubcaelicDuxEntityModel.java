package phanastrae.operation_starcleave.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.entity.mob.SubcaelicDuxEntity;

import java.util.Arrays;
import java.util.List;

public class SubcaelicDuxEntityModel<T extends SubcaelicDuxEntity> extends HierarchicalModel<T> {
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
        this.body = root.getChild(PartNames.BODY);
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

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition root = modelData.getRoot();

        PartDefinition body = root.addOrReplaceChild(
                PartNames.BODY,
                CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -12.0F, -7.0F, 14.0F, 25.0F, 14.0F)
                .texOffs(56, 0).addBox(-8.0F, -16.0F, 2.0F, 16.0F, 20.0F, 6.0F)
                .texOffs(100, 0).addBox(-4.0F, -13.5F, -4.0F, 8.0F, 3.0F, 6.0F)
                .texOffs(100, 9).addBox(5.5F, -14.0F, -6.0F, 2.0F, 14.0F, 8.0F)
                .texOffs(100, 9).mirror().addBox(-7.5F, -14.0F, -6.0F, 2.0F, 14.0F, 8.0F).mirror(false),
                PartPose.offset(0.0F, 11.0F, 0.0F));

        PartDefinition halo = body.addOrReplaceChild(
                HALO,
                CubeListBuilder.create().texOffs(0, 39).addBox(-10.0F, -10.0F, -0.5F, 20.0F, 20.0F, 1.0F),
                PartPose.offset(0.0F, -11.0F, 13.5F));

        PartDefinition leftDoor = body.addOrReplaceChild(
                "leftDoor",
                CubeListBuilder.create().texOffs(42, 39).addBox(-5.5F, -10.5F, -0.75F, 6.0F, 21.0F, 1.0F),
                PartPose.offset(5.5F, 0.5F, -7.0F));
        PartDefinition rightDoor = body.addOrReplaceChild(
                "rightDoor",
                CubeListBuilder.create().texOffs(42, 39).mirror().addBox(-0.5F, -10.5F, -0.75F, 6.0F, 21.0F, 1.0F).mirror(false),
                PartPose.offset(-5.5F, 0.5F, -7.0F));

        PartDefinition leftWing0 = body.addOrReplaceChild(
                getLeftWingName(0),
                CubeListBuilder.create().texOffs(72, 34).addBox(-1.5F, -11.5F, -0.5F, 9.0F, 23.0F, 1.0F),
                PartPose.offset(7.5F, 2.5F, 2.0F));
        PartDefinition leftWing1 = leftWing0.addOrReplaceChild(
                getLeftWingName(1),
                CubeListBuilder.create().texOffs(92, 33).addBox(0.0F, -13.75F, -0.5F, 8.0F, 24.0F, 1.0F),
                PartPose.offset(7.5F, 5.25F, 0.0F));
        PartDefinition leftWing2 = leftWing1.addOrReplaceChild(
                getLeftWingName(2),
                CubeListBuilder.create().texOffs(110, 32).addBox(0.0F, -12.5F, -0.5F, 8.0F, 25.0F, 1.0F),
                PartPose.offset(8.0F, 1.75F, 0.0F));

        PartDefinition rightWing0 = body.addOrReplaceChild(
                getRightWingName(0),
                CubeListBuilder.create().texOffs(72, 34).mirror().addBox(-7.5F, -11.5F, -0.5F, 9.0F, 23.0F, 1.0F).mirror(false),
                PartPose.offset(-7.5F, 2.5F, 2.0F));
        PartDefinition rightWing1 = rightWing0.addOrReplaceChild(
                getRightWingName(1),
                CubeListBuilder.create().texOffs(92, 33).mirror().addBox(-8.0F, -13.75F, -0.5F, 8.0F, 24.0F, 1.0F).mirror(false),
                PartPose.offset(-7.5F, 5.25F, 0.0F));
        PartDefinition rightWing2 = rightWing1.addOrReplaceChild(
                getRightWingName(2),
                CubeListBuilder.create().texOffs(110, 32).mirror().addBox(-8.0F, -12.5F, -0.5F, 8.0F, 25.0F, 1.0F).mirror(false),
                PartPose.offset(-8.0F, 1.75F, 0.0F));

        PartDefinition baseTentacles = body.addOrReplaceChild(
                BASE_TENTACLES,
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 13.0F, 0.0F));

        PartDefinition outerTentacles = body.addOrReplaceChild(
                OUTER_TENTACLES,
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 13.0F, 0.0F));

        CubeListBuilder modelPartBuilder = CubeListBuilder.create().texOffs(56, 26).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 26.0F, 3.0F);
        for(int k = 0; k < 7; ++k) {
            double angle = Math.PI * 2.0 * k / 7.0;
            float x = (float)Math.sin(angle) * -6.0F;
            float z = (float)Math.cos(angle) * -6.0F;

            double angle2 = Math.PI * 2.0 * k / 7.0;
            baseTentacles.addOrReplaceChild(
                    getBaseTentacleName(k),
                    modelPartBuilder,
                    PartPose.offsetAndRotation(x, 0.0F, z, 0.0F, (float)angle2, 0.0F));
        }

        modelPartBuilder = CubeListBuilder.create().texOffs(120, 9).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 21.0F, 2.0F);
        for(int k = 0; k < 7; ++k) {
            double angle = Math.PI * 2.0 * k / 7.0;
            float x = (float)Math.sin(angle) * 9.5F;
            float z = (float)Math.cos(angle) * 9.5F;

            double angle2 = Math.PI * 2.0 * k / 7.0;
            outerTentacles.addOrReplaceChild(
                    getOuterTentacleName(k),
                    modelPartBuilder,
                    PartPose.offsetAndRotation(x, 0.0F, z, 0.0F, (float)angle2, 0.0F));
        }

        return LayerDefinition.create(modelData, 128, 64);
    }

    @Override
    public void setupAnim(SubcaelicDuxEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        for(ModelPart tentacle : this.baseTentacles) {
            tentacle.xRot = -0.15f * animationProgress;
        }
        for(ModelPart tentacle : this.outerTentacles) {
            tentacle.xRot = 0.3f * animationProgress;
        }
    }

    @Override
    public void prepareMobModel(SubcaelicDuxEntity entity, float limbAngle, float limbDistance, float tickDelta) {
        float f = (float)Math.toRadians(Mth.rotLerp(tickDelta, entity.prevTentacleRollAngle, entity.tentacleRollAngle));
        this.baseTentacleRoot.yRot = -f;
        this.outerTentacleRoot.yRot = 2f * f;

        float g = 0.4f * (float)Math.sin(f);
        for(int i = 0; i < 3; i++) {
            this.leftWing[i].yRot = g;
            this.rightWing[i].yRot = -g;
        }

        this.halo.zRot = (float)Math.toRadians(Mth.rotLerp(tickDelta, entity.prevHaloAngle, entity.haloAngle));
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    public List<ModelPart> getGlowingParts() {
        return this.glowingParts;
    }
}
