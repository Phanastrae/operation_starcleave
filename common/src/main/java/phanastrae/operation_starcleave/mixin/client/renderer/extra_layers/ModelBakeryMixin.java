package phanastrae.operation_starcleave.mixin.client.renderer.extra_layers;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import phanastrae.operation_starcleave.client.render.extras_baking.Iridescence;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Mixin(ModelBakery.class)
public class ModelBakeryMixin {

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/DefaultedRegistry;keySet()Ljava/util/Set;"))
    private Set<ResourceLocation> h(DefaultedRegistry<Item> instance, Operation<Set<ResourceLocation>> original) {
        Set<ResourceLocation> og = original.call(instance);
        Set<ResourceLocation> modified = new HashSet<>(og);
        for (Item item : Iridescence.IRIDESCENCE_ITEM_ID_MAP.keySet()) {
            modified.add(BuiltInRegistries.ITEM.getKey(item).withSuffix("_iridescence"));
        }

        return Collections.unmodifiableSet(modified);
    }
}
