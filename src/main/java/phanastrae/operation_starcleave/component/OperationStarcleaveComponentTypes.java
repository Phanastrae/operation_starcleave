package phanastrae.operation_starcleave.component;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.component.type.StarbleachComponent;

import java.util.function.BiConsumer;

public class OperationStarcleaveComponentTypes {

    public static final DataComponentType<StarbleachComponent> STARBLEACH_COMPONENT =
            DataComponentType.<StarbleachComponent>builder().persistent(StarbleachComponent.CODEC).networkSynchronized(StarbleachComponent.PACKET_CODEC).cacheEncoding().build();

    public static void init(BiConsumer<ResourceLocation, DataComponentType<?>> r) {
        r.accept(id("starbleach"), STARBLEACH_COMPONENT);
    }

    public static ResourceLocation id(String key) {
        return OperationStarcleave.id(key);
    }
}
