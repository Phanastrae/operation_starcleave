package phanastrae.operation_starcleave.fabric.mixin.client;

import net.minecraft.data.models.model.ModelTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(ModelTemplate.class)
public interface ModelTemplateAccessor {
    @Accessor
    Optional<String> getSuffix();
}
