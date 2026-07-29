package phanastrae.operation_starcleave.entity;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.block.tag.OperationStarcleaveBlockTags;
import phanastrae.operation_starcleave.world.starbleach.Starbleach;

import java.util.*;

public class StarbleachChargeEntity extends Entity {
    public static final String KEY_CHARGE = "charge";
    public static final String KEY_DELAY = "delay";
    public static final String KEY_CANDIDATE_OFFSETS = "candidate_offsets";
    public static final String KEY_VISITED_OFFSETS = "visited_offsets";

    private int charge = 0;
    private int delay = 0;
    private final List<Vec3i> candidateOffsets = new ArrayList<>();
    private final Set<Vec3i> visitedOffsets = new HashSet<>();

    public StarbleachChargeEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt(KEY_CHARGE, this.charge);
        tag.putInt(KEY_DELAY, this.delay);
        tag.putByteArray(KEY_CANDIDATE_OFFSETS, convertToByteArray(this.candidateOffsets));
        tag.putByteArray(KEY_VISITED_OFFSETS, convertToByteArray(this.visitedOffsets));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains(KEY_CHARGE, Tag.TAG_INT)) {
            this.charge = tag.getInt(KEY_CHARGE);
        }
        if (tag.contains(KEY_DELAY, Tag.TAG_INT)) {
            this.delay = tag.getInt(KEY_DELAY);
        }
        if (tag.contains(KEY_CANDIDATE_OFFSETS, Tag.TAG_BYTE_ARRAY)) {
            List<Vec3i> vecs = convertFromByteArray(tag.getByteArray(KEY_CANDIDATE_OFFSETS));
            this.candidateOffsets.clear();
            this.candidateOffsets.addAll(vecs);
        }
        if (tag.contains(KEY_VISITED_OFFSETS, Tag.TAG_BYTE_ARRAY)) {
            List<Vec3i> vecs = convertFromByteArray(tag.getByteArray(KEY_VISITED_OFFSETS));
            this.visitedOffsets.clear();
            this.visitedOffsets.addAll(vecs);
        }
    }

    public byte[] convertToByteArray(Collection<Vec3i> vectors) {
        List<Byte> bList = new ArrayList<>();
        for (Vec3i vec : vectors) {
            int x = vec.getX();
            int y = vec.getY();
            int z = vec.getZ();
            if (Math.abs(x) > 127 || Math.abs(y) > 127 || Math.abs(z) > 127) {
                continue;
            }
            bList.add((byte) (x & 0xFF));
            bList.add((byte) (y & 0xFF));
            bList.add((byte) (z & 0xFF));
        }

        byte[] bArray = new byte[bList.size()];
        for (int i = 0; i < bList.size(); i++) {
            bArray[i] = bList.get(i);
        }
        return bArray;
    }

    public List<Vec3i> convertFromByteArray(byte[] bytes) {
        if (bytes.length % 3 != 0) {
            return List.of();
        } else {
            int size = bytes.length / 3;
            ArrayList<Vec3i> list = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                int x = bytes[3 * i];
                int y = bytes[3 * i + 1];
                int z = bytes[3 * i + 2];
                list.add(new Vec3i(x, y, z));
            }
            return list;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level() instanceof ServerLevel serverLevel) {
            this.delay--;
            if (this.delay <= 0) {
                this.delay = this.random.nextIntBetweenInclusive(3, 10);
                this.bleach(serverLevel);
            }
        }
    }

    public void bleach(ServerLevel serverLevel) {
        if (this.visitedOffsets.isEmpty()) {
            this.tryAddOffset(Vec3i.ZERO, 0);
        }

        double sqrt = Math.sqrt(Math.max(this.charge, 0));
        double random = 0.75 + 0.5 * this.random.nextFloat();
        int cap = (int) Math.max(1, 1.8 * sqrt * random);
        List<Direction> directions = new ArrayList(List.of(Direction.values()));
        for (int i = 0; i < cap && this.charge > 0; i++) {
            Vec3i offset = this.getOffset();
            if (offset == null) {
                break;
            }
            BlockPos pos = this.blockPosition().offset(offset);

            BlockState state = serverLevel.getBlockState(pos);
            boolean didBleaching = Starbleach.starbleachPos(serverLevel, pos, state, Starbleach.StarbleachTarget.NO_FILLING, 3, 2);
            if (didBleaching) {
                this.charge--;
            }

            if (didBleaching || Starbleach.isStarbleached(state) || this.candidateOffsets.isEmpty()) {
                Util.shuffle(directions, this.random);
                for (Direction direction : directions) {
                    this.tryAddOffset(offset.offset(direction.getNormal()), getBias(state, direction, didBleaching));
                }
            }
        }

        this.charge--;
        if (this.charge <= 0 || this.candidateOffsets.isEmpty()) {
            this.discard();
        }
    }

    public float getBias(BlockState state, Direction direction, boolean didBleaching) {
        if (state.is(BlockTags.LOGS) || state.is(OperationStarcleaveBlockTags.STARBLEACHED_LOGS)) {
            Direction.Axis axis = state.getOptionalValue(BlockStateProperties.AXIS).orElse(Direction.Axis.Y);
            if (direction.getAxis().equals(axis)) {
                return 0.05f;
            } else {
                return 0.1f;
            }
        } else if (didBleaching) {
            return 0.5f;
        } else {
            return direction.equals(Direction.DOWN) ? 0.65f : 0.8f;
        }
    }

    public void tryAddOffset(Vec3i offset, float bias) {
        // lower bias gives a higher change to be picked earlier
        if (Math.abs(offset.getX()) > 127
                || Math.abs(offset.getY()) > 127
                || Math.abs(offset.getZ()) > 127
        ) {
            return;
        }

        if (!this.visitedOffsets.contains(offset)) {
            this.visitedOffsets.add(offset);
            float r = this.random.nextFloat() - this.random.nextFloat();
            if (r < 0) {
                // r in [-1, 0)
                r = r * bias + bias;
                // r in [0, bias)
            } else {
                // r in [0, 1]
                r = r * (1 - bias) + bias;
                // r in [bias, 1]
            }
            int index = Mth.clamp(Mth.floor(r), 0, this.candidateOffsets.size());
            this.candidateOffsets.add(index, offset);
        }
    }

    @Nullable
    public Vec3i getOffset() {
        if (this.candidateOffsets.isEmpty()) {
            return null;
        } else {
            int size = this.candidateOffsets.size();
            // bias towards start of list
            int index = Mth.clamp(Mth.floor((this.random.nextFloat() * this.random.nextFloat() * this.random.nextFloat() * size)), 0, size);
            return this.candidateOffsets.remove(index);
        }
    }

    @Override
    protected boolean canAddPassenger(Entity entity) {
        return false;
    }

    @Override
    protected boolean couldAcceptPassenger() {
        return false;
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }
}
