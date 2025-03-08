package phanastrae.operation_starcleave.mixin.client;

import net.minecraft.client.model.HorseModel;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HorseRenderer;
import net.minecraft.world.entity.animal.horse.Horse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.client.render.entity.layers.PegasusWingsLayer;

@Mixin(HorseRenderer.class)
public abstract class HorseRendererMixin extends AbstractHorseRenderer<Horse, HorseModel<Horse>> {
    public HorseRendererMixin(EntityRendererProvider.Context context, HorseModel<Horse> model, float scale) {
        super(context, model, scale);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void operation_starcleave$addPegasusWingsLayer(EntityRendererProvider.Context context, CallbackInfo ci) {
        this.addLayer(new PegasusWingsLayer<>(this, context.getModelSet()));
    }
}
