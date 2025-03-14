package phanastrae.operation_starcleave.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.operation_starcleave.block.StellarFarmlandBlock;
import phanastrae.operation_starcleave.world.FirmamentMobSpawning;
import phanastrae.operation_starcleave.world.firmament.Firmament;

@Mixin(NaturalSpawner.class)
public class NaturalSpawnerMixin {

    @Inject(method = "mobsAt", at = @At("HEAD"), cancellable = true)
    private static void operation_starcleave$starlitAreasSpawnOverride(ServerLevel level, StructureManager structureManager, ChunkGenerator generator, MobCategory category, BlockPos pos, Holder<Biome> biome, CallbackInfoReturnable<WeightedRandomList<MobSpawnSettings.SpawnerData>> cir) {
        Firmament firmament = Firmament.fromLevel(level);
        if (firmament != null) {
            if (StellarFarmlandBlock.isStarlit(level, pos, firmament)) {
                switch(category) {
                    case MONSTER:
                        cir.setReturnValue(FirmamentMobSpawning.STARLIGHT_MONSTERS);
                        break;
                    case CREATURE:
                    case AMBIENT:
                        cir.setReturnValue(FirmamentMobSpawning.NOSPAWNS);
                        break;
                }
            }
        }
    }
}
