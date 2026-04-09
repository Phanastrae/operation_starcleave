package phanastrae.operation_starcleave.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import phanastrae.operation_starcleave.entity.mob.HammertailGolemEntity;

import java.util.List;

public class HammertailGolemEntityModel<T extends HammertailGolemEntity> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart pole;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart main;

    private final List<ModelPart> glowingParts;
    private final List<ModelPart> iridescentParts;

    public HammertailGolemEntityModel(ModelPart root) {
        this.root = root;
        this.pole = root.getChild("pole");
        this.neck = this.pole.getChild("neck");
        this.head = this.neck.getChild("head");
        this.main = root.getChild("main");

        this.glowingParts = ImmutableList.<ModelPart>builder().add(this.main).addAll(this.head.getAllParts().iterator()).build();

        this.iridescentParts = this.root.getAllParts().toList();
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition pole = root.addOrReplaceChild("pole", CubeListBuilder.create(), PartPose.offset(0.0F, 16.0F, 2.0F));

        PartDefinition pole_r1 = pole.addOrReplaceChild("pole_r1", CubeListBuilder.create().texOffs(18, 0).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 1.5708F));

        PartDefinition neck = pole.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -5.0F, -3.0F, 6.0F, 6.0F, 6.0F), PartPose.offset(0.0F, -15.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(32, 4).addBox(-4.0F, -6.0F, -7.0F, 8.0F, 8.0F, 8.0F), PartPose.offset(0.0F, -3.0F, -2.0F));

        PartDefinition left_bottom_barrel = head.addOrReplaceChild("left_bottom_barrel", CubeListBuilder.create().texOffs(18, 0).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F), PartPose.offsetAndRotation(3.0F, 2.0F, -2.0F, 0.0F, -1.7453F, 0.1745F));
        PartDefinition right_bottom_barrel = head.addOrReplaceChild("right_bottom_barrel", CubeListBuilder.create().texOffs(18, 0).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F), PartPose.offsetAndRotation(-3.0F, 2.0F, -2.0F, 0.0F, -1.3963F, -0.1745F));
        PartDefinition left_middle_barrel = head.addOrReplaceChild("left_middle_barrel", CubeListBuilder.create().texOffs(18, 0).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F), PartPose.offsetAndRotation(5.0F, -1.0F, 2.0F, 0.0F, -1.6581F, 0.0873F));
        PartDefinition right_middle_barrel = head.addOrReplaceChild("right_middle_barrel", CubeListBuilder.create().texOffs(18, 0).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F), PartPose.offsetAndRotation(-5.0F, -1.0F, 2.0F, 0.0F, -1.4835F, -0.0873F));
        PartDefinition left_top_barrel = head.addOrReplaceChild("left_top_barrel", CubeListBuilder.create().texOffs(18, 0).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F), PartPose.offsetAndRotation(5.0F, -4.0F, 5.0F, -1.5708F, -1.4835F, 1.789F));
        PartDefinition right_top_barrel = head.addOrReplaceChild("right_top_barrel", CubeListBuilder.create().texOffs(18, 0).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F), PartPose.offsetAndRotation(-5.0F, -4.0F, 5.0F, -1.5708F, -1.4835F, 1.3526F));
        PartDefinition crown_barrel = head.addOrReplaceChild("crown_barrel", CubeListBuilder.create().texOffs(18, 0).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F), PartPose.offsetAndRotation(0.0F, -6.0F, 1.0F, -1.5708F, -1.3963F, 1.5708F));

        PartDefinition main = root.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 12).addBox(-5.0F, -8.0F, -6.0F, 10.0F, 8.0F, 12.0F), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float headYawRads = netHeadYaw * (float) (Math.PI / 180.0);
        float headPitchRads = headPitch * (float) (Math.PI / 180.0);

        this.pole.yRot = headYawRads * 0.1F;
        this.neck.yRot = headYawRads * 0.2F;
        this.head.yRot = headYawRads * 0.7F;

        this.pole.xRot = headPitchRads * 0.05F;
        this.neck.xRot = headPitchRads * 0.1F;
        this.head.xRot = headPitchRads * 0.85F;
    }

    public List<ModelPart> getGlowingParts() {
        return this.glowingParts;
    }

    public List<ModelPart> getIridescentParts() {
        return this.iridescentParts;
    }
}
