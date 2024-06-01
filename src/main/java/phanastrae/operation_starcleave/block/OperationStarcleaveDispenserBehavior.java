package phanastrae.operation_starcleave.block;

import net.minecraft.block.*;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.*;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import phanastrae.operation_starcleave.entity.projectile.FirmamentRejuvenatorEntity;
import phanastrae.operation_starcleave.entity.projectile.SplashStarbleachEntity;
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

        register(OperationStarcleaveItems.STARBLEACH_BOTTLE, new ItemDispenserBehavior() {
            private final ItemDispenserBehavior fallbackBehavior = new ItemDispenserBehavior();

            @Override
            public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                World world = pointer.world();
                BlockPos blockPos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
                if(StarbleachCauldronBlock.canFillCauldron(world, blockPos)) {
                    if (!world.isClient) {
                        StarbleachCauldronBlock.fillCauldron(world, blockPos);
                    }

                    stack.decrement(1);
                    Item item = Items.GLASS_BOTTLE;
                    if(stack.isEmpty()) {
                        return new ItemStack(item);
                    } else {
                        if (pointer.blockEntity().addToFirstFreeSlot(new ItemStack(item)) < 0) {
                            this.fallbackBehavior.dispense(pointer, new ItemStack(item));
                        }

                        return stack;
                    }
                } else {
                    return super.dispenseSilently(pointer, stack);
                }
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

        register(OperationStarcleaveItems.SPLASH_STARBLEACH_BOTTLE, new ProjectileDispenserBehavior() {
            @Override
            protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
                return Util.make(new SplashStarbleachEntity(world, position.getX(), position.getY(), position.getZ()),
                        entity -> {
                            entity.setItem(stack);
                            entity.setCanStarbleach(true);
                        });
            }

            @Override
            protected float getVariation() {
                return super.getVariation() * 0.5F;
            }

            @Override
            protected float getForce() {
                return super.getForce() * 1.25F;
            }
        });

        register(OperationStarcleaveItems.FIRMAMENT_REJUVENATOR, new ProjectileDispenserBehavior() {
            @Override
            protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
                return Util.make(new FirmamentRejuvenatorEntity(world, position.getX(), position.getY(), position.getZ()),
                        entity -> entity.setItem(stack));
            }

            @Override
            protected float getVariation() {
                return super.getVariation() * 0.1F;
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
