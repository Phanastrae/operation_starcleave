package phanastrae.operation_starcleave.block;

import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;

public class OperationStarcleaveDispenserBehavior {

    public static void init() {
        register(OperationStarcleaveBlocks.NETHERITE_PUMPKIN, new FallibleItemDispenserBehavior() {
            @Override
            protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                World world = pointer.world();
                BlockPos blockPos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
                NetheritePumpkinBlock netheritePumpkinBlock = (NetheritePumpkinBlock)OperationStarcleaveBlocks.NETHERITE_PUMPKIN;
                if (world.isAir(blockPos) && netheritePumpkinBlock.canDispense(world, blockPos)) {
                    if (!world.isClient) {
                        world.setBlockState(blockPos, netheritePumpkinBlock.getDefaultState(), Block.NOTIFY_ALL);
                        world.emitGameEvent(null, GameEvent.BLOCK_PLACE, blockPos);
                    }

                    stack.decrement(1);
                    this.setSuccess(true);
                } else {
                    this.setSuccess(ArmorItem.dispenseArmor(pointer, stack));
                }

                return stack;
            }
        });

        register(OperationStarcleaveItems.STARBLEACH_BOTTLE, new FallibleItemDispenserBehavior() {
            private final ItemDispenserBehavior fallbackBehavior = new ItemDispenserBehavior();

            private ItemStack replace(BlockPointer pointer, ItemStack oldStack, ItemStack newStack) {
                pointer.world().emitGameEvent(null, GameEvent.FLUID_PICKUP, pointer.pos());
                return this.decrementStackWithRemainder(pointer, oldStack, newStack);
            }

            @Override
            public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                ServerWorld world = pointer.world();
                BlockPos blockPos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
                if(StarbleachCauldronBlock.canFillCauldron(world, blockPos)) {
                    StarbleachCauldronBlock.fillCauldron(world, blockPos);
                    this.setSuccess(true);
                    return this.replace(pointer, stack, new ItemStack(Items.GLASS_BOTTLE));
                } else {
                    return super.dispenseSilently(pointer, stack);
                }
            }
        });

        DispenserBlock.registerProjectileBehavior(OperationStarcleaveItems.STARBLEACHED_PEARL);
        DispenserBlock.registerProjectileBehavior(OperationStarcleaveItems.SPLASH_STARBLEACH_BOTTLE);
        DispenserBlock.registerProjectileBehavior(OperationStarcleaveItems.FIRMAMENT_REJUVENATOR);
    }

    public static void register(ItemConvertible provider, DispenserBehavior behavior) {
        DispenserBlock.registerBehavior(provider, behavior);
    }
}
