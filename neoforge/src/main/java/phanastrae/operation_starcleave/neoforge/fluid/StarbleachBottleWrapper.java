package phanastrae.operation_starcleave.neoforge.fluid;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;
import phanastrae.operation_starcleave.fluid.OperationStarcleaveFluids;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;

public class StarbleachBottleWrapper extends FluidBucketWrapper {

    public StarbleachBottleWrapper(ItemStack container) {
        super(container);
    }

    @Override
    public boolean canFillFluidType(FluidStack fluid) {
        return fluid.is(OperationStarcleaveFluids.STARBLEACH);
    }

    @Override
    public FluidStack getFluid() {
        Item item = container.getItem();
        if (item.equals(OperationStarcleaveItems.STARBLEACH_BOTTLE)) {
            return new FluidStack(OperationStarcleaveFluids.STARBLEACH, FluidType.BUCKET_VOLUME / 4);
        } else {
            return FluidStack.EMPTY;
        }
    }

    @Override
    protected void setFluid(FluidStack fluidStack) {
        if (fluidStack.isEmpty() || fluidStack.getFluid() != OperationStarcleaveFluids.STARBLEACH)
            container = new ItemStack(Items.GLASS_BOTTLE);
        else
            container = new ItemStack(OperationStarcleaveItems.STARBLEACH_BOTTLE);
    }

    @Override
    public int getTankCapacity(int tank) {
        return FluidType.BUCKET_VOLUME / 4;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (container.getCount() != 1 || resource.getAmount() < FluidType.BUCKET_VOLUME / 4 || !getFluid().isEmpty() || !canFillFluidType(resource)) {
            return 0;
        }

        if (action.execute()) {
            setFluid(resource);
        }

        return FluidType.BUCKET_VOLUME / 4;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (container.getCount() != 1 || resource.getAmount() < FluidType.BUCKET_VOLUME / 4) {
            return FluidStack.EMPTY;
        }

        FluidStack fluidStack = getFluid();
        if (!fluidStack.isEmpty() && FluidStack.isSameFluidSameComponents(fluidStack, resource)) {
            if (action.execute()) {
                setFluid(FluidStack.EMPTY);
            }
            return fluidStack;
        }

        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (container.getCount() != 1 || maxDrain < FluidType.BUCKET_VOLUME / 4) {
            return FluidStack.EMPTY;
        }

        FluidStack fluidStack = getFluid();
        if (!fluidStack.isEmpty()) {
            if (action.execute()) {
                setFluid(FluidStack.EMPTY);
            }
            return fluidStack;
        }

        return FluidStack.EMPTY;
    }
}
