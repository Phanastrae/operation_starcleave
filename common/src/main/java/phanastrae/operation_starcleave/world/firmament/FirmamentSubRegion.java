package phanastrae.operation_starcleave.world.firmament;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import phanastrae.operation_starcleave.duck.FirmamentWatcher;
import phanastrae.operation_starcleave.network.packet.UpdateFirmamentSubRegionPayload;
import phanastrae.operation_starcleave.services.XPlatInterface;
import phanastrae.operation_starcleave.world.firmament.actor.FirmamentActor;
import phanastrae.operation_starcleave.world.firmament.data.FirmamentSubRegionData;
import phanastrae.operation_starcleave.world.firmament.pos.SubRegionPos;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FirmamentSubRegion implements FirmamentAccess {
    // getter/setter functions should only be called with x and z in range [0, 15]

    public static final int SUBREGION_SIZE = 32;

    public static final int TILES = 8;
    public static final int TILE_MASK = 0x3;
    public static final int TILE_SIZE_BITS = 2;
    public static final int TILE_SIZE = 4;

    public static final int DATA_SIZE_BYTES = TILES * TILES;

    public int[][] damage;

    private final List<FirmamentActor> actors = new ArrayList<>();
    private final List<FirmamentActor> newActors = new ArrayList<>();

    private boolean pendingClientUpdate = false;

    private boolean hadDamageLastCheck = false;

    public final SubRegionPos subRegionPos;
    public final FirmamentRegion firmamentRegion;

    public FirmamentSubRegion(FirmamentRegion firmamentRegion, SubRegionPos subRegionPos) {
        this.firmamentRegion = firmamentRegion;
        this.subRegionPos = subRegionPos;

        this.damage = new int[TILES][TILES];
    }

    public int minX() {
        return this.subRegionPos.minWorldX;
    }

    public int minZ() {
        return this.subRegionPos.minWorldZ;
    }

    @Override
    public void clearActors() {
        this.actors.clear();
        this.newActors.clear();
    }

    @Override
    public void addActor(FirmamentActor actor) {
        this.newActors.add(actor);
    }

    @Override
    public void manageActors() {
        actors.addAll(newActors);
        newActors.clear();

        actors.removeIf((actor) -> !actor.isActive());
    }

    @Override
    public void tickActors() {
        for (FirmamentActor actor : actors) {
            if (actor.isActive()) {
                actor.tick();
            }
        }
    }

    @Override
    public void forEachActor(Consumer<FirmamentActor> consumer) {
        for (FirmamentActor actor : actors) {
            consumer.accept(actor);
        }
    }

    @Override
    public int getDamage(int x, int z) {
        return damage[x >> TILE_SIZE_BITS][z >> TILE_SIZE_BITS];
    }

    @Override
    public void setDamage(int x, int z, int value) {
        this.pendingClientUpdate = true;
        damage[x >> TILE_SIZE_BITS][z >> TILE_SIZE_BITS] = value;
    }

    public static byte[] getAsByteArray(int[][] target) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(DATA_SIZE_BYTES);
        boolean multipleValues = false;
        byte val = (byte) target[0][0];
        for (int i = 0; i < FirmamentSubRegion.TILES; i++) {
            for (int j = 0; j < FirmamentSubRegion.TILES; j++) {
                byte b = (byte) target[i][j];
                byteBuffer.put(b);
                if (b != val) {
                    multipleValues = true;
                }
            }
        }
        if (multipleValues) {
            return byteBuffer.array();
        } else {
            ByteBuffer buffer = ByteBuffer.allocate(1);
            buffer.put(val);
            return buffer.array();
        }
    }

    public void checkDamage() {
        for (int i = 0; i < TILES; i++) {
            for (int j = 0; j < TILES; j++) {
                if (damage[i][j] != 0) {
                    this.hadDamageLastCheck = true;
                    return;
                }
            }
        }
        this.hadDamageLastCheck = false;
    }

    public boolean hadDamageLastCheck() {
        return this.hadDamageLastCheck;
    }

    public void flushUpdates() {
        if (this.pendingClientUpdate) {
            this.pendingClientUpdate = false;
            Level world = this.firmamentRegion.firmament.getLevel();
            if (world instanceof ServerLevel serverWorld) {
                List<ServerPlayer> nearbyPlayers = new ArrayList<>();
                serverWorld.players().forEach(serverPlayerEntity -> {
                    if (((FirmamentWatcher) serverPlayerEntity).operation_starcleave$getWatchedRegions().getWatchedRegions().contains(this.firmamentRegion.regionPos.id)) {
                        nearbyPlayers.add(serverPlayerEntity);
                    }
                });

                if (!nearbyPlayers.isEmpty()) {
                    FirmamentSubRegionData data = new FirmamentSubRegionData(this);
                    nearbyPlayers.forEach(serverPlayerEntity -> XPlatInterface.INSTANCE.sendPayload(serverPlayerEntity, new UpdateFirmamentSubRegionPayload(this.subRegionPos.id, data)));
                }
            }
        }
    }

    public void readFromData(FirmamentSubRegionData firmamentSubRegionData) {
        readFromByteArray(firmamentSubRegionData.damageData, this.damage, 0x7);
        checkDamage();
    }

    public static void readFromByteArray(byte[] byteArray, int[][] targetArray, int mask) {
        if (byteArray == null) {
            return;
        }

        if (byteArray.length == DATA_SIZE_BYTES) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(DATA_SIZE_BYTES);
            byteBuffer.put(byteArray);
            byteBuffer.position(0);
            for (int i = 0; i < FirmamentSubRegion.TILES; i++) {
                for (int j = 0; j < FirmamentSubRegion.TILES; j++) {
                    targetArray[i][j] = byteBuffer.get() & mask;
                }
            }
        } else if (byteArray.length == 1) {
            int val = byteArray[0] & mask;
            for (int i = 0; i < FirmamentSubRegion.TILES; i++) {
                for (int j = 0; j < FirmamentSubRegion.TILES; j++) {
                    targetArray[i][j] = val;
                }
            }
        }
    }
}
