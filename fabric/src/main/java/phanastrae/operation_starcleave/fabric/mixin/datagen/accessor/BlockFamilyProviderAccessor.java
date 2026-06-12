package phanastrae.operation_starcleave.fabric.mixin.datagen.accessor;

import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(BlockModelGenerators.BlockFamilyProvider.class)
public interface BlockFamilyProviderAccessor {

    @Accessor
    Set<Block> getSkipGeneratingModelsFor();
}
