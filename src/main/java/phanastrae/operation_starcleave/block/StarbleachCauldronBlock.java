package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import phanastrae.operation_starcleave.entity.OperationStarcleaveDamageTypes;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;
import phanastrae.operation_starcleave.item.StarbleachCoating;

import static net.minecraft.block.cauldron.CauldronBehavior.createMap;

public class StarbleachCauldronBlock extends AbstractCauldronBlock {
    public static final MapCodec<StarbleachCauldronBlock> CODEC = createCodec(StarbleachCauldronBlock::new);
    public static CauldronBehavior.CauldronBehaviorMap STARBLEACH_CAULDRON_BEHAVIOR = createMap("operation_starcleave:starbleach");

    @Override
    protected MapCodec<? extends AbstractCauldronBlock> getCodec() {
        return CODEC;
    }

    public StarbleachCauldronBlock(Settings settings) {
        super(settings, STARBLEACH_CAULDRON_BEHAVIOR);
        this.setDefaultState(this.stateManager.getDefaultState().with(LeveledCauldronBlock.LEVEL, Integer.valueOf(1)));
    }

    @Override
    public boolean isFull(BlockState state) {
        return state.get(LeveledCauldronBlock.LEVEL) == 3;
    }

    @Override
    protected boolean canBeFilledByDripstone(Fluid fluid) {
        return false;
    }

    @Override
    protected double getFluidHeight(BlockState state) {
        return (6.0 + (double)((Integer)state.get(LeveledCauldronBlock.LEVEL)).intValue() * 3.0) / 16.0;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (this.isEntityTouchingFluid(state, pos, entity)) {
            entity.damage(OperationStarcleaveDamageTypes.of(world, OperationStarcleaveDamageTypes.INTERNAL_STARBLEACHING), 0.5f * state.get(LeveledCauldronBlock.LEVEL));
        }
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return state.get(LeveledCauldronBlock.LEVEL);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LeveledCauldronBlock.LEVEL);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        if(StarbleachCoating.canAddStarbleach(itemStack)) {
            if (!world.isClient) {
                player.incrementStat(Stats.USE_CAULDRON);
                ItemStack coatedStack = itemStack.copy();
                coatedStack.setCount(1);
                StarbleachCoating.addStarbleach(coatedStack);

                player.setStackInHand(hand, ItemUsage.exchangeStack(itemStack, player, coatedStack));

                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
                world.playSound(null, pos, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0F, 1.5F);
                world.emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);
            }
            return ActionResult.success(world.isClient);
        } else {
            return super.onUse(state, world, pos, player, hand, hit);
        }
    }

    public static void init() {
        CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR.map().put(OperationStarcleaveItems.STARBLEACH_BOTTLE, (state, world, pos, player, hand, stack) -> {
            if (!world.isClient) {
                Item item = stack.getItem();
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                player.incrementStat(Stats.USE_CAULDRON);
                player.incrementStat(Stats.USED.getOrCreateStat(item));
                world.setBlockState(pos, OperationStarcleaveBlocks.STARBLEACH_CAULDRON.getDefaultState());
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
            }

            return ActionResult.success(world.isClient);
        });

        StarbleachCauldronBlock.STARBLEACH_CAULDRON_BEHAVIOR.map().put(OperationStarcleaveItems.STARBLEACH_BOTTLE, (state, world, pos, player, hand, stack) -> {
            if (state.get(LeveledCauldronBlock.LEVEL) != 3) {
                if (!world.isClient) {
                    player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                    player.incrementStat(Stats.USE_CAULDRON);
                    player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
                    world.setBlockState(pos, state.cycle(LeveledCauldronBlock.LEVEL));
                    world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
                }

                return ActionResult.success(world.isClient);
            } else {
                return ActionResult.PASS;
            }
        });

        StarbleachCauldronBlock.STARBLEACH_CAULDRON_BEHAVIOR.map().put(Items.GLASS_BOTTLE, (state, world, pos, player, hand, stack) -> {
            if (!world.isClient) {
                Item item = stack.getItem();
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(OperationStarcleaveItems.STARBLEACH_BOTTLE)));
                player.incrementStat(Stats.USE_CAULDRON);
                player.incrementStat(Stats.USED.getOrCreateStat(item));
                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);
            }

            return ActionResult.success(world.isClient);
        });

        StarbleachCauldronBlock.STARBLEACH_CAULDRON_BEHAVIOR.map().put(Items.ENDER_PEARL, (state, world, pos, player, hand, stack) -> {
            if (!world.isClient) {
                Item item = stack.getItem();
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(OperationStarcleaveItems.STARBLEACHED_PEARL)));
                player.incrementStat(Stats.USE_CAULDRON);
                player.incrementStat(Stats.USED.getOrCreateStat(item));
                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
                world.playSound(null, pos, SoundEvents.ENTITY_ENDERMAN_SCREAM, SoundCategory.BLOCKS, 0.3F, 1.6F);
                world.emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);
            }

            return ActionResult.success(world.isClient);
        });

        StarbleachCauldronBlock.STARBLEACH_CAULDRON_BEHAVIOR.map().put(Items.CHORUS_FRUIT, (state, world, pos, player, hand, stack) -> {
            if (!world.isClient) {
                Item item = stack.getItem();
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(OperationStarcleaveItems.STARFRUIT)));
                player.incrementStat(Stats.USE_CAULDRON);
                player.incrementStat(Stats.USED.getOrCreateStat(item));
                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
                world.playSound(null, pos, SoundEvents.BLOCK_CHORUS_FLOWER_GROW, SoundCategory.BLOCKS, 0.3F, 1.6F);
                world.emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);
            }

            return ActionResult.success(world.isClient);
        });

        StarbleachCauldronBlock.STARBLEACH_CAULDRON_BEHAVIOR.map().put(Items.INK_SAC, (state, world, pos, player, hand, stack) -> {
            if (!world.isClient) {
                Item item = stack.getItem();
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(OperationStarcleaveItems.HOLLOWED_SAC)));
                player.incrementStat(Stats.USE_CAULDRON);
                player.incrementStat(Stats.USED.getOrCreateStat(item));
                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
                world.playSound(null, pos, SoundEvents.ITEM_GLOW_INK_SAC_USE, SoundCategory.BLOCKS, 0.3F, 1.6F);
                world.emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);
            }

            return ActionResult.success(world.isClient);
        });
    }
}
