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
import phanastrae.operation_starcleave.block.tag.OperationStarcleaveBlockTags;
import phanastrae.operation_starcleave.entity.OperationStarcleaveDamageTypes;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityAttachment;
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
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.shapesByState.get(state.setValue(AGE, 0).setValue(WATERLOGGED, false));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return this.areBlocksAroundValidSupports(level, pos);
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
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean notify) {
        super.onPlace(state, level, pos, oldState, notify);
        int age = state.getValue(AGE);
        boolean waterlogged = state.getValue(WATERLOGGED);
        level.scheduleTick(pos, this, getFireTickDelay(level.random, age, waterlogged));
    }

    @Override
    public BlockState updateShape(
            BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos
    ) {
        boolean canPlaceAt = this.canSurvive(state, level, pos);
        if(canPlaceAt) {
            if (state.getValue(WATERLOGGED)) {
                level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
            }
            return getStateWithAge(level, pos, state.getValue(AGE));
        } else {
            return Blocks.AIR.defaultBlockState();
        }
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!entity.getType().is(OperationStarcleaveEntityTypeTags.PHLOGISTIC_FIRE_IMMUNE)) {
            OperationStarcleaveEntityAttachment osea = OperationStarcleaveEntityAttachment.fromEntity(entity);
            osea.setPhlogisticFireTicks(osea.getPhlogisticFireTicks() + 1);
            if(osea.getPhlogisticFireTicks() == 0) {
                osea.setOnPhlogisticFireFor(8);
            }
        }

        entity.hurt(OperationStarcleaveDamageTypes.source(level, OperationStarcleaveDamageTypes.IN_PHLOGISTIC_FIRE), 3.0F);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int age = state.getValue(AGE);
        boolean waterlogged = state.getValue(WATERLOGGED);
        level.scheduleTick(pos, this, getFireTickDelay(level.random, age, waterlogged));
        if (level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
            if (!state.canSurvive(level, pos)) {
                level.removeBlock(pos, false);
                return;
            }

            int newAge = Math.min(7, age + random.nextInt(4) / 3);
            if (age != newAge) {
                state = state.setValue(AGE, newAge);
                level.setBlock(pos, state, Block.UPDATE_INVISIBLE);
            }

            boolean worldInfiniburn = state.is(level.dimensionType().infiniburn());
            if (!worldInfiniburn) {
                BlockPos floorPos = pos.below();

                if (age > 2 && !this.areBlocksAroundFlammable(level, pos)) {
                    if (!level.getBlockState(floorPos).isFaceSturdy(level, floorPos, Direction.UP)) {
                        level.removeBlock(pos, false);
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

                                if(level.getBlockState(mutable).is(this.asBlock())) {
                                    nearbyFire++;
                                }
                            }
                        }
                    }
                    if(nearbyFire > (8 - age)) {
                        level.removeBlock(pos, false);
                    }
                }
            }

            spread(level, pos, random, age);
            burn(level, pos, random, age);
        }
    }

    protected void spread(Level level, BlockPos pos, RandomSource random, int age) {
        for(Direction direction : UPDATE_SHAPE_ORDER) {
            int spreadFactor = direction.getAxis() == Direction.Axis.Y ? 125 : 150;
            this.trySpreadingFire(level, pos.relative(direction), direction.getOpposite(), spreadFactor, random, age);
        }
    }

    protected void burn(Level level, BlockPos pos, RandomSource random, int age) {
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

                    int burnChance = this.getBurnChance(level, mutable);
                    if (burnChance > 0) {
                        int modifiedBurnChance = (burnChance + 60 + level.getDifficulty().getId() * 7) / (7 + age * age);

                        if (modifiedBurnChance > 0 && random.nextInt(o) <= modifiedBurnChance) {
                            int newAge;
                            newAge = Math.min(7, age + random.nextInt(4) / 3);
                            /*
                            if(isHyperflammable(level.getBlockState(mutable))) {
                                newAge = Math.min(newAge, 2);
                            }
                            */
                            level.setBlock(mutable, withWaterlogged(getStateWithAge(level, mutable, newAge), shouldWaterlog(level, mutable)), Block.UPDATE_ALL);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(24) == 0) {
            level.playLocalSound(
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

        if(this.isValidSupport(level, pos, Direction.DOWN)) {
            for(int i = 0; i < 2; ++i) {
                double x = pos.getX() + random.nextDouble();
                double y = pos.getY() + random.nextDouble() * 0.5 + 0.5;
                double z = pos.getZ() + random.nextDouble();
                level.addParticle(OperationStarcleaveParticleTypes.LARGE_GLIMMER_SMOKE, x, y, z, 0.0, 0.0, 0.0);
            }
        } else {
            for(Direction direction : UPDATE_SHAPE_ORDER) {
                if(this.isValidSupport(level, pos, direction)) {
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

                    level.addParticle(OperationStarcleaveParticleTypes.LARGE_GLIMMER_SMOKE,
                            pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz,
                            0.0, 0.0, 0.0);
                }
            }
        }
    }

    public static boolean isHyperflammable(BlockState state) {
        return state.is(OperationStarcleaveBlockTags.PHLOGISTIC_HYPERFLAMMABLES);
    }

    @Override
    protected boolean canBurn(BlockState state) {
        if(isHyperflammable(state)) {
            return true;
        }
        return XPlatInterface.INSTANCE.canBurn(state);
    }

    protected boolean isValidSupport(BlockGetter level, BlockPos blockPos, Direction direction) {
        BlockPos groundPos = blockPos.relative(direction);
        BlockState groundState = level.getBlockState(groundPos);
        return this.canBurn(groundState) || groundState.isFaceSturdy(level, blockPos, direction.getOpposite());
    }

    protected boolean areBlocksAroundFlammable(BlockGetter level, BlockPos pos) {
        for(Direction direction : Direction.values()) {
            if (this.canBurn(level.getBlockState(pos.relative(direction)))) {
                return true;
            }
        }

        return false;
    }

    protected boolean areBlocksAroundValidSupports(BlockGetter level, BlockPos pos) {
        for(Direction direction : Direction.values()) {
            if (this.isValidSupport(level, pos, direction)) {
                return true;
            }
        }

        return false;
    }

    private int getBurnChance(LevelReader level, BlockPos pos) {
        if (!(level.isEmptyBlock(pos) || level.isWaterAt(pos))) {
            return 0;
        } else {
            int maxBurnChance = 0;

            for(Direction direction : Direction.values()) {
                BlockPos adjPos = pos.relative(direction);
                BlockState blockState = level.getBlockState(adjPos);
                maxBurnChance = Math.max(this.getBurnChance(blockState, level, adjPos, direction.getOpposite()), maxBurnChance);
            }

            return maxBurnChance;
        }
    }

    private int getSpreadChance(BlockState state, BlockGetter blockGetter, BlockPos blockPos, Direction face) {
        if(isHyperflammable(state)) {
            return 100;
        }
        return XPlatInterface.INSTANCE.getFireSpreadChance(state, blockGetter, blockPos, face);
    }

    private int getBurnChance(BlockState state, BlockGetter blockGetter, BlockPos blockPos, Direction face) {
        /*
        if(isHyperflammable(state)) {
            return 100;
        }
        */
        return XPlatInterface.INSTANCE.getFireBurnChance(state, blockGetter, blockPos, face);
    }

    private void trySpreadingFire(Level level, BlockPos pos, Direction originDirection, int spreadFactor, RandomSource random, int currentAge) {
        int spreadChance = this.getSpreadChance(level.getBlockState(pos), level, pos, originDirection);
        if (random.nextInt(spreadFactor) < spreadChance) {
            BlockState blockState = level.getBlockState(pos);
            if (random.nextInt(currentAge + 5) < 13) {
                int newAge;
                newAge = Math.min(7, currentAge + random.nextInt(5) / 2);
                if(isHyperflammable(blockState)) {
                    newAge = Math.min(newAge, 2);
                }
                level.setBlock(pos, withWaterlogged(getStateWithAge(level, pos, newAge), shouldWaterlog(level, pos)), Block.UPDATE_ALL);
            } else {
                level.setBlock(pos, withWaterlogged(Blocks.AIR.defaultBlockState(), shouldWaterlog(level, pos)), Block.UPDATE_ALL);
            }

            if (blockState.getBlock() instanceof TntBlock) {
                TntBlock.explode(level, pos);
            }

            if(blockState.is(OperationStarcleaveBlocks.NUCLEOSYNTHESEED)) {
                NucleosyntheseedBlock.detonate(level, pos, currentAge);
            }
        }
    }

    protected static BlockState withWaterlogged(BlockState state, boolean waterlogged) {
        if(state.hasProperty(WATERLOGGED)) {
            return state.setValue(WATERLOGGED, waterlogged);
        } else if(state.isAir() && waterlogged) {
            return Blocks.WATER.defaultBlockState();
        } else {
            return state;
        }
    }

    protected static boolean shouldWaterlog(BlockGetter level, BlockPos pos) {
        int adjWater = 0;
        for(Direction direction : UPDATE_SHAPE_ORDER) {
            if(direction.getAxis() != Direction.Axis.Y) {
                boolean water = level.getFluidState(pos.relative(direction)).is(Fluids.WATER);
                if(water) adjWater++;

                if(adjWater >= 2) {
                    return true;
                }
            }
        }

        return false;
    }

    protected BlockState getStateForPosition(BlockGetter level, BlockPos pos) {
        boolean isWet = level.getFluidState(pos).is(FluidTags.WATER);

        BlockState newState = this.defaultBlockState();
        if (!this.isValidSupport(level, pos, Direction.DOWN)) {
            for(Direction direction : Direction.values()) {
                BooleanProperty booleanProperty = DIRECTION_PROPERTIES.get(direction);
                if (booleanProperty != null) {
                    newState = newState.setValue(booleanProperty, this.isValidSupport(level, pos, direction));
                }
            }
        }

        if(isWet) {
            newState = newState.setValue(WATERLOGGED, true);
        }
        return newState;
    }

    public static BlockState getStateWithAge(LevelAccessor level, BlockPos pos, int age) {
        BlockState blockState = getState(level, pos);
        return blockState.is(OperationStarcleaveBlocks.PHLOGISTIC_FIRE) ? blockState.setValue(AGE, age) : blockState;
    }

    public static BlockState getState(BlockGetter level, BlockPos pos) {
        return ((PhlogisticFireBlock)OperationStarcleaveBlocks.PHLOGISTIC_FIRE).getStateForPosition(level, pos);
    }

    public static boolean canPlaceAt(Level level, BlockPos pos) {
        BlockState blockState = level.getBlockState(pos);
        if (!(blockState.isAir() || level.isWaterAt(pos))) {
            return false;
        } else {
            return getState(level, pos).canSurvive(level, pos);
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
