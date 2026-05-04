package phanastrae.operation_starcleave.world.firmament.actor;

import net.minecraft.world.level.Level;
import org.joml.Math;
import phanastrae.operation_starcleave.entity.NuclearStormcloudEntity;
import phanastrae.operation_starcleave.world.OperationStarcleaveGameRules;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.FirmamentUpdater;

import java.util.Random;

import static phanastrae.operation_starcleave.world.firmament.FirmamentSubRegion.TILE_SIZE;

public class FirmamentShatterActor extends FirmamentActor {

    public FirmamentShatterActor(Firmament firmament, int originX, int originZ, float dx, float dz, float damagePotential) {
        this(firmament, originX, originZ, dx, dz, damagePotential, originX, originZ, damagePotential);
    }

    public FirmamentShatterActor(Firmament firmament, int originX, int originZ, float dx, float dz, float damagePotential, int trueOriginX, int trueOriginZ, float initialDamagePotential) {
        super(firmament, originX, originZ);
        this.damagePotential = damagePotential;
        this.initialDamagePotential = initialDamagePotential;
        this.dx = dx;
        this.dz = dz;
        this.trueOriginX = trueOriginX;
        this.trueOriginZ = trueOriginZ;
    }

    public float damagePotential;
    public float initialDamagePotential;

    public int initialDelay = 0;

    public int trueOriginX;
    public int trueOriginZ;
    public float dx;
    public float dz;

    public Random random = new Random();

    @Override
    public void tick() {
        if (this.damagePotential < 0.0001f) {
            discard();
            return;
        }

        if (this.initialDelay > 0) {
            this.initialDelay--;
            return;
        }

        if (random.nextFloat() > 0.9f) {
            split();
            discard();
            return;
        }

        double theta = random.nextFloat() * Math.PI * 2;

        float ddx = (float) Math.cos(theta);
        float ddz = (float) Math.sin(theta);
        float dSqr = dx * dx + dz * dz;
        float dNorm = dSqr == 0 ? 0 : 1 / Math.sqrt(dSqr);
        float dot = (dx * ddx + dz * ddz) * dNorm;

        float f = dot * dot * dot;

        int n = random.nextInt(4, 12);
        for (int i = 0; i < n; i++) {
            float dFdx = FirmamentUpdater.dFdxBigDamage(firmament, originX + (int) dx, originZ + (int) dz);
            float dFdz = FirmamentUpdater.dFdzBigDamage(firmament, originX + (int) dx, originZ + (int) dz);
            float dFSqr = dFdx * dFdx + dFdz * dFdz;
            float dFnorm = dFSqr == 0 ? 0 : 1 / Math.sqrt(dFSqr);
            float dot2 = (ddx * dFdx + ddz * dFdz) * dFnorm;

            float g = (1 - dot2 * dot2 * dot2) / 2;

            dx += ddx * f * g * TILE_SIZE;
            dz += ddz * f * g * TILE_SIZE;

            int idx = originX + (int) dx;
            int idz = originZ + (int) dz;

            float addDamage = Math.clamp(1, 15, damagePotential * damagePotential / initialDamagePotential);
            this.damagePotential -= addDamage;

            int damage = firmament.getDamage(idx, idz);
            if (damage < 7) {
                int d = Math.clamp(0, 7, damage + (int) addDamage);
                firmament.setDamage(idx, idz, d);

                if (d == 7) {
                    this.trySpawnStormcloud(this.firmament.getLevel(), 2 + idx, 2 + idz);
                }
            }

            FirmamentUpdater.forEachNeighbor((nx, nz, nWeight) -> {
                float addDamage2 = addDamage * 0.5f * nWeight;
                int damage2 = firmament.getDamage(idx + nx, idz + nz);
                if (damage2 < 7) {
                    int d2 = Math.clamp(0, 7, damage2 + (int) addDamage2);
                    firmament.setDamage(idx + nx, idz + nz, d2);
                    if (d2 == 7) {
                        this.trySpawnStormcloud(this.firmament.getLevel(), 2 + idx + nx, 2 + idz + nz);
                    }
                }
            });
        }
    }

    public void split() {
        int hdx = (int) (dx * 0.9f);
        int hdz = (int) (dz * 0.9f);

        int ddp = (int) (damagePotential * (random.nextFloat() * 0.6f + 0.3f));

        float dx2 = dx - hdx;
        float dz2 = dz - hdz;

        FirmamentActor actor1 = new FirmamentShatterActor(firmament, originX + hdx, originZ + hdz, dx2, dz2, damagePotential - ddp, trueOriginX, trueOriginZ, initialDamagePotential);
        FirmamentActor actor2 = new FirmamentShatterActor(firmament, originX + hdx, originZ + hdz, dx2, dz2, ddp, trueOriginX, trueOriginZ, initialDamagePotential);

        firmament.addActor(actor1);
        firmament.addActor(actor2);
    }

    public void trySpawnStormcloud(Level level, int x, int z) {
        if (level.getGameRules().getBoolean(OperationStarcleaveGameRules.SPAWN_FRACTURE_BYPRODUCTS)) {
            if (this.random.nextInt(25) == 0) {
                int y = this.firmament.getY();
                NuclearStormcloudEntity stormcloud = new NuclearStormcloudEntity(level, x, y, z);
                level.addFreshEntity(stormcloud);
            }
        }
    }
}
