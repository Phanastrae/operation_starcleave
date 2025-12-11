package phanastrae.operation_starcleave.mixin.client.renderer.extra_layers;

import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.client.render.extras_baking.Iridescence;

@Mixin(BlockStateModelLoader.class)
public abstract class BlockStateModelLoaderMixin {

    @Shadow
    protected abstract void loadBlockStateDefinitions(ResourceLocation blockStateId, StateDefinition<Block, BlockState> stateDefenition);

    @Inject(method = "loadAllBlockStates", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V"))
    private void operation_starcleave$loadBonusModels(CallbackInfo ci) {
        for (Block block : Iridescence.IRIDESCENCE_BLOCK_ID_MAP.keySet()) {
            this.loadBlockStateDefinitions(block.builtInRegistryHolder().key().location().withSuffix("_iridescence"), block.getStateDefinition());
        }
    }
}
