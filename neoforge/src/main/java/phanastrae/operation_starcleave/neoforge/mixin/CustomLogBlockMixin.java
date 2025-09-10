package phanastrae.operation_starcleave.neoforge.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.extensions.IBlockExtension;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import phanastrae.operation_starcleave.block.CustomLogBlock;
import phanastrae.operation_starcleave.block.OperationStarcleaveLogStripping;

@Mixin(CustomLogBlock.class)
public abstract class CustomLogBlockMixin extends RotatedPillarBlock implements IBlockExtension {
    private CustomLogBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        ItemStack itemStack = context.getItemInHand();
        if (!itemStack.canPerformAction(itemAbility))
            return null;

        if(ItemAbilities.AXE_STRIP == itemAbility) {
            Block block = state.getBlock();
            if(OperationStarcleaveLogStripping.STARCLEAVE_STRIPPABLES.containsKey(block)) {
                Block newBlock = OperationStarcleaveLogStripping.STARCLEAVE_STRIPPABLES.get(block);
                return newBlock.defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS));
            }
        }

        return super.getToolModifiedState(state, context, itemAbility, simulate);
    }
}
