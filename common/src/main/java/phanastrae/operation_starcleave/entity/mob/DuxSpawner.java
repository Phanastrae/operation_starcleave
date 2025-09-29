package phanastrae.operation_starcleave.entity.mob;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;
import phanastrae.operation_starcleave.world.firmament.Firmament;

import java.util.List;
import java.util.Optional;

public class DuxSpawner implements CustomSpawner {
    private int nextTick;

    @Override
    public int tick(ServerLevel level, boolean spawnEnemies, boolean spawnFriendlies) {
        if(!spawnEnemies && !spawnFriendlies) {
            return 0;
        } else {
            RandomSource randomSource = level.random;
            this.nextTick--;
            if (this.nextTick > 0) {
                return 0;
            } else {
                this.nextTick += (50 + randomSource.nextInt(30)) * 20;

                int addedMobs = 0;
                // shuffle to avoid bias due to limited dux count
                List<ServerPlayer> players = level.players();
                List<ServerPlayer> shuffled = Util.shuffledCopy(players.toArray(new ServerPlayer[0]), randomSource);
                for (ServerPlayer serverPlayer : shuffled) {
                    if(serverPlayer.isSpectator()) {
                        continue;
                    }

                    BlockPos blockPos = serverPlayer.blockPosition();
                    // check player can (roughly) see the sky
                    if (skyExposureAtLeast(level, blockPos, 6)) {
                        // do not spawn if too many duxes nearby already
                        AABB checkBox = new AABB(blockPos).inflate(128, 512, 128);
                        List<SubcaelicDuxEntity> nearbyDuxes = level.getEntitiesOfClass(SubcaelicDuxEntity.class, checkBox);
                        if(nearbyDuxes.size() > 2) {
                            continue;
                        }

                        // try to spawn dux
                        Optional<BlockPos> spawnPosOptional = getPositionForDuxSpawn(level, blockPos, randomSource);
                        if(spawnPosOptional.isPresent()) {
                            BlockPos spawnPos = spawnPosOptional.get();

                            SubcaelicDuxEntity dux = OperationStarcleaveEntityTypes.SUBCAELIC_DUX.create(level);
                            if (dux != null) {
                                dux.moveTo(spawnPos, 0.0F, 0.0F);
                                dux.finalizeSpawn(level, level.getCurrentDifficultyAt(blockPos), MobSpawnType.NATURAL, null);
                                level.addFreshEntityWithPassengers(dux);
                                addedMobs++;
                            }
                        }
                    }
                }

                return addedMobs;
            }
        }
    }

    public boolean skyExposureAtLeast(Level level, BlockPos blockPos, int threshold) {
        // check skylight is at least the threshold value, or that the position is on the surface for dimensions without skylight
        if(level.dimensionType().hasSkyLight()) {
            return level.getBrightness(LightLayer.SKY, blockPos) >= threshold;
        } else {
            return threshold == 0 || level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockPos.getX(), blockPos.getZ()) <= blockPos.getY();
        }
    }

    public Optional<BlockPos> getPositionForDuxSpawn(ServerLevel level, BlockPos pos, RandomSource randomSource) {
        Firmament firmament = Firmament.fromLevel(level);
        if(firmament == null) {
            return Optional.empty();
        }

        int maxAttempts = 7;
        for(int i = 0; i < maxAttempts; i++) {
            int d = 45 + randomSource.nextIntBetweenInclusive((maxAttempts - i) * 5, (maxAttempts - i) * 8);
            int x = pos.getX() + randomSource.nextIntBetweenInclusive(-d, d);
            int z = pos.getZ() + randomSource.nextIntBetweenInclusive(-d, d);

            int damage = firmament.getDamage(x, z);
            if(damage < 7) {
                continue;
            }

            // find the highest position nearby the dux's spawn point, or the player's position if that is higher
            int height = pos.getY();
            for(int dx = -4;  dx <= 4; dx++) {
                for(int dz = -4; dz <= 4; dz++) {
                    int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
                    if(height < y) {
                        height = y;
                    }
                }
            }
            int y = height + randomSource.nextIntBetweenInclusive(21, 35);

            // do not spawn close to or above firmament
            if(y + 16 > firmament.getY()) {
                continue;
            }

            BlockPos tryPos = new BlockPos(x, y, z);

            // check distance to nearest player
            Player player = level.getNearestPlayer(tryPos.getX() + 0.5, tryPos.getY() + 0.5, tryPos.getZ() + 0.5, -1.0, false);
            if(player == null) {
                continue;
            }
            double distSqr = player.distanceToSqr(tryPos.getCenter());
            if(distSqr < 24 * 24) {
                // too close
                continue;
            } else if(distSqr > 128 * 128) {
                // too far
                continue;
            }

            // check position is unobstructed
            if(!level.noCollision(OperationStarcleaveEntityTypes.SUBCAELIC_DUX.getSpawnAABB((double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5))) {
                continue;
            }

            return Optional.of(tryPos);
        }
        return Optional.empty();
    }
}
