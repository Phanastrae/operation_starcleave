package phanastrae.operation_starcleave.mixin.common.accessor.item;

import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ShovelItem.class)
public interface ShovelItemAccessor {
    @Accessor
    static Map<Block, BlockState> getFLATTENABLES() {
        throw new AssertionError();
    }
}
