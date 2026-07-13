package phanastrae.operation_starcleave.block;

import net.minecraft.world.level.block.state.properties.WoodType;
import phanastrae.operation_starcleave.mixin.common.accessor.WoodTypeAccessor;

public class OperationStarcleaveWoodTypes {
    public static final WoodType STARTOUCHED_WOODSET = new WoodType("operation_starcleave:startouched", OperationStarcleaveBlockSetTypes.STARTOUCHED);

    public static void init() {
        WoodTypeAccessor.invokeRegister(STARTOUCHED_WOODSET);
    }
}
