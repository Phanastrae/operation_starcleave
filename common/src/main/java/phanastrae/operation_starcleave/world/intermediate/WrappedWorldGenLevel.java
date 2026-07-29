package phanastrae.operation_starcleave.world.intermediate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.LevelTickAccess;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class WrappedWorldGenLevel implements WorldGenLevel {

    protected final WorldGenLevel wrapped;

    public WrappedWorldGenLevel(WorldGenLevel wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public long getSeed() {
        return this.wrapped.getSeed();
    }

    @Override
    public ServerLevel getLevel() {
        return this.wrapped.getLevel();
    }

    @Override
    public long nextSubTickCount() {
        return this.wrapped.nextSubTickCount();
    }

    @Override
    public LevelTickAccess<Block> getBlockTicks() {
        return this.wrapped.getBlockTicks();
    }

    @Override
    public LevelTickAccess<Fluid> getFluidTicks() {
        return this.wrapped.getFluidTicks();
    }

    @Override
    public LevelData getLevelData() {
        return this.wrapped.getLevelData();
    }

    @Override
    public DifficultyInstance getCurrentDifficultyAt(BlockPos pos) {
        return this.wrapped.getCurrentDifficultyAt(pos);
    }

    @Override
    public @Nullable MinecraftServer getServer() {
        return this.wrapped.getServer();
    }

    @Override
    public ChunkSource getChunkSource() {
        return this.wrapped.getChunkSource();
    }

    @Override
    public RandomSource getRandom() {
        return this.wrapped.getRandom();
    }

    @Override
    public void playSound(@Nullable Player player, BlockPos pos, SoundEvent sound, SoundSource source, float volume, float pitch) {
        this.wrapped.playSound(player, pos, sound, source, volume, pitch);
    }

    @Override
    public void addParticle(ParticleOptions particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        this.wrapped.addParticle(particleData, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    @Override
    public void levelEvent(@Nullable Player player, int type, BlockPos pos, int data) {
        this.wrapped.levelEvent(player, type, pos, data);
    }

    @Override
    public void gameEvent(Holder<GameEvent> gameEvent, Vec3 pos, GameEvent.Context context) {
        this.wrapped.gameEvent(gameEvent, pos, context);
    }

    @Override
    public float getShade(Direction direction, boolean shade) {
        return this.wrapped.getShade(direction, shade);
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return this.wrapped.getLightEngine();
    }

    @Override
    public WorldBorder getWorldBorder() {
        return this.wrapped.getWorldBorder();
    }

    @Override
    public @Nullable BlockEntity getBlockEntity(BlockPos pos) {
        return this.wrapped.getBlockEntity(pos);
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return this.wrapped.getBlockState(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return this.wrapped.getFluidState(pos);
    }

    @Override
    public List<Entity> getEntities(@Nullable Entity entity, AABB area, Predicate<? super Entity> predicate) {
        return this.wrapped.getEntities(entity, area, predicate);
    }

    @Override
    public <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> entityTypeTest, AABB bounds, Predicate<? super T> predicate) {
        return this.wrapped.getEntities(entityTypeTest, bounds, predicate);
    }

    @Override
    public List<? extends Player> players() {
        return this.wrapped.players();
    }

    @Override
    public @Nullable ChunkAccess getChunk(int x, int z, ChunkStatus chunkStatus, boolean requireChunk) {
        return this.wrapped.getChunk(x, z, chunkStatus, requireChunk);
    }

    @Override
    public int getHeight(Heightmap.Types heightmapType, int x, int z) {
        return this.wrapped.getHeight(heightmapType, x, z);
    }

    @Override
    public int getSkyDarken() {
        return this.wrapped.getSkyDarken();
    }

    @Override
    public BiomeManager getBiomeManager() {
        return this.wrapped.getBiomeManager();
    }

    @Override
    public Holder<Biome> getUncachedNoiseBiome(int x, int y, int z) {
        return this.wrapped.getUncachedNoiseBiome(x, y, z);
    }

    @Override
    public boolean isClientSide() {
        return this.wrapped.isClientSide();
    }

    @Override
    public int getSeaLevel() {
        return this.wrapped.getSeaLevel();
    }

    @Override
    public DimensionType dimensionType() {
        return this.wrapped.dimensionType();
    }

    @Override
    public RegistryAccess registryAccess() {
        return this.wrapped.registryAccess();
    }

    @Override
    public FeatureFlagSet enabledFeatures() {
        return this.wrapped.enabledFeatures();
    }

    @Override
    public boolean isStateAtPosition(BlockPos pos, Predicate<BlockState> state) {
        return this.wrapped.isStateAtPosition(pos, state);
    }

    @Override
    public boolean isFluidAtPosition(BlockPos pos, Predicate<FluidState> predicate) {
        return this.wrapped.isFluidAtPosition(pos, predicate);
    }

    @Override
    public boolean setBlock(BlockPos pos, BlockState state, int flags, int recursionLeft) {
        return this.wrapped.setBlock(pos, state, flags, recursionLeft);
    }

    @Override
    public boolean removeBlock(BlockPos pos, boolean isMoving) {
        return this.wrapped.removeBlock(pos, isMoving);
    }

    @Override
    public boolean destroyBlock(BlockPos pos, boolean dropBlock, @Nullable Entity entity, int recursionLeft) {
        return this.wrapped.destroyBlock(pos, dropBlock, entity, recursionLeft);
    }
}
