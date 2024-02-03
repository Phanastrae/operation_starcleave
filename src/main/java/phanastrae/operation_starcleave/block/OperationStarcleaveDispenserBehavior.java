package phanastrae.operation_starcleave.block;

import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import phanastrae.operation_starcleave.entity.projectile.StarbleachedPearlEntity;
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

        register(OperationStarcleaveItems.STARBLEACHED_PEARL, new ProjectileDispenserBehavior() {
            @Override
            protected StarbleachedPearlEntity createProjectile(World world, Position position, ItemStack stack) {
                return Util.make(new StarbleachedPearlEntity(world, position.getX(), position.getY(), position.getZ()), entity -> entity.setItem(stack));
            }

            @Override
            protected float getForce() {
                return super.getForce() * 1.5F;
            }
        });
    }

    public static void register(ItemConvertible provider, DispenserBehavior behavior) {
        DispenserBlock.registerBehavior(provider, behavior);
    }
}
