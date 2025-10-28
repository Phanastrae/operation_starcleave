package phanastrae.operation_starcleave.mixin;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.block.BlessedBedBlock;
import phanastrae.operation_starcleave.entity.mob.DuxSpawner;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.FirmamentHolder;
import phanastrae.operation_starcleave.world.firmament.ServerFirmamentRegionManager;
import phanastrae.operation_starcleave.world.starbleach.Starbleach;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements FirmamentHolder {

    @Mutable
    @Shadow
    @Final
    private List<CustomSpawner> customSpawners;

    @Shadow
    public abstract List<ServerPlayer> players();

    @Unique
    private Firmament operation_starcleave$firmament;

    @Override
    public Firmament operation_starcleave$getFirmament() {
        return this.operation_starcleave$firmament;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void operation_starcleave$onInit(MinecraftServer server, Executor workerExecutor, LevelStorageSource.LevelStorageAccess session, ServerLevelData properties, ResourceKey worldKey, LevelStem dimensionOptions, ChunkProgressListener worldGenerationProgressListener, boolean debugWorld, long seed, List spawners, boolean shouldTickTime, RandomSequences randomSequencesState, CallbackInfo ci) {
        this.operation_starcleave$firmament = new Firmament((Level) (Object) this, new ServerFirmamentRegionManager((ServerLevel) (Object) this));

        List<CustomSpawner> extraSpawners = List.of(new DuxSpawner());
        List<CustomSpawner> newSpawners = new ArrayList<>(extraSpawners);
        newSpawners.addAll(extraSpawners);
        this.customSpawners = ImmutableList.copyOf(newSpawners);
    }

    // insert just before tickBlocks (i.e. randomTicks)
    @Inject(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", ordinal = 1))
    private void operation_starcleave$starbleachChunk(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci) {
        Starbleach.starbleachChunk((ServerLevel) (Object) this, chunk, randomTickSpeed);
    }

    // Stop Precipitation beneath damaged firmament
    @Inject(method = "tickPrecipitation", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/level/ServerLevel;getHeightmapPos(Lnet/minecraft/world/level/levelgen/Heightmap$Types;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/BlockPos;", shift = At.Shift.AFTER))
    private void operation_starcleave$blockIceAndSnowTick(CallbackInfo ci, @Local(ordinal = 1) LocalRef<BlockPos> refTopPosition) {
        Firmament firmament = this.operation_starcleave$getFirmament();

        BlockPos topPos = refTopPosition.get();
        int damage = firmament.getDamage(topPos.getX(), topPos.getZ());
        if (damage >= 5) {
            refTopPosition.set(new BlockPos(topPos.getX(), firmament.getY(), topPos.getZ()));
        }
    }

    @Inject(method = "wakeUpAllPlayers", at = @At("HEAD"))
    private void operation_starcleave$buffAllBlessedSleepers(CallbackInfo ci) {
        // give all sleepers blessed sleep effects
        this.players().stream().filter(Player::isSleeping).toList().forEach(BlessedBedBlock::attemptBlessedSleep);
    }
}
