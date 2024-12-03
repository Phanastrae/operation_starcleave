package phanastrae.operation_starcleave.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import phanastrae.operation_starcleave.duck.EntityDuck;
import phanastrae.operation_starcleave.entity.OperationStarcleaveDamageTypes;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypeTags;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;
import phanastrae.operation_starcleave.services.XPlatInterface;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PhlogisticFireBlock extends BaseFireBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<PhlogisticFireBlock> CODEC = simpleCodec(PhlogisticFireBlock::new);
    public static final IntegerProperty AGE = BlockStateProperties.AGE_7;
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    public static final BooleanProperty UP = PipeBlock.UP;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final Map<Direction, BooleanProperty> DIRECTION_PROPERTIES = PipeBlock.PROPERTY_BY_DIRECTION
            .entrySet()
            .stream()
            .filter(entry -> entry.getKey() != Direction.DOWN)
            .collect(Util.toMap());
    private static final VoxelShape UP_SHAPE = Block.box(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape WEST_SHAPE = Block.box(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
    private static final VoxelShape EAST_SHAPE = Block.box(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape NORTH_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
    private static final VoxelShape SOUTH_SHAPE = Block.box(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
    private final Map<BlockState, VoxelShape> shapesByState;

    @Override
    public MapCodec<PhlogisticFireBlock> codec() {
        return CODEC;
    }

    public PhlogisticFireBlock(BlockBehaviour.Properties settings) {
        super(settings, 3.0F);
        this.registerDefaultState(
                this.stateDefinition
                        .any()
                        .setValue(AGE, 0)
                        .setValue(NORTH, false)
                        .setValue(EAST, false)
                        .setValue(SOUTH, false)
                        .setValue(WEST, false)
                        .setValue(UP, false)
                        .setValue(WATERLOGGED, false)
        );
        this.shapesByState = ImmutableMap.copyOf(
                this.stateDefinition
                        .getPossibleStates()
                        .stream()
                        .filter(state -> state.getValue(AGE) == 0 && !state.getValue(WATERLOGGED))
                        .collect(Collectors.toMap(Function.identity(), PhlogisticFireBlock::getShapeForState))
        );
    }

    private static VoxelShape getShapeForState(BlockState state) {
        VoxelShape voxelShape = Shapes.empty();
        if (state.getValue(UP)) voxelShape = UP_SHAPE;
        if (state.getValue(NORTH)) voxelShape = Shapes.or(voxelShape, NORTH_SHAPE);
        if (state.getValue(SOUTH)) voxelShape = Shapes.or(voxelShape, SOUTH_SHAPE);
        if (state.getValue(EAST)) voxelShape = Shapes.or(voxelShape, EAST_SHAPE);
        if (state.getValue(WEST)) voxelShape = Shapes.or(voxelShape, WEST_SHAPE);
        return voxelShape.isEmpty() ? DOWN_AABB : voxelShape;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, NORTH, EAST, SOUTH, WEST, UP, WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return this.shapesByState.get(state.setValue(AGE, 0).setValue(WATERLOGGED, false));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return this.areBlocksAroundValidSupports(world, pos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.getStateForPosition(ctx.getLevel(), ctx.getClickedPos());
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onPlace(state, world, pos, oldState, notify);
        int age = state.getValue(AGE);
        boolean waterlogged = state.getValue(WATERLOGGED);
        world.scheduleTick(pos, this, getFireTickDelay(world.random, age, waterlogged));
    }

    @Override
    public BlockState updateShape(
            BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos
    ) {
        boolean canPlaceAt = this.canSurvive(state, world, pos);
        if(canPlaceAt) {
            if (state.getValue(WATERLOGGED)) {
                world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
            }
            return this.getStateWithAge(world, pos, state.getValue(AGE));
        } else {
            return Blocks.AIR.defaultBlockState();
        }
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (!entity.getType().is(OperationStarcleaveEntityTypeTags.PHLOGISTIC_FIRE_IMMUNE)) {
            EntityDuck opsce = (EntityDuck)entity;
            opsce.operation_starcleave$setPhlogisticFireTicks(opsce.operation_starcleave$getPhlogisticFireTicks() + 1);
            if(opsce.operation_starcleave$getPhlogisticFireTicks() == 0) {
                opsce.operation_starcleave$setOnPhlogisticFireFor(8);
            }
        }

        entity.hurt(OperationStarcleaveDamageTypes.of(world, OperationStarcleaveDamageTypes.IN_PHLOGISTIC_FIRE), 3.0F);
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        int age = state.getValue(AGE);
        boolean waterlogged = state.getValue(WATERLOGGED);
        world.scheduleTick(pos, this, getFireTickDelay(world.random, age, waterlogged));
        if (world.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
            if (!state.canSurvive(world, pos)) {
                world.removeBlock(pos, false);
                return;
            }

            int newAge = Math.min(7, age + random.nextInt(4) / 3);
            if (age != newAge) {
                state = state.setValue(AGE, newAge);
                world.setBlock(pos, state, Block.UPDATE_INVISIBLE);
            }

            boolean worldInfiniburn = state.is(world.dimensionType().infiniburn());
            if (!worldInfiniburn) {
                BlockPos floorPos = pos.below();

                if (age > 2 && !this.areBlocksAroundFlammable(world, pos)) {
                    if (!world.getBlockState(floorPos).isFaceSturdy(world, floorPos, Direction.UP)) {
                        world.removeBlock(pos, false);
                        return;
                    }
                }

                if(random.nextInt(age + 7) > 9) {
                    int nearbyFire = 0;
                    BlockPos.MutableBlockPos mutable = pos.mutable();
                    for(int x = -1; x <= 1; x++) {
                        for(int z = -1; z <= 1; z++) {
                            for(int y = -1; y <= 1; y++) {
                                if(x == 0 && y == 0 && z == 0) continue;
                                mutable.setWithOffset(pos, x, y, z);

                                if(world.getBlockState(mutable).is(this.asBlock())) {
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

    protected void spread(Level world, BlockPos pos, RandomSource random, int age) {
        for(Direction direction : UPDATE_SHAPE_ORDER) {
            int spreadFactor = direction.getAxis() == Direction.Axis.Y ? 125 : 150;
            this.trySpreadingFire(world, pos.relative(direction), spreadFactor, random, age);
        }
    }

    protected void burn(Level world, BlockPos pos, RandomSource random, int age) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for(int x = -1; x <= 1; ++x) {
            for(int z = -1; z <= 1; ++z) {
                for(int y = -1; y <= 4; ++y) {
                    if(x == 0 && y == 0 && z == 0) continue;
                    mutable.setWithOffset(pos, x, y, z);

                    int o = 100;
                    if (y > 1) {
                        o += (y - 1) * 100;
                    }

                    int burnChance = this.getBurnChance(world, mutable);
                    if (burnChance > 0) {
                        int modifiedBurnChance = (burnChance + 60 + world.getDifficulty().getId() * 7) / (7 + age * age);

                        if (modifiedBurnChance > 0 && random.nextInt(o) <= modifiedBurnChance) {
                            int newAge = Math.min(7, age + random.nextInt(4) / 3);
                            world.setBlock(mutable, withWaterlogged(this.getStateWithAge(world, mutable, newAge), shouldWaterlog(world, mutable)), Block.UPDATE_ALL);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (random.nextInt(24) == 0) {
            world.playLocalSound(
                    (double) pos.getX() + 0.5,
                    (double) pos.getY() + 0.5,
                    (double) pos.getZ() + 0.5,
                    SoundEvents.FIRE_AMBIENT,
                    SoundSource.BLOCKS,
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
            for(Direction direction : UPDATE_SHAPE_ORDER) {
                if(this.isValidSupport(world, pos, direction)) {
                    double dx = random.nextDouble();
                    double dy = random.nextDouble();
                    double dz = random.nextDouble();
                    if(direction.getAxis() == Direction.Axis.X) {
                        dx = 0.5 + direction.getStepX() * (0.1 * dx + 0.4);
                    }
                    if(direction.getAxis() == Direction.Axis.Y) {
                        dy = 0.5 + direction.getStepY() * (0.1 * dy + 0.4);
                    }
                    if(direction.getAxis() == Direction.Axis.Z) {
                        dz = 0.5 + direction.getStepZ() * (0.1 * dz + 0.4);
                    }

                    world.addParticle(OperationStarcleaveParticleTypes.LARGE_GLIMMER_SMOKE,
                            pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz,
                            0.0, 0.0, 0.0);
                }
            }
        }
    }

    @Override
    protected boolean canBurn(BlockState state) {
        return this.getBurnChance(state) > 0;
    }

    protected boolean isValidSupport(BlockGetter world, BlockPos blockPos, Direction direction) {
        BlockPos groundPos = blockPos.relative(direction);
        BlockState groundState = world.getBlockState(groundPos);
        return this.canBurn(groundState) || groundState.isFaceSturdy(world, blockPos, direction.getOpposite());
    }

    protected boolean areBlocksAroundFlammable(BlockGetter world, BlockPos pos) {
        for(Direction direction : Direction.values()) {
            if (this.canBurn(world.getBlockState(pos.relative(direction)))) {
                return true;
            }
        }

        return false;
    }

    protected boolean areBlocksAroundValidSupports(BlockGetter world, BlockPos pos) {
        for(Direction direction : Direction.values()) {
            if (this.isValidSupport(world, pos, direction)) {
                return true;
            }
        }

        return false;
    }

    private int getBurnChance(LevelReader world, BlockPos pos) {
        if (!(world.isEmptyBlock(pos) || world.isWaterAt(pos))) {
            return 0;
        } else {
            int maxBurnChance = 0;

            for(Direction direction : Direction.values()) {
                BlockState blockState = world.getBlockState(pos.relative(direction));
                maxBurnChance = Math.max(this.getBurnChance(blockState), maxBurnChance);
            }

            return maxBurnChance;
        }
    }

    private int getSpreadChance(BlockState state) {
        return XPlatInterface.INSTANCE.getFireSpreadChance(state);
    }

    private int getBurnChance(BlockState state) {
        return XPlatInterface.INSTANCE.getFireBurnChance(state);
    }

    private void trySpreadingFire(Level world, BlockPos pos, int spreadFactor, RandomSource random, int currentAge) {
        int spreadChance = this.getSpreadChance(world.getBlockState(pos));
        if (random.nextInt(spreadFactor) < spreadChance) {
            BlockState blockState = world.getBlockState(pos);
            if (random.nextInt(currentAge + 5) < 13) {
                int newAge = Math.min(7, currentAge + random.nextInt(5) / 2);
                world.setBlock(pos, withWaterlogged(this.getStateWithAge(world, pos, newAge), shouldWaterlog(world, pos)), Block.UPDATE_ALL);
            } else {
                world.setBlock(pos, withWaterlogged(Blocks.AIR.defaultBlockState(), shouldWaterlog(world, pos)), Block.UPDATE_ALL);
            }

            if (blockState.getBlock() instanceof TntBlock) {
                TntBlock.explode(world, pos);
            }
        }
    }

    protected BlockState withWaterlogged(BlockState state, boolean waterlogged) {
        if(state.hasProperty(WATERLOGGED)) {
            return state.setValue(WATERLOGGED, waterlogged);
        } else if(state.isAir() && waterlogged) {
            return Blocks.WATER.defaultBlockState();
        } else {
            return state;
        }
    }

    protected boolean shouldWaterlog(BlockGetter world, BlockPos pos) {
        int adjWater = 0;
        for(Direction direction : UPDATE_SHAPE_ORDER) {
            if(direction.getAxis() != Direction.Axis.Y) {
                boolean water = world.getFluidState(pos.relative(direction)).is(Fluids.WATER);
                if(water) adjWater++;

                if(adjWater >= 2) {
                    return true;
                }
            }
        }

        return false;
    }

    protected BlockState getStateForPosition(BlockGetter world, BlockPos pos) {
        boolean isWet = world.getFluidState(pos).is(FluidTags.WATER);

        BlockState newState = this.defaultBlockState();
        if (!this.isValidSupport(world, pos, Direction.DOWN)) {
            for(Direction direction : Direction.values()) {
                BooleanProperty booleanProperty = DIRECTION_PROPERTIES.get(direction);
                if (booleanProperty != null) {
                    newState = newState.setValue(booleanProperty, this.isValidSupport(world, pos, direction));
                }
            }
        }

        if(isWet) {
            newState = newState.setValue(WATERLOGGED, true);
        }
        return newState;
    }

    private BlockState getStateWithAge(LevelAccessor world, BlockPos pos, int age) {
        BlockState blockState = getStateForPosition(world, pos);
        return blockState.is(OperationStarcleaveBlocks.PHLOGISTIC_FIRE) ? blockState.setValue(AGE, age) : blockState;
    }

    public static BlockState getState(BlockGetter world, BlockPos pos) {
        return ((PhlogisticFireBlock)OperationStarcleaveBlocks.PHLOGISTIC_FIRE).getStateForPosition(world, pos);
    }

    public static boolean canPlaceAt(Level world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (!(blockState.isAir() || world.isWaterAt(pos))) {
            return false;
        } else {
            return getState(world, pos).canSurvive(world, pos);
        }
    }

    private static int getFireTickDelay(RandomSource random, int age, boolean waterlogged) {
        if(waterlogged) {
            return 2 + age * 2 + random.nextInt(2 + age * 2) + (age == 7 ? 140 : 0);
        } else {
            return 5 + age * age / 3 + random.nextInt(4 + age * age / 3) + (age == 7 ? 100 : 0);
        }
    }
}
