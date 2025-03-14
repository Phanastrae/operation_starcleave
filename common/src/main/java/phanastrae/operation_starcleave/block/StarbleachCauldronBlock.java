package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.entity.OperationStarcleaveDamageTypes;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;
import phanastrae.operation_starcleave.item.StarbleachCoating;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;
import phanastrae.operation_starcleave.recipe.ItemStarbleachingRecipe;
import phanastrae.operation_starcleave.recipe.OperationStarcleaveRecipeTypes;
import phanastrae.operation_starcleave.recipe.input.ItemStarbleachingRecipeInput;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import static net.minecraft.core.cauldron.CauldronInteraction.newInteractionMap;

public class StarbleachCauldronBlock extends AbstractCauldronBlock {
    public static final MapCodec<StarbleachCauldronBlock> CODEC = simpleCodec(StarbleachCauldronBlock::new);
    public static final int MAX_STARBLEACH_LEVEL = 7;
    public static final IntegerProperty LEVEL_7 = IntegerProperty.create("level", 1, MAX_STARBLEACH_LEVEL);
    public static CauldronInteraction.InteractionMap STARBLEACH_CAULDRON_BEHAVIOR = newInteractionMap("operation_starcleave:starbleach");

    public static final float STARBLEACHING_FOOD_COST = 0.25F;

    @Override
    protected MapCodec<? extends AbstractCauldronBlock> codec() {
        return CODEC;
    }

