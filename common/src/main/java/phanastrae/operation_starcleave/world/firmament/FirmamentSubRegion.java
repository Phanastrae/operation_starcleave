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
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class FirmamentSubRegion implements FirmamentAccess {
    // getter/setter functions should only be called with x and z in range [0, 15]

    public static final int SUBREGION_SIZE = 32;

    public static final int TILES = 8;
    public static final int TILE_MASK = 0x3;
    public static final int TILE_SIZE_BITS = 2;
    public static final int TILE_SIZE = 4;

    public static final int DATA_SIZE_BYTES = TILES * TILES;

    //    x -->
    // z  0 1 2
    // |  3 4 5
    // \/ 6 7 8
    public static final int[] X_OFFSETS = new int[]{
            -SUBREGION_SIZE, 0, SUBREGION_SIZE,
            -SUBREGION_SIZE, 0, SUBREGION_SIZE,
            -SUBREGION_SIZE, 0, SUBREGION_SIZE
    };
    public static final int[] Z_OFFSETS = new int[]{
            -SUBREGION_SIZE, -SUBREGION_SIZE, -SUBREGION_SIZE,
            0, 0, 0,
            SUBREGION_SIZE, SUBREGION_SIZE, SUBREGION_SIZE
    };

    public int[][] velocity;
    public int[][] displacement;

    public int[][] damage;
    public int[][] drip;
    public float[][] dDrip;

    private final List<FirmamentActor> actors = new ArrayList<>();
    private final List<FirmamentActor> newActors = new ArrayList<>();

    private final boolean[] active = new boolean[9];
    private boolean shouldUpdate = false;

    private boolean pendingClientUpdate = false;

    private boolean hadDamageLastCheck = false;

    public final SubRegionPos subRegionPos;
    public final FirmamentRegion firmamentRegion;

    public FirmamentSubRegion(FirmamentRegion firmamentRegion, SubRegionPos subRegionPos) {
        this.firmamentRegion = firmamentRegion;
        this.subRegionPos = subRegionPos;

        this.displacement = new int[TILES][TILES];
        this.velocity = new int[TILES][TILES];
        this.damage = new int[TILES][TILES];
        this.drip = new int[TILES][TILES];
        this.dDrip = new float[TILES][TILES];
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
    public void forEachActivePosition(BiConsumer<Integer, Integer> method) {
        for (int i = 0; i < TILES; i++) {
            for (int j = 0; j < TILES; j++) {
                method.accept(i * TILE_SIZE, j * TILE_SIZE);
            }
        }
    }

    @Override
    public int getDrip(int x, int z) {
        return drip[x >> TILE_SIZE_BITS][z >> TILE_SIZE_BITS];
    }

    @Override
    public int getDamage(int x, int z) {
        return damage[x >> TILE_SIZE_BITS][z >> TILE_SIZE_BITS];
    }

    @Override
    public int getDisplacement(int x, int z) {
        return displacement[x >> TILE_SIZE_BITS][z >> TILE_SIZE_BITS];
    }

    @Override
    public int getVelocity(int x, int z) {
        return velocity[x >> TILE_SIZE_BITS][z >> TILE_SIZE_BITS];
    }

    @Override
    public float getDDrip(int x, int z) {
        return dDrip[x >> TILE_SIZE_BITS][z >> TILE_SIZE_BITS];
    }

    @Override
    public void setDrip(int x, int z, int value) {
        drip[x >> TILE_SIZE_BITS][z >> TILE_SIZE_BITS] = value;
    }

    @Override
    public void setDamage(int x, int z, int value) {
        this.pendingClientUpdate = true;
        damage[x >> TILE_SIZE_BITS][z >> TILE_SIZE_BITS] = value;
    }

    @Override
    public void setDisplacement(int x, int z, int value) {
        displacement[x >> TILE_SIZE_BITS][z >> TILE_SIZE_BITS] = value;
    }

    @Override
    public void setVelocity(int x, int z, int value) {
        velocity[x >> TILE_SIZE_BITS][z >> TILE_SIZE_BITS] = value;
    }

    @Override
    public void setDDrip(int x, int z, float value) {
        dDrip[x >> TILE_SIZE_BITS][z >> TILE_SIZE_BITS] = value;
    }

    public boolean shouldUpdate() {
        return this.shouldUpdate;
    }

    public void markShouldUpdate() {
        this.shouldUpdate = true;
    }

    @Override
    public void clearShouldUpdate() {
        this.shouldUpdate = false;
    }

    @Override
    public void markActive(int x, int z) {
        int tx = x >> TILE_SIZE_BITS;
        int tz = z >> TILE_SIZE_BITS;

        boolean xMin = tx == 0;
        boolean zMin = tz == TILES - 1;
        boolean xMax = tx == 0;
        boolean zMax = tz == TILES - 1;

        active[0] |= xMin && zMin;
        active[1] |= zMin;
        active[2] |= xMax && zMin;
        active[3] |= xMin;
        active[4] = true;
        active[5] |= xMax;
        active[6] |= xMin && zMax;
        active[7] |= zMax;
        active[8] |= xMax && zMax;

    }

    @Override
    public void clearActive() {
        for (int i = 0; i < 9; i++) {
            active[i] = false;
        }
    }

    @Override
    public void markUpdatesFromActivity() {
        for (int k = 0; k < 9; k++) {
            if (active[k]) {
                this.firmamentRegion.firmament.markShouldUpdate(this.minX() + X_OFFSETS[k], this.minZ() + Z_OFFSETS[k]);
            }
        }
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
        //readFromByteArray(firmamentSubRegionData.displacementData, this.displacement, 0xF);
        //readFromByteArray(firmamentSubRegionData.velocityData, this.velocity, 0xF);
        readFromByteArray(firmamentSubRegionData.damageData, this.damage, 0x7);
        //readFromByteArray(firmamentSubRegionData.dripData, this.drip, 0x7);
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
