package phanastrae.operation_starcleave.mixin.client.renderer.extra_layers;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.client.render.extras_baking.CustomRendererWrappedModel;
import phanastrae.operation_starcleave.client.render.extras_baking.Iridescence;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mixin(ModelBakery.class)
public class ModelBakeryMixin {

    @Shadow
    @Final
    private Map<ModelResourceLocation, BakedModel> bakedTopLevelModels;

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/DefaultedRegistry;keySet()Ljava/util/Set;"))
    private Set<ResourceLocation> operation_starcleave$wrapSet(DefaultedRegistry<Item> instance, Operation<Set<ResourceLocation>> original) {
        Set<ResourceLocation> og = original.call(instance);
        Set<ResourceLocation> modified = new HashSet<>(og);
        for (Item item : Iridescence.IRIDESCENCE_ITEM_ID_MAP.keySet()) {
            modified.add(BuiltInRegistries.ITEM.getKey(item).withSuffix("_iridescence"));
        }

        return Collections.unmodifiableSet(modified);
    }

    @Inject(method = "bakeModels", at = @At("RETURN"))
    private void operation_starcleave$afterBake(ModelBakery.TextureGetter textureGetter, CallbackInfo ci) {
        // replace iridescent item models with wrapped models
        for (Item item : Iridescence.IRIDESCENCE_ITEM_ID_MAP.keySet()) {
            ModelResourceLocation mrl = ModelResourceLocation.inventory(BuiltInRegistries.ITEM.getKey(item));
            if (this.bakedTopLevelModels.containsKey(mrl)) {
                BakedModel model = this.bakedTopLevelModels.get(mrl);
                CustomRendererWrappedModel wrappedModel = new CustomRendererWrappedModel(model);
                this.bakedTopLevelModels.put(mrl, wrappedModel);
            }
        }
    }
}
