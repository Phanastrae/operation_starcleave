package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import phanastrae.operation_starcleave.block.tag.OperationStarcleaveBlockTags;
import phanastrae.operation_starcleave.entity.OperationStarcleaveDamageTypes;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityAttachment;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;
import phanastrae.operation_starcleave.world.OperationStarcleaveGameRules;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class NucleosyntheseedBlock extends Block implements BonemealableBlock {
    public static final MapCodec<NucleosyntheseedBlock> CODEC = simpleCodec(NucleosyntheseedBlock::new);
    public static final int MAX_AGE = 15;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_15;
    public static final BooleanProperty GROWS_DOWN = BooleanProperty.create("grows_down");

    private final IntProvider xpRange;

    @Override
    protected MapCodec<? extends NucleosyntheseedBlock> codec() {
        return CODEC;
    }

    public NucleosyntheseedBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.defaultBlockState()
                        .setValue(AGE, 0)
                        .setValue(GROWS_DOWN, true)
        );

        this.xpRange = UniformInt.of(2, 5);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, GROWS_DOWN);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        level.scheduleTick(pos, this, 30 + level.getRandom().nextInt(120));
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        trySpread(state, level, pos, random, false);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        trySpread(state, level, pos, random, false);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.getAbilities().mayBuild) {
            if (stack.is(OperationStarcleaveItems.PHLOGISTON_SAC)) {
                detonate(level, pos, 0);

                Item item = stack.getItem();
                stack.consume(1, player);
                player.awardStat(Stats.ITEM_USED.get(item));

                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        if (!level.isClientSide) {
            BlockPos pos = hit.getBlockPos();
            if (OperationStarcleaveEntityAttachment.fromEntity(projectile).isOnPhlogisticFire() && projectile.mayInteract(level, pos)) {
                detonate(level, pos, 0);
            }
        }
    }

    @Override
    protected void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack stack, boolean dropExperience) {
        super.spawnAfterBreak(state, level, pos, stack, dropExperience);
        if (dropExperience) {
            this.tryDropExperience(level, pos, stack, this.xpRange);
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return state.getValue(AGE) < MAX_AGE;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        trySpread(state, level, pos, random, true);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        spawnLightningParticles(pos, random, level, state, true, 0.3);
    }

    public static void spawnLightningParticles(
            BlockPos pos, RandomSource random, Level level, BlockState state
    ) {
        spawnLightningParticles(pos, random, level, state, false);
    }


    public static void spawnLightningParticles(
            BlockPos pos, RandomSource random, Level level, BlockState state, boolean skipSelfCollisionCheck
    ) {
        spawnLightningParticles(pos, random, level, state, skipSelfCollisionCheck, 0.5);
    }

    public static void spawnLightningParticles(
            BlockPos pos, RandomSource random, Level level, BlockState state, boolean skipSelfCollisionCheck, double normalAngleContribution
    ) {
        Direction direction = Direction.getRandom(random);

        BlockPos adjPos = pos.relative(direction);
        BlockState adjState = level.getBlockState(adjPos);
        if (!adjState.isFaceSturdy(level, pos, direction.getOpposite())) {
            Vec3i normal = direction.getNormal();
            double rx = random.nextFloat();
            double ry = random.nextFloat();
            double rz = random.nextFloat();

            VoxelShape shape = state.getVisualShape(level, pos, CollisionContext.empty());
            double midX = (shape.min(Direction.Axis.X) + shape.max(Direction.Axis.X)) * 0.5;
            double midY = (shape.min(Direction.Axis.Y) + shape.max(Direction.Axis.Y)) * 0.5;
            double midZ = (shape.min(Direction.Axis.Z) + shape.max(Direction.Axis.Z)) * 0.5;
            shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> spawnLightningParticles(
                    pos, random, level, skipSelfCollisionCheck, shape, normalAngleContribution,
                    normal, rx, ry, rz, midX, midY, midZ,
                    x1, y1, z1, x2, y2, z2
            ));
        }
    }

    public static boolean pointInsideShape(VoxelShape shape, double x, double y, double z) {
        AtomicBoolean bl = new AtomicBoolean(false);
        shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> {
            if (bl.get()) return;
            if (pointInsideBox(x, y, z, x1, y1, z1, x2, y2, z2)) {
                bl.set(true);
            }
        });
        return bl.get();
    }

    public static boolean pointInsideBox(double x, double y, double z, double x1, double y1, double z1, double x2, double y2, double z2) {
        if (x < x1 || x2 < x) return false;
        if (y < y1 || y2 < x) return false;
        if (z < z1 || z2 < x) return false;

        return true;
    }

    public static void spawnLightningParticles(
            BlockPos pos, RandomSource random, Level level, boolean skipSelfCollisionCheck, VoxelShape shape, double normalAngleContribution,
            Vec3i normal, double rx, double ry, double rz, double midX, double midY, double midZ,
            double x1, double y1, double z1, double x2, double y2, double z2
    ) {
        if (!pointInsideBox(rx, ry, rz, x1, y1, z1, x2, y2, z2)) {
            return;
        }

        int nx = normal.getX();
        int ny = normal.getX();
        int nz = normal.getX();

        double xOffset = nx == 0 ? rx : x1 + (nx + 1) * 0.5 * (x2 - x1);
        double yOffset = ny == 0 ? ry : y1 + (ny + 1) * 0.5 * (y2 - y1);
        double zOffset = nz == 0 ? rz : z1 + (nz + 1) * 0.5 * (z2 - z1);

        // expand point outwards slightly from middle
        if (!skipSelfCollisionCheck && pointInsideShape(shape,
                (xOffset - midX) * 1.01 + midX,
                (yOffset - midY) * 1.01 + midY,
                (zOffset - midZ) * 1.01 + midZ
        )) {
            // avoid any self-intersections
            return;
        }

        double offsetAngleContribution = 1.0 - normalAngleContribution;
        double xSpeed = (xOffset - midX) * offsetAngleContribution + nx * normalAngleContribution;
        double ySpeed = (yOffset - midY) * offsetAngleContribution + ny * normalAngleContribution;
        double zSpeed = (zOffset - midZ) * offsetAngleContribution + nz * normalAngleContribution;

        double speed = Math.sqrt(xSpeed * xSpeed + ySpeed * ySpeed + zSpeed * zSpeed);
        double targetSpeed = 0.5F + 0.7F * random.nextFloat();

        level.addParticle(
                OperationStarcleaveParticleTypes.NUCLEO_LIGHTNING,
                pos.getX() + xOffset,
                pos.getY() + yOffset,
                pos.getZ() + zOffset,
                xSpeed * targetSpeed / speed,
                ySpeed * targetSpeed / speed,
                zSpeed * targetSpeed / speed
        );
    }

    public static void trySpread(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, boolean forceGrowth) {
        if (!level.getGameRules().getBoolean(OperationStarcleaveGameRules.DO_NUCLEOSYNTHESEED_GROWTH) && !forceGrowth) {
            return;
        }

        if (state.getValue(AGE) == MAX_AGE) {
            return;
        }

        boolean growsDown = state.getValue(GROWS_DOWN);

        if (random.nextInt(8) <= (growsDown ? 1 : 2)) {
            if (trySpread(state, level, pos, random, growsDown ? Direction.DOWN : Direction.UP)) {
                return;
            }
        }

        List<Direction> directions = Direction.Plane.HORIZONTAL.shuffledCopy(random);
        for (Direction direction : directions) {
            if (trySpread(state, level, pos, random, direction)) {
                return;
            }
        }

        level.setBlockAndUpdate(pos, state.cycle(AGE));
    }

    public static boolean trySpread(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, Direction direction) {
        boolean growsDown = state.getValue(GROWS_DOWN);
        int age = state.getValue(AGE);
        BlockState newState = random.nextInt(growsDown ? 6 : 3) == 0 ? state.cycle(AGE) : state;

        BlockPos adjPos = pos.relative(direction);
        BlockState adjState = level.getBlockState(adjPos);

        if (canBurrowThrough(adjState)) {
            level.setBlockAndUpdate(adjPos, newState);
            level.scheduleTick(adjPos, newState.getBlock(), 30 + random.nextInt(120));
            level.setBlockAndUpdate(pos, OperationStarcleaveBlocks.NUCLEIC_FISSUREROOT.defaultBlockState().setValue(RotatedPillarBlock.AXIS, direction.getAxis()));
            if (!growsDown) {
                spawnLeaves(level, adjPos, random);
            }

            boolean vertical = direction.getAxis() != Direction.Axis.Y;
            if (vertical || growsDown) {
                BlockPos oppPos = pos.relative(direction.getOpposite());
                BlockState oppState = level.getBlockState(oppPos);
                if (canBurrowThrough(oppState)) {
                    if (random.nextInt(3) == 0 || (vertical && growsDown && age <= 2)) {
                        int newAge;
                        if (random.nextInt(3) == 0) {
                            newAge = MAX_AGE;
                        } else {
                            newAge = Math.min(age + 5 + random.nextInt(5), MAX_AGE);
                        }

                        BlockState nState = state.setValue(AGE, newAge);
                        boolean makeGrowUp = vertical && growsDown;
                        if (makeGrowUp) {
                            nState = nState.setValue(GROWS_DOWN, false);
                        }
                        level.setBlockAndUpdate(oppPos, nState);
                        level.scheduleTick(oppPos, newState.getBlock(), 30 + random.nextInt(120));

                        if (makeGrowUp || !growsDown) {
                            if (random.nextInt(5) <= 1) {
                                spawnLeaves(level, oppPos, random);
                            }
                        }
                    }
                }
            }
            return true;
        }

        return false;
    }

    public static void spawnLeaves(Level level, BlockPos pos, RandomSource random) {
        // place direct neighbours
        for (Direction direction : Direction.values()) {
            BlockPos adjPos = pos.relative(direction);
            if (random.nextInt(5) != 0) {
                trySpawnLeavesAtPos(level, adjPos);
            }
        }

        // place plane-diagonal blocks
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    int distSqr = i * i + j * j + k * k;
                    if (distSqr != 2) continue; // only check the blocks next to the directional blocks

                    BlockPos adjPos = pos.offset(i, j, k);
                    if (random.nextInt(3) != 0) {
                        if (countAdjacentLeaves(level, adjPos) >= 1) {
                            trySpawnLeavesAtPos(level, adjPos);
                        }
                    }
                }
            }
        }

        // place 3-axis-diagonal blocks
        for (int i = -1; i <= 1; i++) {
            if (i == 0) continue;
            for (int j = -1; j <= 1; j++) {
                if (j == 0) continue;
                for (int k = -1; k <= 1; k++) {
                    if (k == 0) continue;
                    BlockPos adjPos = pos.offset(i, j, k);
                    if (random.nextInt(2) != 0) {
                        if (countAdjacentLeaves(level, adjPos) >= 2) {
                            trySpawnLeavesAtPos(level, adjPos);
                        }
                    }
                }
            }
        }
    }

    public static int countAdjacentLeaves(Level level, BlockPos pos) {
        int count = 0;
        for (Direction direction : Direction.values()) {
            BlockPos adjPos = pos.relative(direction);
            BlockState adjState = level.getBlockState(adjPos);
            if (adjState.is(OperationStarcleaveBlocks.NUCLEIC_FISSURELEAVES)) {
                count++;
            }
        }
        return count;
    }

    public static void trySpawnLeavesAtPos(Level level, BlockPos pos) {
        BlockState adjState = level.getBlockState(pos);
        if (adjState.isAir() || adjState.is(BlockTags.REPLACEABLE_BY_TREES)) {
            BlockState leafState = OperationStarcleaveBlocks.NUCLEIC_FISSURELEAVES.defaultBlockState();
            if (adjState.getFluidState().is(FluidTags.WATER)) {
                leafState = leafState.setValue(LeavesBlock.WATERLOGGED, true);
            }
            level.setBlockAndUpdate(pos, leafState);
        }
    }

    public static void detonate(Level level, BlockPos pos, int ignitingFireAge) {
        // TODO optimise this at some point

        // replace seed with fire
        int newFireAge = Math.min(ignitingFireAge + 3, 6);
        level.setBlockAndUpdate(pos, PhlogisticFireBlock.getStateWithAge(level, pos, newFireAge));
        // replace nearby hyperflammables (that are not also seeds) with fire
        instantlyIgniteNearbyHyperflammables(level, pos, newFireAge);

        explode(level, pos);

        RandomSource random = level.getRandom();

        Set<BlockPos> positions = new HashSet<>();
        Set<BlockPos> positionsLast = new HashSet<>();
        positionsLast.add(locateStartPos(level, pos));

        for (int i = 0; i < 19; i++) {
            // clear blocks below
            for (BlockPos targetPos : positionsLast) {
                BlockPos downPos = targetPos.below();
                BlockState downState = level.getBlockState(downPos);
                if (canErode(downState)) {
                    if (!downState.is(OperationStarcleaveBlocks.PHLOGISTIC_FIRE)) {
                        level.setBlockAndUpdate(downPos, Blocks.AIR.defaultBlockState());
                        potentiallySpawnParticle(level, downPos);
                        coagulateHorizontallyAdjacentPlasma(level, downPos);
                    }
                    positions.add(downPos);
                }
            }

            positionsLast.clear();

            // spread outwards
            for (BlockPos targetPos : positions) {
                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    BlockPos adjPos = targetPos.relative(direction);
                    if (!positionsLast.contains(adjPos)) {
                        BlockState adjState = level.getBlockState(adjPos);
                        if (canErode(adjState)) {
                            if (random.nextInt(5) <= 1) {
                                if (!adjState.is(OperationStarcleaveBlocks.PHLOGISTIC_FIRE)) {
                                    level.setBlockAndUpdate(adjPos, Blocks.AIR.defaultBlockState());
                                    potentiallySpawnParticle(level, adjPos);
                                    coagulateHorizontallyAdjacentPlasma(level, adjPos);
                                }
                                positionsLast.add(adjPos);
                            }
                        }
                    }
                }
            }

            positionsLast.addAll(positions);
            positions.clear();

            if (positionsLast.isEmpty()) {
                break;
            }
        }

        positions.addAll(positionsLast);

        for (int i = 0; i < 15; i++) {
            // spread inwards
            for (BlockPos targetPos : positions) {
                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    BlockPos adjPos = targetPos.relative(direction);
                    if (!positions.contains(adjPos)) {
                        if (random.nextInt(1) == 0) {
                            positionsLast.remove(targetPos);

                            BlockPos downPos = targetPos.below();
                            BlockState downState = level.getBlockState(downPos);
                            if (canErode(downState) && !((downState.canBeReplaced() || !downState.canOcclude()) && i <= 1)) {
                                if (!downState.is(OperationStarcleaveBlocks.PHLOGISTIC_FIRE)) {
                                    level.setBlockAndUpdate(downPos, OperationStarcleaveBlocks.COAGULATED_PLASMA.defaultBlockState());
                                    potentiallySpawnParticle(level, downPos);
                                    coagulateHorizontallyAdjacentPlasma(level, downPos);
                                }
                            }
                        }
                    }
                }
            }

            positions.clear();

            // clear blocks below
            for (BlockPos targetPos : positionsLast) {
                BlockPos downPos = targetPos.below();
                BlockState downState = level.getBlockState(downPos);
                boolean spawnsPlasmaAtHeight = i >= 2;
                if (canErode(downState) && !((downState.canBeReplaced() || !downState.canOcclude()) && !spawnsPlasmaAtHeight)) {
                    BlockState state = (!spawnsPlasmaAtHeight ? Blocks.AIR : (i < 14 ? OperationStarcleaveBlocks.PETRICHORIC_PLASMA : OperationStarcleaveBlocks.COAGULATED_PLASMA)).defaultBlockState();
                    if (!downState.is(OperationStarcleaveBlocks.PHLOGISTIC_FIRE) || spawnsPlasmaAtHeight) {
                        level.setBlockAndUpdate(downPos, state);
                        potentiallySpawnParticle(level, downPos);
                        if (!spawnsPlasmaAtHeight) {
                            coagulateHorizontallyAdjacentPlasma(level, downPos);
                        }
                    }
                    positions.add(downPos);
                }
            }

            positionsLast.clear();
            positionsLast.addAll(positions);

            if (positionsLast.isEmpty()) {
                break;
            }
        }
    }

    public static void potentiallySpawnParticle(Level level, BlockPos pos) {
        RandomSource random = level.random;
        if (random.nextInt(32) == 0 && level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    OperationStarcleaveParticleTypes.LARGE_NUCLEAR_SMOKE,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    2,
                    0.25,
                    0.25,
                    0.25,
                    0.2
            );
        }
    }

    public static void coagulateHorizontallyAdjacentPlasma(Level level, BlockPos pos) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos adjPos = pos.relative(direction);
            BlockState adjState = level.getBlockState(adjPos);
            FluidState adjFState = level.getFluidState(adjPos);
            if (adjState.is(OperationStarcleaveBlocks.PETRICHORIC_PLASMA) && !adjFState.isEmpty() && adjFState.isSource()) {
                level.setBlockAndUpdate(adjPos, OperationStarcleaveBlocks.COAGULATED_PLASMA.defaultBlockState());
            }
        }
    }

    public static BlockPos locateStartPos(Level level, BlockPos pos) {
        BlockPos startPos = pos;
        for (int i = 0; i < 7; i++) {
            BlockPos upPos = startPos.above();
            BlockState upState = level.getBlockState(upPos);
            if (canErode(upState)) {
                startPos = upPos;
            }
        }
        return startPos;
    }

    public static void instantlyIgniteNearbyHyperflammables(Level level, BlockPos pos, int fireAge) {
        BlockPos.MutableBlockPos mutableBlockPos = pos.mutable();

        int searchRadius = 3;
        for (int i = -searchRadius; i <= searchRadius; i++) {
            mutableBlockPos.setX(pos.getX() + i);
            for (int j = -searchRadius; j <= searchRadius; j++) {
                mutableBlockPos.setY(pos.getY() + j);
                for (int k = -searchRadius; k < searchRadius; k++) {
                    int distSqr = i * i + j * j + k * k;

                    if (distSqr <= searchRadius * searchRadius) {
                        mutableBlockPos.setZ(pos.getZ() + k);

                        BlockState currentState = level.getBlockState(mutableBlockPos);
                        if (currentState.is(OperationStarcleaveBlockTags.PHLOGISTIC_HYPERFLAMMABLES) && !currentState.is(OperationStarcleaveBlocks.NUCLEOSYNTHESEED)) {
                            level.setBlockAndUpdate(mutableBlockPos, PhlogisticFireBlock.getStateWithAge(level, mutableBlockPos, fireAge));
                        }
                    }
                }
            }
        }
    }

    public static void explode(Level level, BlockPos pos) {
        ExplosionDamageCalculator damageCalculator = new ExplosionDamageCalculator() {
            @Override
            public boolean shouldBlockExplode(Explosion explosion, BlockGetter reader, BlockPos pos, BlockState state, float power) {
                return state.is(OperationStarcleaveBlockTags.NUCLEOSYNTHESEED_BLAST_IMMUNE)
                        ? false
                        : super.shouldBlockExplode(explosion, reader, pos, state, power);
            }
        };

        level.explode(
                null,
                OperationStarcleaveDamageTypes.source(level, OperationStarcleaveDamageTypes.IN_PHLOGISTIC_FIRE),
                damageCalculator,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                4,
                false,
                Level.ExplosionInteraction.TNT,
                OperationStarcleaveParticleTypes.NUCLEAR_SMOKE,
                OperationStarcleaveParticleTypes.LARGE_NUCLEAR_SMOKE,
                SoundEvents.GENERIC_EXPLODE
        );

        // spawn lightning particles
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    OperationStarcleaveParticleTypes.NUCLEO_LIGHTNING,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    9,
                    0.25,
                    0.25,
                    0.25,
                    2.25
            );
        }
    }

    public static boolean canBurrowThrough(BlockState state) {
        return canDestroy(state);
    }

    public static boolean canErode(BlockState state) {
        // do destroy coagulated plasma
        if (state.is(OperationStarcleaveBlocks.COAGULATED_PLASMA)) {
            return true;
        }

        return canDestroy(state);
    }

    public static boolean canDestroy(BlockState state) {
        // do not destroy roots or seeds or phlogistic fire
        if (state.is(OperationStarcleaveBlockTags.NUCLEOSYNTHESEED_BLAST_IMMUNE)) {
            return false;
        }

        // do destroy petrichoric blocks
        if (state.is(OperationStarcleaveBlocks.PETRICHORIC_PLASMA) || state.is(OperationStarcleaveBlocks.PETRICHORIC_VAPOR)) {
            return true;
        }

        // destroy replaceable blocks
        if (state.canBeReplaced()) {
            return true;
        }

        return mayDestroy(state);
    }

    public static boolean mayDestroy(BlockState state) {
        // do not destroy boss immune blocks
        if (state.is(BlockTags.WITHER_IMMUNE) || state.is(BlockTags.DRAGON_IMMUNE)) {
            return false;
        }

        // destroy blocks with low explosion resistane
        return !(state.getBlock().getExplosionResistance() > 6);
    }
}
