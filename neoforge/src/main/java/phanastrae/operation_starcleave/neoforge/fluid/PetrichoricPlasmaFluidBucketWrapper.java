package phanastrae.operation_starcleave.neoforge.fluid;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;

public class PetrichoricPlasmaFluidBucketWrapper extends FluidBucketWrapper {
    public PetrichoricPlasmaFluidBucketWrapper(ItemStack container) {
        super(container);
    }

    @Override

    protected void setFluid(FluidStack fluidStack) {
        if (fluidStack.isEmpty())
            container = new ItemStack(Items.AIR);
        else
            container = FluidUtil.getFilledBucket(fluidStack);
    }
}
