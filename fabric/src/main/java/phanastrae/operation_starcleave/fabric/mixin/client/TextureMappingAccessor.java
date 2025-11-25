package phanastrae.operation_starcleave.fabric.mixin.client;

import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.Set;

@Mixin(TextureMapping.class)
public interface TextureMappingAccessor {
    @Accessor
    Map<TextureSlot, ResourceLocation> getSlots();

    @Accessor
    Set<TextureSlot> getForcedSlots();
}
