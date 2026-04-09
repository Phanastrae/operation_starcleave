package phanastrae.operation_starcleave.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;

public class OperationStarcleaveDispenserBehavior {

    public static void init() {
        registerProjectileBehavior(OperationStarcleaveItems.STARBLEACHED_PEARL);
        registerProjectileBehavior(OperationStarcleaveItems.SPLASH_STARBLEACH_BOTTLE);
        registerProjectileBehavior(OperationStarcleaveItems.FIRMAMENT_REJUVENATOR);

        DispenseItemBehavior fluidBucketBehaviour = new DefaultDispenseItemBehavior() {
            private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

            @Override
            public ItemStack execute(BlockSource p_338850_, ItemStack p_338251_) {
                DispensibleContainerItem dispensiblecontaineritem = (DispensibleContainerItem) p_338251_.getItem();
                BlockPos blockpos = p_338850_.pos().relative(p_338850_.state().getValue(DispenserBlock.FACING));
                Level level = p_338850_.level();
                if (dispensiblecontaineritem.emptyContents(null, level, blockpos, null)) {
                    dispensiblecontaineritem.checkExtraContent(null, level, p_338251_, blockpos);
                    return this.consumeWithRemainder(p_338850_, p_338251_, new ItemStack(Items.BUCKET));
                } else {
                    return this.defaultDispenseItemBehavior.dispense(p_338850_, p_338251_);
                }
            }
        };
        DispenserBlock.registerBehavior(OperationStarcleaveItems.STARBLEACH_BUCKET, fluidBucketBehaviour);

        register(OperationStarcleaveItems.PETRICHORIC_PLASMA_BUCKET, new DefaultDispenseItemBehavior() {
            private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

            @Override
            public ItemStack execute(BlockSource blockSource, ItemStack stack) {
                DispensibleContainerItem dispensibleContainerItem = (DispensibleContainerItem) stack.getItem();
                BlockPos blockPos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));
                Level level = blockSource.level();
                if (dispensibleContainerItem.emptyContents(null, level, blockPos, null)) {
                    dispensibleContainerItem.checkExtraContent(null, level, stack, blockPos);

                    level.playSound(
                            null,
                            blockPos,
                            Items.IRON_PICKAXE.getBreakingSound(),
                            SoundSource.BLOCKS,
                            0.8F,
                            1.0F
                    );

                    return this.consumeWithRemainder(blockSource, stack, new ItemStack(OperationStarcleaveItems.LIMESLAGGED_BUCKET));
                } else {
                    return this.defaultDispenseItemBehavior.dispense(blockSource, stack);
                }
            }
        });

        register(OperationStarcleaveBlocks.NETHERITE_PUMPKIN, new OptionalDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource pointer, ItemStack stack) {
                Level world = pointer.level();
                BlockPos blockPos = pointer.pos().relative(pointer.state().getValue(DispenserBlock.FACING));
                NetheritePumpkinBlock netheritePumpkinBlock = (NetheritePumpkinBlock) OperationStarcleaveBlocks.NETHERITE_PUMPKIN;
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
            private ItemStack replace(BlockSource pointer, ItemStack oldStack, ItemStack newStack) {
                pointer.level().gameEvent(null, GameEvent.FLUID_PICKUP, pointer.pos());
                return this.consumeWithRemainder(pointer, oldStack, newStack);
            }

            @Override
            public ItemStack execute(BlockSource pointer, ItemStack stack) {
                ServerLevel world = pointer.level();
                BlockPos blockPos = pointer.pos().relative(pointer.state().getValue(DispenserBlock.FACING));
                if (StarbleachCauldronBlock.canFillCauldron(world, blockPos, 1)) {
                    StarbleachCauldronBlock.fillCauldron(world, blockPos, 1);
                    this.setSuccess(true);
                    return this.replace(pointer, stack, new ItemStack(Items.GLASS_BOTTLE));
                } else {
                    return super.execute(pointer, stack);
                }
            }
        });

        // spawn eggs
        DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior() {
            @Override
            public ItemStack execute(BlockSource blockSource, ItemStack item) {
                Direction direction = blockSource.state().getValue(DispenserBlock.FACING);
                EntityType<?> entityType = ((SpawnEggItem) item.getItem()).getType(item);

                try {
                    entityType.spawn(blockSource.level(), item, null, blockSource.pos().relative(direction), MobSpawnType.DISPENSER, direction != Direction.UP, false);
                } catch (Exception exception) {
                    OperationStarcleave.LOGGER.error("Error while dispensing spawn egg from dispenser at {}", blockSource.pos(), exception);
                    return ItemStack.EMPTY;
                }

                item.shrink(1);
                blockSource.level().gameEvent(null, GameEvent.ENTITY_PLACE, blockSource.pos());
                return item;
            }
        };

        for (SpawnEggItem item : OperationStarcleaveItems.SPAWN_EGGS) {
            register(item, defaultDispenseItemBehavior);
        }
    }

    public static void register(ItemLike provider, DispenseItemBehavior behavior) {
        DispenserBlock.registerBehavior(provider, behavior);
    }

    public static void registerProjectileBehavior(ItemLike provider) {
        DispenserBlock.registerProjectileBehavior(provider);
    }
}
