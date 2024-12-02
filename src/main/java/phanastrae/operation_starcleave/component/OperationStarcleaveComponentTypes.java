package phanastrae.operation_starcleave.component;

import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.component.type.StarbleachComponent;

public class OperationStarcleaveComponentTypes {

    public static final ComponentType<StarbleachComponent> STARBLEACH_COMPONENT =
            ComponentType.<StarbleachComponent>builder().codec(StarbleachComponent.CODEC).packetCodec(StarbleachComponent.PACKET_CODEC).cache().build();

    public static void init() {
        Registry.register(Registries.DATA_COMPONENT_TYPE, id("starbleach"), STARBLEACH_COMPONENT);
    }

    public static Identifier id(String key) {
        return OperationStarcleave.id(key);
    }
}
