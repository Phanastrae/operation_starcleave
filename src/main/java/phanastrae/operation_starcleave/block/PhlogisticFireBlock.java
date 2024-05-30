package phanastrae.operation_starcleave.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.*;
import phanastrae.operation_starcleave.entity.OperationStarcleaveDamageTypes;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntity;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypeTags;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PhlogisticFireBlock extends AbstractFireBlock implements Waterloggable {
    public static final MapCodec<PhlogisticFireBlock> CODEC = createCodec(PhlogisticFireBlock::new);
    public static final IntProperty AGE = Properties.AGE_7;
    public static final BooleanProperty NORTH = ConnectingBlock.NORTH;
    public static final BooleanProperty EAST = ConnectingBlock.EAST;
    public static final BooleanProperty SOUTH = ConnectingBlock.SOUTH;
    public static final BooleanProperty WEST = ConnectingBlock.WEST;
    public static final BooleanProperty UP = ConnectingBlock.UP;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private static final Map<Direction, BooleanProperty> DIRECTION_PROPERTIES = ConnectingBlock.FACING_PROPERTIES
            .entrySet()
            .stream()
            .filter(entry -> entry.getKey() != Direction.DOWN)
            .collect(Util.toMap());
    private static final VoxelShape UP_SHAPE = Block.createCuboidShape(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
    private static final VoxelShape EAST_SHAPE = Block.createCuboidShape(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
    private static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
    private final Map<BlockState, VoxelShape> shapesByState;

    @Override
    public MapCodec<PhlogisticFireBlock> getCodec() {
        return CODEC;
    }

    public PhlogisticFireBlock(AbstractBlock.Settings settings) {
        super(settings, 3.0F);
        this.setDefaultState(
                this.stateManager
                        .getDefaultState()
                        .with(AGE, 0)
                        .with(NORTH, false)
                        .with(EAST, false)
                        .with(SOUTH, false)
                        .with(WEST, false)
                        .with(UP, false)
                        .with(WATERLOGGED, false)
        );
        this.shapesByState = ImmutableMap.copyOf(
                this.stateManager
                        .getStates()
                        .stream()
                        .filter(state -> state.get(AGE) == 0 && !state.get(WATERLOGGED))
                        .collect(Collectors.toMap(Function.identity(), PhlogisticFireBlock::getShapeForState))
        );
    }

    private static VoxelShape getShapeForState(BlockState state) {
        VoxelShape voxelShape = VoxelShapes.empty();
        if (state.get(UP)) voxelShape = UP_SHAPE;
        if (state.get(NORTH)) voxelShape = VoxelShapes.union(voxelShape, NORTH_SHAPE);
        if (state.get(SOUTH)) voxelShape = VoxelShapes.union(voxelShape, SOUTH_SHAPE);
        if (state.get(EAST)) voxelShape = VoxelShapes.union(voxelShape, EAST_SHAPE);
        if (state.get(WEST)) voxelShape = VoxelShapes.union(voxelShape, WEST_SHAPE);
        return voxelShape.isEmpty() ? BASE_SHAPE : voxelShape;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE, NORTH, EAST, SOUTH, WEST, UP, WATERLOGGED);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.shapesByState.get(state.with(AGE, 0).with(WATERLOGGED, false));
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return this.areBlocksAroundValidSupports(world, pos);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getStateForPosition(ctx.getWorld(), ctx.getBlockPos());
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        int age = state.get(AGE);
        boolean waterlogged = state.get(WATERLOGGED);
        world.scheduleBlockTick(pos, this, getFireTickDelay(world.random, age, waterlogged));
    }

    @Override
    public BlockState getStateForNeighborUpdate(
            BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
    ) {
        boolean canPlaceAt = this.canPlaceAt(state, world, pos);
        if(canPlaceAt) {
            if (state.get(WATERLOGGED)) {
                world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
            }
            return this.getStateWithAge(world, pos, state.get(AGE));
        } else {
            return Blocks.AIR.getDefaultState();
        }
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!entity.getType().isIn(OperationStarcleaveEntityTypeTags.PHLOGISTIC_FIRE_IMMUNE)) {
            OperationStarcleaveEntity opsce = (OperationStarcleaveEntity)entity;
            opsce.operation_starcleave$setPhlogisticFireTicks(opsce.operation_starcleave$getPhlogisticFireTicks() + 1);
            if(opsce.operation_starcleave$getPhlogisticFireTicks() == 0) {
                opsce.operation_starcleave$setOnPhlogisticFireFor(8);
            }
        }

        entity.damage(OperationStarcleaveDamageTypes.of(world, OperationStarcleaveDamageTypes.IN_PHLOGISTIC_FIRE), 3.0F);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int age = state.get(AGE);
        boolean waterlogged = state.get(WATERLOGGED);
        world.scheduleBlockTick(pos, this, getFireTickDelay(world.random, age, waterlogged));
        if (world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
            if (!state.canPlaceAt(world, pos)) {
                world.removeBlock(pos, false);
                return;
            }

            int newAge = Math.min(7, age + random.nextInt(4) / 3);
            if (age != newAge) {
                state = state.with(AGE, newAge);
                world.setBlockState(pos, state, Block.NO_REDRAW);
            }

            boolean worldInfiniburn = state.isIn(world.getDimension().infiniburn());
            if (!worldInfiniburn) {
                BlockPos floorPos = pos.down();

                if (age > 2 && !this.areBlocksAroundFlammable(world, pos)) {
                    if (!world.getBlockState(floorPos).isSideSolidFullSquare(world, floorPos, Direction.UP)) {
                        world.removeBlock(pos, false);
                        return;
                    }
                }

                if(random.nextInt(age + 7) > 9) {
                    int nearbyFire = 0;
                    BlockPos.Mutable mutable = pos.mutableCopy();
                    for(int x = -1; x <= 1; x++) {
                        for(int z = -1; z <= 1; z++) {
                            for(int y = -1; y <= 1; y++) {
                                if(x == 0 && y == 0 && z == 0) continue;
                                mutable.set(pos, x, y, z);

                                if(world.getBlockState(mutable).isOf(this.asBlock())) {
                                    nearbyFire++;
                                }
                            }
                        }
                    }
                    if(nearbyFire > (8 - age)) {
                        world.removeBlock(pos, false);
                    }
                }
            }

            spread(world, pos, random, age);
            burn(world, pos, random, age);
        }
    }

    protected void spread(World world, BlockPos pos, Random random, int age) {
        for(Direction direction : DIRECTIONS) {
            int spreadFactor = direction.getAxis() == Direction.Axis.Y ? 125 : 150;
            this.trySpreadingFire(world, pos.offset(direction), spreadFactor, random, age);
        }
    }

    protected void burn(World world, BlockPos pos, Random random, int age) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for(int x = -1; x <= 1; ++x) {
            for(int z = -1; z <= 1; ++z) {
                for(int y = -1; y <= 4; ++y) {
                    if(x == 0 && y == 0 && z == 0) continue;
                    mutable.set(pos, x, y, z);

                    int o = 100;
                    if (y > 1) {
                        o += (y - 1) * 100;
                    }

                    int burnChance = this.getBurnChance(world, mutable);
                    if (burnChance > 0) {
                        int modifiedBurnChance = (burnChance + 60 + world.getDifficulty().getId() * 7) / (7 + age * age);

                        if (modifiedBurnChance > 0 && random.nextInt(o) <= modifiedBurnChance) {
                            int newAge = Math.min(7, age + random.nextInt(4) / 3);
                            world.setBlockState(mutable, withWaterlogged(this.getStateWithAge(world, mutable, newAge), shouldWaterlog(world, mutable)), Block.NOTIFY_ALL);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (random.nextInt(24) == 0) {
            world.playSound(
                    (double) pos.getX() + 0.5,
                    (double) pos.getY() + 0.5,
                    (double) pos.getZ() + 0.5,
                    SoundEvents.BLOCK_FIRE_AMBIENT,
                    SoundCategory.BLOCKS,
                    1.0F + random.nextFloat(),
                    random.nextFloat() * 0.7F + 0.3F,
                    false
            );
        }

        if(this.isValidSupport(world, pos, Direction.DOWN)) {
            for(int i = 0; i < 2; ++i) {
                double x = pos.getX() + random.nextDouble();
                double y = pos.getY() + random.nextDouble() * 0.5 + 0.5;
                double z = pos.getZ() + random.nextDouble();
                world.addParticle(OperationStarcleaveParticleTypes.LARGE_GLIMMER_SMOKE, x, y, z, 0.0, 0.0, 0.0);
            }
        } else {
            for(Direction direction : DIRECTIONS) {
                if(this.isValidSupport(world, pos, direction)) {
                    double dx = random.nextDouble();
                    double dy = random.nextDouble();
                    double dz = random.nextDouble();
                    if(direction.getAxis() == Direction.Axis.X) {
                        dx = 0.5 + direction.getOffsetX() * (0.1 * dx + 0.4);
                    }
                    if(direction.getAxis() == Direction.Axis.Y) {
                        dy = 0.5 + direction.getOffsetY() * (0.1 * dy + 0.4);
                    }
                    if(direction.getAxis() == Direction.Axis.Z) {
                        dz = 0.5 + direction.getOffsetZ() * (0.1 * dz + 0.4);
                    }

                    world.addParticle(OperationStarcleaveParticleTypes.LARGE_GLIMMER_SMOKE,
                            pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz,
                            0.0, 0.0, 0.0);
                }
            }
        }
    }

    @Override
    protected boolean isFlammable(BlockState state) {
        return this.getBurnChance(state) > 0;
    }

    protected boolean isValidSupport(BlockView world, BlockPos blockPos, Direction direction) {
        BlockPos groundPos = blockPos.offset(direction);
        BlockState groundState = world.getBlockState(groundPos);
        return this.isFlammable(groundState) || groundState.isSideSolidFullSquare(world, blockPos, direction.getOpposite());
    }

    protected boolean areBlocksAroundFlammable(BlockView world, BlockPos pos) {
        for(Direction direction : Direction.values()) {
            if (this.isFlammable(world.getBlockState(pos.offset(direction)))) {
                return true;
            }
        }

        return false;
    }

    protected boolean areBlocksAroundValidSupports(BlockView world, BlockPos pos) {
        for(Direction direction : Direction.values()) {
            if (this.isValidSupport(world, pos, direction)) {
                return true;
            }
        }

        return false;
    }

    private int getBurnChance(WorldView world, BlockPos pos) {
        if (!(world.isAir(pos) || world.isWater(pos))) {
            return 0;
        } else {
            int maxBurnChance = 0;

            for(Direction direction : Direction.values()) {
                BlockState blockState = world.getBlockState(pos.offset(direction));
                maxBurnChance = Math.max(this.getBurnChance(blockState), maxBurnChance);
            }

            return maxBurnChance;
        }
    }

    private int getSpreadChance(BlockState state) {
        return FlammableBlockRegistry.getDefaultInstance().get(state.getBlock()).getSpreadChance();
    }

    private int getBurnChance(BlockState state) {
        return FlammableBlockRegistry.getDefaultInstance().get(state.getBlock()).getBurnChance();
    }

    private void trySpreadingFire(World world, BlockPos pos, int spreadFactor, Random random, int currentAge) {
        int spreadChance = this.getSpreadChance(world.getBlockState(pos));
        if (random.nextInt(spreadFactor) < spreadChance) {
            BlockState blockState = world.getBlockState(pos);
            if (random.nextInt(currentAge + 5) < 13) {
                int newAge = Math.min(7, currentAge + random.nextInt(5) / 2);
                world.setBlockState(pos, withWaterlogged(this.getStateWithAge(world, pos, newAge), shouldWaterlog(world, pos)), Block.NOTIFY_ALL);
            } else {
                world.setBlockState(pos, withWaterlogged(Blocks.AIR.getDefaultState(), shouldWaterlog(world, pos)), Block.NOTIFY_ALL);
            }

            if (blockState.getBlock() instanceof TntBlock) {
                TntBlock.primeTnt(world, pos);
            }
        }
    }

    protected BlockState withWaterlogged(BlockState state, boolean waterlogged) {
        if(state.contains(WATERLOGGED)) {
            return state.with(WATERLOGGED, waterlogged);
        } else if(state.isAir() && waterlogged) {
            return Blocks.WATER.getDefaultState();
        } else {
            return state;
        }
    }

    protected boolean shouldWaterlog(BlockView world, BlockPos pos) {
        int adjWater = 0;
        for(Direction direction : DIRECTIONS) {
            if(direction.getAxis() != Direction.Axis.Y) {
                boolean water = world.getFluidState(pos.offset(direction)).isOf(Fluids.WATER);
                if(water) adjWater++;

                if(adjWater >= 2) {
                    return true;
                }
            }
        }

        return false;
    }

    protected BlockState getStateForPosition(BlockView world, BlockPos pos) {
        boolean isWet = world.getFluidState(pos).isIn(FluidTags.WATER);

        BlockState newState = this.getDefaultState();
        if (!this.isValidSupport(world, pos, Direction.DOWN)) {
            for(Direction direction : Direction.values()) {
                BooleanProperty booleanProperty = DIRECTION_PROPERTIES.get(direction);
                if (booleanProperty != null) {
                    newState = newState.with(booleanProperty, this.isValidSupport(world, pos, direction));
                }
            }
        }

        if(isWet) {
            newState = newState.with(WATERLOGGED, true);
        }
        return newState;
    }

    private BlockState getStateWithAge(WorldAccess world, BlockPos pos, int age) {
        BlockState blockState = getStateForPosition(world, pos);
        return blockState.isOf(OperationStarcleaveBlocks.PHLOGISTIC_FIRE) ? blockState.with(AGE, age) : blockState;
    }

    public static BlockState getState(BlockView world, BlockPos pos) {
        return ((PhlogisticFireBlock)OperationStarcleaveBlocks.PHLOGISTIC_FIRE).getStateForPosition(world, pos);
    }

    public static boolean canPlaceAt(World world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (!(blockState.isAir() || world.isWater(pos))) {
            return false;
        } else {
            return getState(world, pos).canPlaceAt(world, pos);
        }
    }

    private static int getFireTickDelay(Random random, int age, boolean waterlogged) {
        if(waterlogged) {
            return 2 + age * 2 + random.nextInt(2 + age * 2) + (age == 7 ? 140 : 0);
        } else {
            return 5 + age * age / 3 + random.nextInt(4 + age * age / 3) + (age == 7 ? 100 : 0);
        }
    }
}
