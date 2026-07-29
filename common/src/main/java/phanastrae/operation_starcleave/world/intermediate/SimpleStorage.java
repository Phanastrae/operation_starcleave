package phanastrae.operation_starcleave.world.intermediate;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public class SimpleStorage implements IntermediateLevelStorage {
    private static final BlockState EMPTY_STATE = Blocks.STRUCTURE_VOID.defaultBlockState();

    private final Object2ObjectOpenHashMap<BlockPos, BlockState> blocks;
    private final Object2ObjectOpenHashMap<BlockPos, BlockEntity> blockEntities;
    private final List<Entity> entities;

    public SimpleStorage() {
        this.blocks = new Object2ObjectOpenHashMap<>();
        this.blockEntities = new Object2ObjectOpenHashMap<>();
        this.entities = new ObjectArrayList<>();
    }

    @Override
    public boolean setBlockState(BlockPos pos, BlockState state) {
        this.blockEntities.remove(pos);
        this.blocks.put(pos.immutable(), state);

        if (state.getBlock() instanceof EntityBlock entityBlock) {
            BlockEntity blockEntity = entityBlock.newBlockEntity(pos, state);
            if (blockEntity != null) {
                this.blockEntities.put(pos.immutable(), blockEntity);
            }
        }
        return true;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return this.blocks.getOrDefault(pos, EMPTY_STATE);
    }

    @Override
    public @Nullable BlockEntity getBlockEntity(BlockPos pos) {
        return this.blockEntities.getOrDefault(pos, null);
    }

    @Override
    public void addEntity(Entity entity) {
        this.entities.add(entity);
    }

    @Nullable
    public AABB getBlockBoundingBox() {
        Optional<BlockPos> optionalAny = this.blocks.keySet().stream().findAny();
        if (optionalAny.isEmpty()) {
            return null;
        } else {
            BlockPos any = optionalAny.get();

            int minX = any.getX();
            int minY = any.getY();
            int minZ = any.getZ();
            int maxX = any.getX();
            int maxY = any.getY();
            int maxZ = any.getZ();

            for (BlockPos pos : this.blocks.keySet()) {
                if (pos.getX() < minX) {
                    minX = pos.getX();
                }
                if (pos.getY() < minY) {
                    minY = pos.getY();
                }
                if (pos.getZ() < minZ) {
                    minZ = pos.getZ();
                }

                if (pos.getX() > maxX) {
                    maxX = pos.getX();
                }
                if (pos.getY() > maxY) {
                    maxY = pos.getY();
                }
                if (pos.getZ() > maxZ) {
                    maxZ = pos.getZ();
                }
            }

            return new AABB(minX, minY, minZ, maxX + 1.0, maxY + 1.0, maxZ + 1.0);
        }
    }

    public void forEachBlock(BiConsumer<BlockPos, BlockState> biConsumer) {
        this.blocks.forEach(biConsumer);
    }
}
