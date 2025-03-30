package phanastrae.operation_starcleave.item;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;

public class HollowedSacItem extends Item {
    public HollowedSacItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player user = context.getPlayer();
        if(user == null) return InteractionResult.FAIL;

        Level world = context.getLevel();
        BlockPos blockPos = context.getClickedPos();

        if (world.getBlockState(blockPos).is(OperationStarcleaveBlocks.PHLOGISTIC_FIRE)) {
            this.playUseSound(world, blockPos);
            world.setBlockAndUpdate(blockPos, getNewBlockState(world, blockPos));
            world.gameEvent(user, GameEvent.FLUID_PICKUP, blockPos);
            this.fill(context.getItemInHand(), user, context.getHand());
            return InteractionResult.sidedSuccess(world.isClientSide);
        } else {
            return InteractionResult.FAIL;
        }
    }

    protected void fill(ItemStack stack, Player player, InteractionHand hand) {
        ItemStack newStack = new ItemStack(OperationStarcleaveItems.PHLOGISTON_SAC);
        player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, newStack));
    }

    protected BlockState getNewBlockState(Level world, BlockPos blockPos) {
        return (world.isWaterAt(blockPos) ? Blocks.WATER : Blocks.AIR).defaultBlockState();
    }

    protected void playUseSound(Level world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.GLOW_INK_SAC_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }
}
