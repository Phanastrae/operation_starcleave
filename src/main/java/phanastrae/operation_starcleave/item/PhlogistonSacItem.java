package phanastrae.operation_starcleave.item;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import phanastrae.operation_starcleave.block.PhlogisticFireBlock;
import phanastrae.operation_starcleave.duck.EntityDuck;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypeTags;

public class PhlogistonSacItem extends Item {

    public PhlogistonSacItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos blockPos = context.getClickedPos().relative(context.getClickedFace());

        if (PhlogisticFireBlock.canPlaceAt(world, blockPos)) {
            this.playUseSound(world, blockPos);
            world.setBlockAndUpdate(blockPos, PhlogisticFireBlock.getState(world, blockPos));
            world.gameEvent(context.getPlayer(), GameEvent.BLOCK_PLACE, blockPos);
            this.empty(context.getItemInHand());
            return InteractionResult.sidedSuccess(world.isClientSide);
        } else {
            return InteractionResult.FAIL;
        }
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity entity, InteractionHand hand) {
        if(!entity.isAlive() || entity.getType().is(OperationStarcleaveEntityTypeTags.PHLOGISTIC_FIRE_IMMUNE)) {
            return InteractionResult.FAIL;
        } else {
            Level world = entity.level();
            this.playUseSound(world, entity.position());
            if (!user.level().isClientSide) {
                ((EntityDuck) entity).operation_starcleave$setOnPhlogisticFireFor(5);
                world.gameEvent(user, GameEvent.ENTITY_ACTION, entity.position());
                this.empty(stack);
            }

            return InteractionResult.sidedSuccess(user.level().isClientSide);
        }
    }

    protected void empty(ItemStack stack) {
        stack.shrink(1);
    }

    protected void playUseSound(Level world, BlockPos blockPos) {
        playUseSound(world, Vec3.atCenterOf(blockPos));
    }

    protected void playUseSound(Level world, Vec3 pos) {
        RandomSource random = world.getRandom();
        world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.15F + 0.75F);
    }
}
