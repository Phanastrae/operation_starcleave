package phanastrae.operation_starcleave.neoforge.fluid;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;

public class PetrichoricPlasmaFluidBucketWrapper extends FluidBucketWrapper {
    public PetrichoricPlasmaFluidBucketWrapper(ItemStack container) {
        super(container);
    }

    @Override
    protected void setFluid(FluidStack fluidStack) {
        if (fluidStack.isEmpty())
            container = new ItemStack(OperationStarcleaveItems.LIMESLAGGED_BUCKET);
        else
            container = FluidUtil.getFilledBucket(fluidStack);
    }
}
