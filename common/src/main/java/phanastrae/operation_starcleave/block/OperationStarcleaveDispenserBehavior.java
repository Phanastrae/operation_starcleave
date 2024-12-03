package phanastrae.operation_starcleave.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;

public class OperationStarcleaveDispenserBehavior {

    public static void init() {
        register(OperationStarcleaveBlocks.NETHERITE_PUMPKIN, new OptionalDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource pointer, ItemStack stack) {
                Level world = pointer.level();
                BlockPos blockPos = pointer.pos().relative(pointer.state().getValue(DispenserBlock.FACING));
                NetheritePumpkinBlock netheritePumpkinBlock = (NetheritePumpkinBlock)OperationStarcleaveBlocks.NETHERITE_PUMPKIN;
                if (world.isEmptyBlock(blockPos) && netheritePumpkinBlock.canSpawnGolem(world, blockPos)) {
                    if (!world.isClientSide) {
                        world.setBlock(blockPos, netheritePumpkinBlock.defaultBlockState(), Block.UPDATE_ALL);
                        world.gameEvent(null, GameEvent.BLOCK_PLACE, blockPos);
                    }

                    stack.shrink(1);
                    this.setSuccess(true);
                } else {
                    this.setSuccess(ArmorItem.dispenseArmor(pointer, stack));
                }

                return stack;
            }
        });

        register(OperationStarcleaveItems.STARBLEACH_BOTTLE, new OptionalDispenseItemBehavior() {
            private final DefaultDispenseItemBehavior fallbackBehavior = new DefaultDispenseItemBehavior();

            private ItemStack replace(BlockSource pointer, ItemStack oldStack, ItemStack newStack) {
                pointer.level().gameEvent(null, GameEvent.FLUID_PICKUP, pointer.pos());
                return this.consumeWithRemainder(pointer, oldStack, newStack);
            }

            @Override
            public ItemStack execute(BlockSource pointer, ItemStack stack) {
                ServerLevel world = pointer.level();
                BlockPos blockPos = pointer.pos().relative(pointer.state().getValue(DispenserBlock.FACING));
                if(StarbleachCauldronBlock.canFillCauldron(world, blockPos)) {
                    StarbleachCauldronBlock.fillCauldron(world, blockPos);
                    this.setSuccess(true);
                    return this.replace(pointer, stack, new ItemStack(Items.GLASS_BOTTLE));
                } else {
                    return super.execute(pointer, stack);
                }
            }
        });

        DispenserBlock.registerProjectileBehavior(OperationStarcleaveItems.STARBLEACHED_PEARL);
        DispenserBlock.registerProjectileBehavior(OperationStarcleaveItems.SPLASH_STARBLEACH_BOTTLE);
        DispenserBlock.registerProjectileBehavior(OperationStarcleaveItems.FIRMAMENT_REJUVENATOR);
    }

    public static void register(ItemLike provider, DispenseItemBehavior behavior) {
        DispenserBlock.registerBehavior(provider, behavior);
    }
}
