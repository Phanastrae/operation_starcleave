package phanastrae.operation_starcleave.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class OperationStarcleaveDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(BlockTagProvider::new);
		pack.addProvider(ItemTagProvider::new);
		pack.addProvider(EntityTypeTagProvider::new);

		pack.addProvider(BlockLootTableProvider::new);
		pack.addProvider(EntityLootTableProvider::new);

		pack.addProvider(RecipeProvider::new);
		pack.addProvider(AdvancementProvider::new);

		pack.addProvider(ModelProvider::new);
	}
}
