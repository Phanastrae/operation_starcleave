package phanastrae.operation_starcleave.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import phanastrae.operation_starcleave.OperationStarcleave;

public class OperationStarcleaveFluidTags {
    public static final TagKey<Fluid> PETRICHORIC_PLASMA = of("petrichoric_plasma");

    private static TagKey<Fluid> of(String id) {
        return TagKey.create(Registries.FLUID, OperationStarcleave.id(id));
    }
}
