package phanastrae.operation_starcleave.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import phanastrae.operation_starcleave.data.worldgen.features.OperationStarcleaveVegetationFeatures;
import phanastrae.operation_starcleave.item.enchantment.OperationStarcleaveEnchantments;

public class OperationStarcleaveDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(FluidTagProvider::new);
        BlockTagProvider btp = pack.addProvider(BlockTagProvider::new);
        pack.addProvider(((output, registriesFuture) -> new ItemTagProvider(output, registriesFuture, btp)));
        pack.addProvider(EntityTypeTagProvider::new);
        pack.addProvider(DamageTypeTagProvider::new);
        pack.addProvider(EnchantmentTagProvider::new);

        pack.addProvider(BlockLootTableProvider::new);
        pack.addProvider(EntityLootTableProvider::new);

        pack.addProvider(RecipeProvider::new);
        pack.addProvider(AdvancementProvider::new);

        pack.addProvider(ModelProvider::new);

        // dynamic registries
        addProvider(pack, Registries.CONFIGURED_FEATURE);
        addProvider(pack, Registries.ENCHANTMENT);
    }

    private <T> void addProvider(FabricDataGenerator.Pack pack, ResourceKey<? extends Registry<T>> registryKey) {
        pack.addProvider(((output, completableFuture) -> new DynamicRegistryProvider<>(output, completableFuture, registryKey)));
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder
                .add(Registries.CONFIGURED_FEATURE, OperationStarcleaveDataGenerator::bootstrapConfiguredFeatures)
                .add(Registries.ENCHANTMENT, OperationStarcleaveEnchantments::bootstrap);
    }

    private static void bootstrapConfiguredFeatures(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        OperationStarcleaveVegetationFeatures.bootstrap(context);
    }
}
