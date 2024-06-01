package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.entity.OperationStarcleaveDamageTypes;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;
import phanastrae.operation_starcleave.item.StarbleachCoating;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;
import phanastrae.operation_starcleave.recipe.ItemStarbleachingRecipe;
import phanastrae.operation_starcleave.recipe.OperationStarcleaveRecipeTypes;

import java.util.Optional;

import static net.minecraft.block.cauldron.CauldronBehavior.createMap;

public class StarbleachCauldronBlock extends AbstractCauldronBlock {
    public static final MapCodec<StarbleachCauldronBlock> CODEC = createCodec(StarbleachCauldronBlock::new);
    public static final int MAX_STARBLEACH_LEVEL = 7;
    public static final IntProperty LEVEL_7 = IntProperty.of("level", 1, MAX_STARBLEACH_LEVEL);
    public static CauldronBehavior.CauldronBehaviorMap STARBLEACH_CAULDRON_BEHAVIOR = createMap("operation_starcleave:starbleach");

    public static final float STARBLEACHING_FOOD_COST = 0.25F;

    @Override
    protected MapCodec<? extends AbstractCauldronBlock> getCodec() {
        return CODEC;
    }

    public StarbleachCauldronBlock(Settings settings) {
        super(settings, STARBLEACH_CAULDRON_BEHAVIOR);
        this.setDefaultState(this.stateManager.getDefaultState().with(LEVEL_7, 1));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LEVEL_7);
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return state.get(LEVEL_7);
    }

    @Override
    public boolean isFull(BlockState state) {
        return state.get(LEVEL_7) == MAX_STARBLEACH_LEVEL;
    }

    @Override
    protected boolean canBeFilledByDripstone(Fluid fluid) {
        return false;
    }

    @Override
    protected double getFluidHeight(BlockState state) {
        return (8.0 + state.get(LEVEL_7)) / 16.0;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if(world.isClient) {
            return;
        }

        if (this.isEntityTouchingFluid(state, pos, entity)) {
            if(entity instanceof ItemEntity itemEntity) {
                ItemStack itemStack = itemEntity.getStack();

                Entity owner = itemEntity.getOwner();
                PlayerEntity playerOwner = owner instanceof PlayerEntity ? (PlayerEntity)owner : null;
                Optional<ItemStack> optionalItemStack = attemptCraft(world, itemStack, pos, playerOwner, true);
                if(optionalItemStack.isPresent()) {
                    ItemStack outputStack = optionalItemStack.get();
                    ItemEntity newEntity = new ItemEntity(world,
                            pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                            outputStack);
                    world.spawnEntity(newEntity);

                    if (itemStack.isEmpty()) {
                        itemEntity.discard();
                    }

                    return;
                }
            }

            entity.damage(OperationStarcleaveDamageTypes.of(world, OperationStarcleaveDamageTypes.INTERNAL_STARBLEACHING), 0.25f * state.get(LEVEL_7));
            if(!entity.isAlive() && world instanceof ServerWorld serverWorld) {
                spawnParticles(serverWorld, pos);
            }
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);

        boolean creativeMode = player.getAbilities().creativeMode;
        Optional<ItemStack> optionalItemStack = attemptCraft(world, itemStack, pos, player, !creativeMode);
        if(optionalItemStack.isPresent()) {
            if(!world.isClient) {
                ItemStack outputStack = optionalItemStack.get();
                if(creativeMode) {
                    if (!player.getInventory().contains(outputStack)) {
                        player.getInventory().insertStack(outputStack);
                    }
                } else {
                    if (itemStack.isEmpty()) {
                        player.setStackInHand(hand, outputStack);
                    } else {
                        if (!player.getInventory().insertStack(outputStack)) {
                            player.dropItem(outputStack, false);
                        }
                    }
                }
            }

            return ActionResult.success(world.isClient);
        }

        CauldronBehavior cauldronBehavior = this.behaviorMap.map().get(itemStack.getItem());
        return cauldronBehavior.interact(state, world, pos, player, hand, itemStack);
    }

    // Attempt to craft using the input item
    // If the recipe is successful, returns the output stack. On the client this is just an empty stack
    // If the recipe fails, returns an empty Optional
    public Optional<ItemStack> attemptCraft(World world, ItemStack input, BlockPos pos, @Nullable PlayerEntity player, boolean decrement) {
        ItemStarbleachingRecipe recipe = getRecipe(world, input);
        if(recipe != null) {
            if(canEmptyCauldron(world, pos, recipe.getRequiredStarbleachToAttemptCraft())) {
                if(!world.isClient) {
                    craft(world, pos, recipe.getStarbleachCost(), !recipe.getIsFillingRecipe(), input, player, decrement);
                    return Optional.of(recipe.getOutputStack());
                } else {
                    return Optional.of(ItemStack.EMPTY);
                }
            }
        }

        if(StarbleachCoating.canAddStarbleach(input)) {
            if(canEmptyCauldron(world, pos, 1)) {
                if(!world.isClient) {
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

    public void craft(World world, BlockPos blockPos, float starbleachCost, boolean spawnParticles, ItemStack input, @Nullable PlayerEntity player, boolean decrement) {
        if(player != null) {
            incrementStats(player, input);
        }
        emptyCauldron(world, blockPos, ItemStarbleachingRecipe.getConsumedStarbleach(world.getRandom(), starbleachCost), spawnParticles);

        if(decrement) {
            input.decrement(1);
        }
    }

    @Nullable
    public static ItemStarbleachingRecipe getRecipe(World world, ItemStack stack) {
        Inventory inventory = new SimpleInventory(1);
        inventory.setStack(0, stack);
        Optional<RecipeEntry<ItemStarbleachingRecipe>> recipeEntryOptional = world.getRecipeManager().getFirstMatch(OperationStarcleaveRecipeTypes.ITEM_STARBLEACHING, inventory, world);
        return recipeEntryOptional.map(RecipeEntry::value).orElse(null);
    }

    public static void spawnParticles(ServerWorld world, BlockPos blockPos) {
        Vec3d pos = Vec3d.ofCenter(blockPos);
        world.spawnParticles(OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER,
                pos.getX(), pos.getY(), pos.getZ(),
                400,
                0.2, 0.4, 0.2,
                0.005);
    }

    private static CauldronBehavior getFillingBehaviour() {
        return (state, world, pos, player, hand, stack) ->
        {
            if (canFillCauldron(world, pos)) {
                if (!world.isClient) {
                    incrementStats(player, stack);
                    player.incrementStat(Stats.FILL_CAULDRON);
                    fillCauldron(world, pos);
                    ItemStack outputStack = Items.GLASS_BOTTLE.getDefaultStack();
                    player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, outputStack));
                }

                return ActionResult.success(world.isClient);
            } else {
                return ActionResult.PASS;
            }
        };
    }

    public static void incrementStats(PlayerEntity player, @Nullable ItemStack itemStack) {
        if(itemStack != null && !itemStack.isEmpty()) {
            player.incrementStat(Stats.USED.getOrCreateStat(itemStack.getItem()));
        }
    }

    public static void fillCauldron(World world, BlockPos blockPos) {
        BlockState state = world.getBlockState(blockPos);
        int currentStarbleachLevel = getStarbleachLevel(state);

        world.setBlockState(blockPos, getStateWithStarbleachLevel(currentStarbleachLevel + 1));

        world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(state));
        world.emitGameEvent(null, GameEvent.FLUID_PLACE, blockPos);

        world.playSound(null, blockPos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    public static void emptyCauldron(World world, BlockPos blockPos, int amount, boolean spawnParticles) {
        BlockState state = world.getBlockState(blockPos);
        int currentStarbleachLevel = getStarbleachLevel(state);

        world.setBlockState(blockPos, getStateWithStarbleachLevel(currentStarbleachLevel - amount));

        world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(state));
        world.emitGameEvent(null, GameEvent.FLUID_PICKUP, blockPos);

        if(spawnParticles) {
            world.playSound(null, blockPos, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0F, 1.5F);
        } else {
            world.playSound(null, blockPos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }

        if(spawnParticles) {
            if(world instanceof ServerWorld serverWorld) {
                spawnParticles(serverWorld, blockPos);
            }
        }
    }

    public static boolean canFillCauldron(World world, BlockPos blockPos) {
        BlockState blockState = world.getBlockState(blockPos);
        if(blockState.isOf(Blocks.CAULDRON) || blockState.isOf(OperationStarcleaveBlocks.STARBLEACH_CAULDRON)) {
            int currentStarbleachLevel = getStarbleachLevel(blockState);
            return currentStarbleachLevel != MAX_STARBLEACH_LEVEL;
        } else {
            return false;
        }
    }

    public static boolean canEmptyCauldron(World world, BlockPos blockPos, int amount) {
        BlockState blockState = world.getBlockState(blockPos);
        if(blockState.isOf(OperationStarcleaveBlocks.STARBLEACH_CAULDRON)) {
            int currentStarbleachLevel = getStarbleachLevel(blockState);

            return currentStarbleachLevel >= amount;
        } else {
            return false;
        }
    }

    public static int getStarbleachLevel(BlockState state) {
        if(state.isOf(OperationStarcleaveBlocks.STARBLEACH_CAULDRON)) {
            return state.get(LEVEL_7);
        } else {
            return 0;
        }
    }

    public static BlockState getStateWithStarbleachLevel(int level) {
        if(level <= 0) {
            return Blocks.CAULDRON.getDefaultState();
        } else {
            if(level > MAX_STARBLEACH_LEVEL) level = MAX_STARBLEACH_LEVEL;
            return OperationStarcleaveBlocks.STARBLEACH_CAULDRON.getDefaultState().with(LEVEL_7, level);
        }
    }

    public static void init() {
        CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR.map().put(OperationStarcleaveItems.STARBLEACH_BOTTLE, getFillingBehaviour());

        StarbleachCauldronBlock.STARBLEACH_CAULDRON_BEHAVIOR.map().put(OperationStarcleaveItems.STARBLEACH_BOTTLE, getFillingBehaviour());
    }
}
