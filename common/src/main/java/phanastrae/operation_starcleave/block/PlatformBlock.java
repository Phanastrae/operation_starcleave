package phanastrae.operation_starcleave.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;
import java.util.function.Function;

public class PlatformBlock extends SlabBlock {
    public static final MapCodec<PlatformBlock> CODEC = simpleCodec(PlatformBlock::new);
    public static final Map<Direction, VoxelShape> TOP_SIDES = createSideShapes(true);
    public static final Map<Direction, VoxelShape> BOTTOM_SIDES = createSideShapes(false);

    protected static Map<Direction, VoxelShape> createSideShapes(boolean isTop) {
        return Direction.stream().filter(d -> d.getAxis().isHorizontal()).collect(ImmutableMap.toImmutableMap(Function.identity(), d -> createSideShape(isTop, d)));
    }

    protected static VoxelShape createSideShape(boolean isTop, Direction direction) {
        Vec3i normal = direction.getNormal();
        int nx = normal.getX();
        int nz = normal.getZ();
        return Shapes.box(
                nx == 0 ? 0 : (7.5 + nx * 7.5) / 16.0,
                isTop ? 0.5 : 0.0,
                nz == 0 ? 0 : (7.5 + nz * 7.5) / 16.0,
                nx == 0 ? 1 : (8.5 + nx * 7.5) / 16.0,
                isTop ? 1.0 : 0.5,
                nz == 0 ? 1 : (8.5 + nz * 7.5) / 16.0
        );
    }

    @Override
    public MapCodec<? extends SlabBlock> codec() {
        return CODEC;
    }

    public PlatformBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    protected boolean skipRendering(BlockState state, BlockState adjacentState, Direction direction) {
        if (direction.getAxis().isHorizontal() && adjacentState.is(this)) {
            SlabType thisType = state.getValue(SlabBlock.TYPE);
            SlabType adjType = adjacentState.getValue(SlabBlock.TYPE);

            switch (thisType) {
                case TOP -> {
                    if (adjType != SlabType.BOTTOM) return true;
                }
                case BOTTOM -> {
                    if (adjType != SlabType.TOP) return true;
                }
                default -> {
                    if (adjType == SlabType.DOUBLE) return true;
                }
            }
        }

        return super.skipRendering(state, adjacentState, direction);
    }

    @Override
    protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter reader, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context.isDescending() && !context.isHoldingItem(this.asItem())) {
            return Shapes.empty();
        }

        return switch (state.getValue(SlabBlock.TYPE)) {
            case TOP -> getShapeForHalf(true, level, pos, context);
            case BOTTOM -> getShapeForHalf(false, level, pos, context);
            default -> Shapes.join(
                    getShapeForHalf(true, level, pos, context),
                    getShapeForHalf(false, level, pos, context),
                    BooleanOp.OR
            );
        };
    }

    protected VoxelShape getShapeForHalf(boolean isTop, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape original = isTop ? SlabBlock.TOP_AABB : SlabBlock.BOTTOM_AABB;
        if (context.isAbove(original, pos, true)) {
            return original;
        }

        Map<Direction, VoxelShape> map = isTop ? TOP_SIDES : BOTTOM_SIDES;

        VoxelShape shape = Shapes.empty();
        for (Direction direction : map.keySet()) {
            BlockPos adjPos = pos.offset(direction.getNormal());
            BlockState adjState = level.getBlockState(adjPos);

            if (!adjacentBlockIsMatchingPlatform(isTop, adjState)) {
                shape = Shapes.join(shape, map.get(direction), BooleanOp.OR);
            }
        }

        return shape;
    }

    protected boolean adjacentBlockIsMatchingPlatform(boolean isTop, BlockState adjState) {
        if (adjState.getBlock() instanceof PlatformBlock) {
            SlabType type = adjState.getValue(SlabBlock.TYPE);
            if (isTop) {
                return type != SlabType.BOTTOM;
            } else {
                return type != SlabType.TOP;
            }
        } else {
            return false;
        }
    }
}