    public StarbleachCauldronBlock(Properties settings) {
        super(settings, STARBLEACH_CAULDRON_BEHAVIOR);
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL_7, 1));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL_7);
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        return state.getValue(LEVEL_7);
    }

    @Override
    public boolean isFull(BlockState state) {
        return state.getValue(LEVEL_7) == MAX_STARBLEACH_LEVEL;
    }

    @Override
    protected boolean canReceiveStalactiteDrip(Fluid fluid) {
        return false;
    }

    @Override
    protected double getContentHeight(BlockState state) {
        return (8.0 + state.getValue(LEVEL_7)) / 16.0;
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if(world.isClientSide) {
            return;
        }

        if (this.isEntityInsideContent(state, pos, entity)) {
            if(entity instanceof ItemEntity itemEntity) {
                ItemStack itemStack = itemEntity.getItem();

                Entity owner = itemEntity.getOwner();
                Player playerOwner = owner instanceof Player ? (Player)owner : null;
                Optional<ItemStack> optionalItemStack = attemptCraft(world, itemStack, pos, playerOwner, true);
                if(optionalItemStack.isPresent()) {
                    ItemStack outputStack = optionalItemStack.get();
                    ItemEntity newEntity = new ItemEntity(world,
                            pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                            outputStack);
                    world.addFreshEntity(newEntity);

                    if (itemStack.isEmpty()) {
                        itemEntity.discard();
                    }

                    return;
                }
            }

            entity.hurt(OperationStarcleaveDamageTypes.source(world, OperationStarcleaveDamageTypes.INTERNAL_STARBLEACHING), 0.25f * state.getValue(LEVEL_7));
            if(!entity.isAlive() && world instanceof ServerLevel serverWorld) {
                spawnParticles(serverWorld, pos);
            }
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getItemInHand(hand);

        boolean creativeMode = player.getAbilities().instabuild;
        Optional<ItemStack> optionalItemStack = attemptCraft(world, itemStack, pos, player, !creativeMode);
        if(optionalItemStack.isPresent()) {
            if(!world.isClientSide) {
                ItemStack outputStack = optionalItemStack.get();
                if(creativeMode) {
                    if (!player.getInventory().contains(outputStack)) {
                        player.getInventory().add(outputStack);
                    }
                } else {
                    if (itemStack.isEmpty()) {
                        player.setItemInHand(hand, outputStack);
                    } else {
                        if (!player.getInventory().add(outputStack)) {
                            player.drop(outputStack, false);
                        }
                    }
                }
            }

            return ItemInteractionResult.sidedSuccess(world.isClientSide);
        }

        CauldronInteraction cauldronBehavior = this.interactions.map().get(itemStack.getItem());
        return cauldronBehavior.interact(state, world, pos, player, hand, itemStack);
    }

    // Attempt to craft using the input item
    // If the recipe is successful, returns the output stack. On the client this is just an empty stack
    // If the recipe fails, returns an empty Optional
    public Optional<ItemStack> attemptCraft(Level world, ItemStack input, BlockPos pos, @Nullable Player player, boolean decrement) {
        ItemStarbleachingRecipe recipe = getRecipe(world, input);
        if(recipe != null) {
            if(canEmptyCauldron(world, pos, recipe.getRequiredStarbleachToAttemptCraft())) {
                if(!world.isClientSide) {
                    craft(world, pos, recipe.getStarbleachCost(), !recipe.getIsFillingRecipe(), input, player, decrement);
                    return Optional.of(recipe.getOutputStack());
                } else {
                    return Optional.of(ItemStack.EMPTY);
                }
            }
        }

        if(StarbleachCoating.canAddStarbleach(input)) {
            if(canEmptyCauldron(world, pos, 1)) {
                if(!world.isClientSide) {
                    ItemStack coatedStack = input.copy();
                    coatedStack.setCount(1);
                    StarbleachCoating.addStarbleach(coatedStack);

                    craft(world, pos, STARBLEACHING_FOOD_COST, true, input, player, decrement);
                    return Optional.of(coatedStack);
                } else {
                    return Optional.of(ItemStack.EMPTY);
                }
            }
        }

        return Optional.empty();
    }

    public void craft(Level world, BlockPos blockPos, float starbleachCost, boolean spawnParticles, ItemStack input, @Nullable Player player, boolean decrement) {
        if(player != null) {
            incrementStats(player, input);
        }
        emptyCauldron(world, blockPos, ItemStarbleachingRecipe.getConsumedStarbleach(world.getRandom(), starbleachCost), spawnParticles);

        if(decrement) {
            input.shrink(1);
        }
    }

    @Nullable
    public static ItemStarbleachingRecipe getRecipe(Level world, ItemStack stack) {
        ItemStarbleachingRecipeInput input = new ItemStarbleachingRecipeInput(stack);
        Optional<RecipeHolder<ItemStarbleachingRecipe>> recipeEntryOptional = world.getRecipeManager().getRecipeFor(OperationStarcleaveRecipeTypes.ITEM_STARBLEACHING, input, world);
        return recipeEntryOptional.map(RecipeHolder::value).orElse(null);
    }

    public static void spawnParticles(ServerLevel world, BlockPos blockPos) {
        Vec3 pos = Vec3.atCenterOf(blockPos);
        world.sendParticles(OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER,
                pos.x(), pos.y(), pos.z(),
                400,
                0.2, 0.4, 0.2,
                0.005);
    }

    private static CauldronInteraction getFillingBehaviour() {
        return (state, world, pos, player, hand, stack) ->
        {
            if (canFillCauldron(world, pos)) {
                if (!world.isClientSide) {
                    incrementStats(player, stack);
                    player.awardStat(Stats.FILL_CAULDRON);
                    fillCauldron(world, pos);
                    ItemStack outputStack = Items.GLASS_BOTTLE.getDefaultInstance();
                    player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, outputStack));
                }

                return ItemInteractionResult.sidedSuccess(world.isClientSide);
            } else {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
        };
    }

    public static void incrementStats(Player player, @Nullable ItemStack itemStack) {
        if(itemStack != null && !itemStack.isEmpty()) {
            player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
        }
    }

    public static void fillCauldron(Level world, BlockPos blockPos) {
        BlockState state = world.getBlockState(blockPos);
        int currentStarbleachLevel = getStarbleachLevel(state);

        world.setBlockAndUpdate(blockPos, getStateWithStarbleachLevel(currentStarbleachLevel + 1));

        world.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(state));
        world.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);

        world.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public static void emptyCauldron(Level world, BlockPos blockPos, int amount, boolean spawnParticles) {
        BlockState state = world.getBlockState(blockPos);
        int currentStarbleachLevel = getStarbleachLevel(state);

        world.setBlockAndUpdate(blockPos, getStateWithStarbleachLevel(currentStarbleachLevel - amount));

        world.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(state));
        world.gameEvent(null, GameEvent.FLUID_PICKUP, blockPos);

        if(spawnParticles) {
            world.playSound(null, blockPos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.5F);
        } else {
            world.playSound(null, blockPos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        if(spawnParticles) {
            if(world instanceof ServerLevel serverWorld) {
                spawnParticles(serverWorld, blockPos);
            }
        }
    }

    public static boolean canFillCauldron(Level world, BlockPos blockPos) {
        BlockState blockState = world.getBlockState(blockPos);
        if(blockState.is(Blocks.CAULDRON) || blockState.is(OperationStarcleaveBlocks.STARBLEACH_CAULDRON)) {
            int currentStarbleachLevel = getStarbleachLevel(blockState);
            return currentStarbleachLevel != MAX_STARBLEACH_LEVEL;
        } else {
            return false;
        }
    }

    public static boolean canEmptyCauldron(Level world, BlockPos blockPos, int amount) {
        BlockState blockState = world.getBlockState(blockPos);
        if(blockState.is(OperationStarcleaveBlocks.STARBLEACH_CAULDRON)) {
            int currentStarbleachLevel = getStarbleachLevel(blockState);

            return currentStarbleachLevel >= amount;
        } else {
            return false;
        }
    }

    public static int getStarbleachLevel(BlockState state) {
        if(state.is(OperationStarcleaveBlocks.STARBLEACH_CAULDRON)) {
            return state.getValue(LEVEL_7);
        } else {
            return 0;
        }
    }

    public static BlockState getStateWithStarbleachLevel(int level) {
        if(level <= 0) {
            return Blocks.CAULDRON.defaultBlockState();
        } else {
            if(level > MAX_STARBLEACH_LEVEL) level = MAX_STARBLEACH_LEVEL;
            return OperationStarcleaveBlocks.STARBLEACH_CAULDRON.defaultBlockState().setValue(LEVEL_7, level);
        }
    }

    public static void init() {
        CauldronInteraction.EMPTY.map().put(OperationStarcleaveItems.STARBLEACH_BOTTLE, getFillingBehaviour());

        StarbleachCauldronBlock.STARBLEACH_CAULDRON_BEHAVIOR.map().put(OperationStarcleaveItems.STARBLEACH_BOTTLE, getFillingBehaviour());
    }
}
