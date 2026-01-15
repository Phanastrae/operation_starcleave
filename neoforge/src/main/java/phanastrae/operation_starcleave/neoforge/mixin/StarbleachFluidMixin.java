package phanastrae.operation_starcleave.neoforge.mixin;

import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.extensions.IFluidExtension;
import net.neoforged.neoforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import phanastrae.operation_starcleave.fluid.StarbleachFluid;
import phanastrae.operation_starcleave.neoforge.fluid.OperationStarcleaveFluidTypes;

@Mixin(StarbleachFluid.class)
public abstract class StarbleachFluidMixin extends Fluid implements IFluidExtension {

    @Override
    public FluidType getFluidType() {
        return OperationStarcleaveFluidTypes.STARBLEACH;
    }
}
