package phanastrae.operation_starcleave.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.concurrent.CompletableFuture;

public class DynamicRegistryProvider<T> extends FabricDynamicRegistryProvider {

    private final ResourceKey<? extends Registry<T>> registryKey;

    public DynamicRegistryProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture, ResourceKey<? extends Registry<T>> registryKey) {
        super(output, registriesFuture);
        this.registryKey = registryKey;
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        entries.addAll(registries.lookupOrThrow(this.registryKey));
    }

    @Override
    public String getName() {
        return "Dynamic Registry: " + registryKey.toString();
    }
}
