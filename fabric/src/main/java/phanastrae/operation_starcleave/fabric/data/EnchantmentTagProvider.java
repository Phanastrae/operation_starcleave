package phanastrae.operation_starcleave.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import phanastrae.operation_starcleave.item.enchantment.tag.OperationStarcleaveEnchantmentTags;

import java.util.concurrent.CompletableFuture;

public class EnchantmentTagProvider extends FabricTagProvider<Enchantment> {

    public EnchantmentTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, Registries.ENCHANTMENT, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        // starcleave tags
        getOrCreateTagBuilder(OperationStarcleaveEnchantmentTags.SUPPORTS_BISMUTH_BLASTER)
                .addOptionalTag(OperationStarcleaveEnchantmentTags.PRIMARY_BISMUTH_BLASTER);

        getOrCreateTagBuilder(OperationStarcleaveEnchantmentTags.PRIMARY_BISMUTH_BLASTER)
                .add(
                        Enchantments.QUICK_CHARGE
                );
    }
}
