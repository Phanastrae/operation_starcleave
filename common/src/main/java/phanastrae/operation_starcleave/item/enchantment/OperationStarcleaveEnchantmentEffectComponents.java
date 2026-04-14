package phanastrae.operation_starcleave.item.enchantment;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import phanastrae.operation_starcleave.OperationStarcleave;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;

public class OperationStarcleaveEnchantmentEffectComponents {
    private static final Map<ResourceLocation, DataComponentType<?>> UNREGISTERED_EFFECTS = new HashMap<>();

    public static final DataComponentType<EnchantmentValueEffect> STORAGE_BONUS = register(
            "storage_bonus", builder -> builder.persistent(EnchantmentValueEffect.CODEC)
    );

    public static void init(BiConsumer<ResourceLocation, DataComponentType<?>> r) {
        UNREGISTERED_EFFECTS.forEach(r);
        UNREGISTERED_EFFECTS.clear();
    }

    private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> operator) {
        DataComponentType<T> type = operator.apply(DataComponentType.builder()).build();
        UNREGISTERED_EFFECTS.put(OperationStarcleave.id(name), type);
        return type;
    }
}
