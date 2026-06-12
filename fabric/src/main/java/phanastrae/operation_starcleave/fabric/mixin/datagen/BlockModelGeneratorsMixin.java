package phanastrae.operation_starcleave.fabric.mixin.datagen;

import com.google.common.collect.ImmutableMap;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.fabric.data.ModelProvider;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Mixin(BlockModelGenerators.class)
public abstract class BlockModelGeneratorsMixin {

    @Mutable
    @Shadow
    @Final
    Map<Block, TexturedModel> texturedModels;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void operation_starcleave$adjustBlockModelGenerators(Consumer blockStateOutput, BiConsumer modelOutput, Consumer skippedAutoModelsOutput, CallbackInfo ci) {
        this.texturedModels = ImmutableMap.<Block, TexturedModel>builder()
                .putAll(this.texturedModels)
                .putAll(ModelProvider.CUSTOM_TEXTURED_MODELS)
                .build();
    }
}
