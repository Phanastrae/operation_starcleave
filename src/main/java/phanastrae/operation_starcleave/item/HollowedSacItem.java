package phanastrae.operation_starcleave.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;

public class HollowedSacItem extends Item {
    public HollowedSacItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity user = context.getPlayer();
        if(user == null) return ActionResult.FAIL;

        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();

        if (world.getBlockState(blockPos).isOf(OperationStarcleaveBlocks.PHLOGISTIC_FIRE)) {
            this.playUseSound(world, blockPos);
            world.setBlockState(blockPos, getNewBlockState(world, blockPos));
            world.emitGameEvent(user, GameEvent.FLUID_PICKUP, blockPos);
            this.fill(context.getStack(), user);
            return ActionResult.success(world.isClient);
        } else {
            return ActionResult.FAIL;
        }
    }

    protected void fill(ItemStack stack, PlayerEntity player) {
        ItemStack newStack = new ItemStack(OperationStarcleaveItems.PHLOGISTON_SAC);
        ItemUsage.exchangeStack(stack, player, newStack);
    }

    protected BlockState getNewBlockState(World world, BlockPos blockPos) {
        return (world.isWater(blockPos) ? Blocks.WATER : Blocks.AIR).getDefaultState();
    }

    protected void playUseSound(World world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.ITEM_GLOW_INK_SAC_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }
}
