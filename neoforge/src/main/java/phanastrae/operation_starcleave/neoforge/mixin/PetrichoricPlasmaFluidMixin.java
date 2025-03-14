package phanastrae.operation_starcleave.neoforge.mixin;

import net.minecraft.world.level.material.FlowingFluid;
import net.neoforged.neoforge.common.extensions.IFluidExtension;
import net.neoforged.neoforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import phanastrae.operation_starcleave.fluid.PetrichoricPlasmaFluid;
import phanastrae.operation_starcleave.neoforge.fluid.OperationStarcleaveFluidTypes;

@Mixin(PetrichoricPlasmaFluid.class)
public abstract class PetrichoricPlasmaFluidMixin extends FlowingFluid implements IFluidExtension {

    @Override
    public FluidType getFluidType() {
        return OperationStarcleaveFluidTypes.PETRICHORIC_PLASMA;
    }
}
