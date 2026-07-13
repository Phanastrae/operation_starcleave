package phanastrae.operation_starcleave.mixin.common.block;

import net.minecraft.world.level.block.state.properties.WoodType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.block.OperationStarcleaveWoodTypes;

@Mixin(WoodType.class)
public class WoodTypeMixin {
    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void operation_starcleave$init(CallbackInfo ci) {
        // init wood types now, to make sure they are definitely in the TYPES map before anything (e.g. sign sheets) tries to use it
        OperationStarcleaveWoodTypes.init();
    }
}
