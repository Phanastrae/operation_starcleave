package phanastrae.operation_starcleave.mixin.client.renderer;

import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.item.BismuthBlasterItem;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin<T extends LivingEntity> extends AgeableListModel<T> implements ArmedModel, HeadedModel {
    @Shadow
    public HumanoidModel.ArmPose rightArmPose;
    @Shadow
    public HumanoidModel.ArmPose leftArmPose;
    @Shadow
    @Final
    public ModelPart rightArm;
    @Shadow
    @Final
    public ModelPart leftArm;

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/HumanoidModel;setupAttackAnimation(Lnet/minecraft/world/entity/LivingEntity;F)V"))
    private void operation_starcleave$poseArms(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (this.rightArmPose == HumanoidModel.ArmPose.ITEM) {
            ItemStack stack = entity.getMainArm() == HumanoidArm.RIGHT ? entity.getMainHandItem() : entity.getOffhandItem();

            if (stack.is(OperationStarcleaveItems.BISMUTH_BLASTER) && BismuthBlasterItem.isCharged(stack)) {
                this.rightArm.xRot = (float) (this.rightArm.xRot * 0.25 - Math.PI * 0.4 + 0.6 * Math.toRadians(headPitch));
                double relYaw = Math.toRadians(netHeadYaw);
                this.rightArm.yRot = (float) (relYaw > 0 ? relYaw * 0.9 : relYaw * 0.5);
            }
        }
        if (this.leftArmPose == HumanoidModel.ArmPose.ITEM) {
            ItemStack stack = entity.getMainArm() == HumanoidArm.LEFT ? entity.getMainHandItem() : entity.getOffhandItem();

            if (stack.is(OperationStarcleaveItems.BISMUTH_BLASTER) && BismuthBlasterItem.isCharged(stack)) {
                this.leftArm.xRot = (float) (this.leftArm.xRot * 0.25 - Math.PI * 0.4 + 0.6 * Math.toRadians(headPitch));
                double relYaw = Math.toRadians(netHeadYaw);
                this.leftArm.yRot = (float) (relYaw < 0 ? relYaw * 0.9 : relYaw * 0.5);
            }
        }
    }
}
