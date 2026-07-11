package phanastrae.operation_starcleave.world.feature;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import phanastrae.operation_starcleave.OperationStarcleave;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class OperationStarcleaveFeatures {
    private static final Map<ResourceLocation, Feature<?>> UNREGISTERED_FEATURES = new HashMap<>();

    public static final Feature<BlockStateConfiguration> ASTERUBBLE_BOULDER = register("asterubble_boulder", new AsterubbleBoulder(BlockStateConfiguration.CODEC));

    private static <C extends FeatureConfiguration> Feature<C> register(String key, Feature<C> feature) {
        return register(OperationStarcleave.id(key), feature);
    }

    private static <C extends FeatureConfiguration, F extends Feature<C>> F register(ResourceLocation location, F feature) {
        UNREGISTERED_FEATURES.put(location, feature);
        return feature;
    }

    public static void init(BiConsumer<ResourceLocation, Feature<?>> r) {
        UNREGISTERED_FEATURES.forEach(r);
        UNREGISTERED_FEATURES.clear();
    }
}
