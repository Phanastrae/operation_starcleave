package phanastrae.operation_starcleave.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;

import java.util.concurrent.CompletableFuture;

public class ItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        getOrCreateTagBuilder(ItemTags.DAMPENS_VIBRATIONS).add(
                OperationStarcleaveItems.BLESSED_CLOTH_BLOCK,
                OperationStarcleaveItems.BLESSED_CLOTH_CARPET,
                OperationStarcleaveItems.BLESSED_CLOTH_CURTAIN
        );

        getOrCreateTagBuilder(ItemTags.VILLAGER_PLANTABLE_SEEDS).add(
                OperationStarcleaveItems.BISREED_ROOT
        );
    }
}
