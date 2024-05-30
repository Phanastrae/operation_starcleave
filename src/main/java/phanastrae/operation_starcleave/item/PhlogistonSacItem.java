package phanastrae.operation_starcleave.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import phanastrae.operation_starcleave.block.PhlogisticFireBlock;
import phanastrae.operation_starcleave.duck.EntityDuck;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypeTags;

public class PhlogistonSacItem extends Item {

    public PhlogistonSacItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos().offset(context.getSide());

        if (PhlogisticFireBlock.canPlaceAt(world, blockPos)) {
            this.playUseSound(world, blockPos);
            world.setBlockState(blockPos, PhlogisticFireBlock.getState(world, blockPos));
            world.emitGameEvent(context.getPlayer(), GameEvent.BLOCK_PLACE, blockPos);
            this.empty(context.getStack());
            return ActionResult.success(world.isClient);
        } else {
            return ActionResult.FAIL;
        }
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if(!entity.isAlive() || entity.getType().isIn(OperationStarcleaveEntityTypeTags.PHLOGISTIC_FIRE_IMMUNE)) {
            return ActionResult.FAIL;
        } else {
            World world = entity.getWorld();
            this.playUseSound(world, entity.getPos());
            if (!user.getWorld().isClient) {
                ((EntityDuck) entity).operation_starcleave$setOnPhlogisticFireFor(5);
                world.emitGameEvent(user, GameEvent.ENTITY_ACTION, entity.getPos());
                this.empty(stack);
            }

            return ActionResult.success(user.getWorld().isClient);
        }
    }

    protected void empty(ItemStack stack) {
        stack.decrement(1);
    }

    protected void playUseSound(World world, BlockPos blockPos) {
        playUseSound(world, Vec3d.ofCenter(blockPos));
    }

    protected void playUseSound(World world, Vec3d pos) {
        Random random = world.getRandom();
        world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.15F + 0.75F);
    }
}
