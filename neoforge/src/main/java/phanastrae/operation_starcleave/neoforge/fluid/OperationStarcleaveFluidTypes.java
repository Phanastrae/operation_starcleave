package phanastrae.operation_starcleave.neoforge.fluid;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.neoforge.fluids.FluidType;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.fluid.OperationStarcleaveFluids;

import java.util.function.BiConsumer;

public class OperationStarcleaveFluidTypes {

    public static final FluidType PETRICHORIC_PLASMA = new FluidType(propertiesFromXPlat(OperationStarcleaveFluids.PETRICHORIC_PLASMA_XPGF)
            .descriptionId("block.operation_starcleave.petrichoric_plasma")
    ) {
        @Override
        public void setItemMovement(ItemEntity entity) {
            float gravityMultiplier = 0.25F;

            double gravity = entity.getGravity();
            if (gravity != 0.0) {
                entity.setDeltaMovement(entity.getDeltaMovement().add(0.0, gravity * -gravityMultiplier, 0.0));
            }
        }
    };

    public static void init(BiConsumer<ResourceLocation, FluidType> r) {
        r.accept(id("petrichoric_plasma"), PETRICHORIC_PLASMA);
    }

    private static ResourceLocation id(String path) {
        return OperationStarcleave.id(path);
    }


    public static void registerFluidInteractions() {
    }

    private static FluidType.Properties propertiesFromXPlat(OperationStarcleaveFluids.XPlatGenericFluid xpgf) {
        return FluidType.Properties.create()
                .canSwim(false)
                .canDrown(false)
                .pathType(null)
                .adjacentPathType(null)

                .motionScale(xpgf.getMotionScale())
                .canPushEntity(true)
                .fallDistanceModifier(xpgf.getFallDistanceModifier())
                .canExtinguish(xpgf.canExtinguish())
                .density(xpgf.getDensity())
                .temperature(xpgf.getTemperature())
                .viscosity(xpgf.getViscosity())
                .lightLevel(xpgf.getLuminance());
    }
}
