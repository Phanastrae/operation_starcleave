package phanastrae.operation_starcleave.world.intermediate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraft.world.ticks.ScheduledTick;
import net.minecraft.world.ticks.TickPriority;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class IntermediateWorldGenLevel extends WrappedWorldGenLevel {
    private static final LevelTickAccess<Block> BLOCK_TICK_DUMMY = createDummyTickAccess();
    private static final LevelTickAccess<Fluid> FLUID_TICK_DUMMY = createDummyTickAccess();

    private final IntermediateLevelStorage intermediateStorage;
    private final RandomSource random;

    public IntermediateWorldGenLevel(IntermediateLevelStorage intermediateStorage, WorldGenLevel wrapped) {
        super(wrapped);
        this.intermediateStorage = intermediateStorage;
        this.random = RandomSource.create(this.wrapped.getRandom().nextLong());
    }

    public static <T> LevelTickAccess<T> createDummyTickAccess() {
        return new LevelTickAccess<T>() {
            @Override
            public boolean willTickThisTick(BlockPos pos, T type) {
                return false;
            }

            @Override
            public void schedule(ScheduledTick<T> tick) {
                // empty
            }

            @Override
            public boolean hasScheduledTick(BlockPos pos, T type) {
                return false;
            }

            @Override
            public int count() {
                return 0;
            }
        };
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        BlockState state = this.intermediateStorage.getBlockState(pos);
        if (state.is(Blocks.STRUCTURE_VOID)) {
            return this.wrapped.getBlockState(pos);
        } else {
            return state;
        }
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return this.getBlockState(pos).getFluidState();
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        BlockEntity blockEntity = this.intermediateStorage.getBlockEntity(pos);
        if (blockEntity != null) {
            return blockEntity;
        } else {
            BlockState state = getBlockState(pos);
            if (state.is(Blocks.STRUCTURE_VOID)) {
                return this.wrapped.getBlockEntity(pos);
            } else {
                return null;
            }
        }
    }

    @Override
    public boolean isStateAtPosition(BlockPos pos, Predicate<BlockState> predicate) {
        return predicate.test(this.getBlockState(pos));
    }

    @Override
    public boolean isFluidAtPosition(BlockPos pos, Predicate<FluidState> predicate) {
        return predicate.test(this.getFluidState(pos));
    }

    @Override
    public boolean setBlock(BlockPos pos, BlockState state, int flags, int recursionLeft) {
        return this.intermediateStorage.setBlockState(pos, state);
    }

    @Override
    public boolean removeBlock(BlockPos pos, boolean isMoving) {
        return this.intermediateStorage.setBlockState(pos, Blocks.AIR.defaultBlockState());
    }

    @Override
    public boolean destroyBlock(BlockPos pos, boolean dropBlock, @Nullable Entity entity, int recursionLeft) {
        return this.intermediateStorage.setBlockState(pos, Blocks.AIR.defaultBlockState());
    }

    @Override
    public boolean addFreshEntity(Entity entity) {
        this.intermediateStorage.addEntity(entity);
        return true;
    }

    @Nullable
    @Override
    public ChunkAccess getChunk(int x, int z, ChunkStatus chunkStatus, boolean requireChunk) {
        if (requireChunk) {
            return this.wrapped.getChunk(x, z, chunkStatus, true);
        } else {
            return null;
        }
    }

    @Override
    public LevelTickAccess<Block> getBlockTicks() {
        return BLOCK_TICK_DUMMY;
    }

    @Override
    public LevelTickAccess<Fluid> getFluidTicks() {
        return FLUID_TICK_DUMMY;
    }

    @Override
    public RandomSource getRandom() {
        return this.random;
    }

    @Override
    public List<Entity> getEntities(@Nullable Entity entity, AABB area, Predicate<? super Entity> predicate) {
        return List.of();
    }

    @Override
    public <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> entityTypeTest, AABB bounds, Predicate<? super T> predicate) {
        return List.of();
    }

    @Override
    public void scheduleTick(BlockPos pos, Fluid fluid, int delay) {
        // empty
    }

    @Override
    public void scheduleTick(BlockPos pos, Fluid fluid, int delay, TickPriority priority) {
        // empty
    }

    @Override
    public void scheduleTick(BlockPos pos, Block block, int delay) {
        // empty
    }

    @Override
    public void scheduleTick(BlockPos pos, Block block, int delay, TickPriority priority) {
        // empty
    }

    @Override
    public void playSound(@Nullable Player player, BlockPos pos, SoundEvent sound, SoundSource source, float volume, float pitch) {
        // empty
    }

    @Override
    public void addParticle(ParticleOptions particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        // empty
    }

    @Override
    public void levelEvent(@Nullable Player player, int type, BlockPos pos, int data) {
        // empty
    }

    @Override
    public void gameEvent(Holder<GameEvent> gameEvent, Vec3 pos, GameEvent.Context context) {
        // empty
    }
}
