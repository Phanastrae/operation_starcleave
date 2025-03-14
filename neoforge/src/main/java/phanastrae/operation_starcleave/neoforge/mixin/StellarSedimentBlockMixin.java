package phanastrae.operation_starcleave.neoforge.mixin;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.extensions.IBlockExtension;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.block.StellarSedimentBlock;

@Mixin(StellarSedimentBlock.class)
public class StellarSedimentBlockMixin implements IBlockExtension {
    // this seems stupid but it works fine

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        if (ItemAbilities.HOE_TILL == itemAbility) {
            return OperationStarcleaveBlocks.STELLAR_FARMLAND.defaultBlockState();
        }
        return IBlockExtension.super.getToolModifiedState(state, context, itemAbility, simulate);
    }
}
