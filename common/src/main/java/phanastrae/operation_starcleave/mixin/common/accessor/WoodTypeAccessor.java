package phanastrae.operation_starcleave.mixin.common.accessor;

import net.minecraft.world.level.block.state.properties.WoodType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WoodType.class)
public interface WoodTypeAccessor {
    @Invoker
    static WoodType invokeRegister(WoodType woodType) {
        throw new AssertionError();
    }
}
