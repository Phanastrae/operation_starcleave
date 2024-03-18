package phanastrae.operation_starcleave.world.firmament;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.apache.logging.log4j.util.TriConsumer;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.network.packet.s2c.UpdateFirmamentSubRegionS2CPacket;

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
    public final int[] xOffset = new int[]{
            -1, 0, SUBREGION_SIZE,
            -1, 0, SUBREGION_SIZE,
            -1, 0, SUBREGION_SIZE
    };
    public final int[] zOffset = new int[]{
            -1, -1, -1,
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

    boolean[] active = new boolean[9];
    boolean shouldUpdate = false;

    boolean pendingClientUpdate = false;

    boolean hadDamageLastCheck = false;

    // world coords of minimum x-z corner
    public final int x;
    public final int z;

    public final FirmamentRegion firmamentRegion;

    public FirmamentSubRegion(FirmamentRegion firmamentRegion, int x, int z) {
        this.firmamentRegion = firmamentRegion;
        this.x = x;
        this.z = z;

        this.displacement = new int[TILES][TILES];
        this.velocity = new int[TILES][TILES];
        this.damage = new int[TILES][TILES];
        this.drip = new int[TILES][TILES];
        this.dDrip = new float[TILES][TILES];
    }

    public void clear() {
        for(int i = 0; i < FirmamentSubRegion.TILES; i++) {
            for(int j = 0; j < FirmamentSubRegion.TILES; j++) {
                this.damage[i][j] = 0;
                this.drip[i][j] = 0;
                this.dDrip[i][j] = 0;
                this.displacement[i][j] = 0;
                this.velocity[i][j] = 0;
            }
        }
        this.pendingClientUpdate = true;
        this.firmamentRegion.pendingClientUpdate = true;
        this.clearActors();
    }

    public void markShouldUpdate() {
        this.shouldUpdate = true;
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
        for(FirmamentActor actor : actors) {
            if(actor.isActive()) {
                actor.tick();
            }
        }
    }

    @Override
    public void forEachActor(Consumer<FirmamentActor> consumer) {
        for(FirmamentActor actor : actors) {
            consumer.accept(actor);
        }
    }

    @Override
    public void forEachPosition(BiConsumer<Integer, Integer> method) {
        for(int i = 0; i < TILES; i++) {
            for(int j = 0; j < TILES; j++) {
                method.accept(i * TILE_SIZE, j * TILE_SIZE);
            }
        }
    }

    public void forEachPosition(TriConsumer<Integer, Integer, Boolean> method) {
        for(int i = 0; i < TILES; i++) {
            boolean b1 = i == 0 || i == TILES - 1;
            for(int j = 0; j < TILES; j++) {
                boolean b2 = j == 0 || j == TILES - 1;
                boolean onBorder = b1 || b2;
                method.accept(i * TILE_SIZE, j * TILE_SIZE, onBorder);
            }
        }
    }

    @Override
    public void forEachActivePosition(BiConsumer<Integer, Integer> method) {
        forEachPosition(method);
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

    @Override
    public void markShouldUpdate(int x, int z) {
        markShouldUpdate();
    }

    @Override
    public void clearShouldUpdate() {
        shouldUpdate = false;
    }

    @Override
    public boolean shouldUpdate() {
        return shouldUpdate;
    }

    @Override
    public void markActive(int x, int z) {
        int tx = x >> TILE_SIZE_BITS;
        int tz = z >> TILE_SIZE_BITS;

        boolean xMin = tx == 0;
        boolean zMin = tz == TILES - 1;
        boolean xMax = tx == 0;
        boolean zMax = tz == TILES - 1;

        active[4] = true;
        if(xMin) {
            active[3] = true;
            if(zMin) active[0] = true;
            if(zMax) active[6] = true;
        }
        if(xMax) {
            active[5] = true;
            if(zMin) active[2] = true;
            if(zMax) active[8] = true;
        }
        if(zMin) active[1] = true;
        if(zMax) active[7] = true;
    }

    @Override
    public void clearActive() {
        for(int i = 0; i < 9; i++) {
            active[i] = false;
        }
    }

    @Override
    public void markUpdatesFromActivity() {
        for(int k = 0; k < 9; k++) {
            if(active[k]) {
                this.firmamentRegion.firmament.markShouldUpdate(x + xOffset[k], z + zOffset[k]);
            }
        }
    }

    public long getPosAsLong() {
        int srx = this.x >> FirmamentRegion.SUBREGION_SIZE_BITS;
        int srz = this.z >> FirmamentRegion.SUBREGION_SIZE_BITS;
        return ((long)srx & 4294967295L) | (((long)srz) << 32);
    }

    public byte[] getAsByteArray(int[][] target) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(DATA_SIZE_BYTES);
        boolean multipleValues = false;
        byte val = (byte)target[0][0];
        for(int i = 0; i < FirmamentSubRegion.TILES; i++) {
            for(int j = 0; j < FirmamentSubRegion.TILES; j++) {
                byte b = (byte)target[i][j];
                byteBuffer.put(b);
                if(b != val) {
                    multipleValues = true;
                }
            }
        }
        if(multipleValues) {
            return byteBuffer.array();
        } else {
            ByteBuffer buffer = ByteBuffer.allocate(1);
            buffer.put(val);
            return buffer.array();
        }
    }

    public void readFromData(FirmamentSubRegionData firmamentSubRegionData) {
        //this.readFromByteArray(firmamentSubRegionData.displacementData, this.displacement, 0xF);
        //this.readFromByteArray(firmamentSubRegionData.velocityData, this.velocity, 0xF);
        this.readFromByteArray(firmamentSubRegionData.damageData, this.damage, 0x7);
        //this.readFromByteArray(firmamentSubRegionData.dripData, this.drip, 0x7);
        checkDamage();
    }

    public void readFromByteArray(byte[] byteArray, int[][] targetArray, int mask) {
        if(byteArray == null) {
            return;
        }

        if(byteArray.length == DATA_SIZE_BYTES) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(DATA_SIZE_BYTES);
            byteBuffer.put(byteArray);
            byteBuffer.position(0);
            for (int i = 0; i < FirmamentSubRegion.TILES; i++) {
                for (int j = 0; j < FirmamentSubRegion.TILES; j++) {
                    targetArray[i][j] = byteBuffer.get() & mask;
                }
            }
        } else if(byteArray.length == 1) {
            int val = byteArray[0] & mask;
            for (int i = 0; i < FirmamentSubRegion.TILES; i++) {
                for (int j = 0; j < FirmamentSubRegion.TILES; j++) {
                    targetArray[i][j] = val;
                }
            }
        }
    }

    public void flushUpdates() {
        if(this.pendingClientUpdate) {
            this.pendingClientUpdate = false;
            World world = this.firmamentRegion.firmament.getWorld();
            if(world instanceof ServerWorld serverWorld) {
                SubRegionPos subRegionPos = SubRegionPos.fromWorldCoords(this.x, this.z);
                RegionPos regionPos = RegionPos.fromSubRegion(subRegionPos);

                List<ServerPlayerEntity> nearbyPlayers = new ArrayList<>();
                serverWorld.getPlayers().forEach(serverPlayerEntity -> {
                    if(((FirmamentWatcher)serverPlayerEntity).operation_starcleave$getWatchedRegions().watchedRegions.contains(regionPos.id)) {
                        nearbyPlayers.add(serverPlayerEntity);
                    }
                });

                if(!nearbyPlayers.isEmpty()) {
                    FirmamentSubRegionData data = new FirmamentSubRegionData(this);
                    nearbyPlayers.forEach(serverPlayerEntity -> ServerPlayNetworking.send(serverPlayerEntity, new UpdateFirmamentSubRegionS2CPacket(subRegionPos.id, data)));
                }
            }
        }
    }

    public void checkDamage() {
        for(int i = 0; i < TILES; i++) {
            for(int j = 0; j < TILES; j++) {
                if(damage[i][j] != 0) {
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
}
