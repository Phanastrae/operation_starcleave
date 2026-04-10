package phanastrae.operation_starcleave.item.enchantment.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import phanastrae.operation_starcleave.OperationStarcleave;

public class OperationStarcleaveEnchantmentTags {
    public static final TagKey<Enchantment> SUPPORTS_BISMUTH_BLASTER = of("supports/bismuth_blaster");
    public static final TagKey<Enchantment> PRIMARY_BISMUTH_BLASTER = of("primary/bismuth_blaster");

    private static TagKey<Enchantment> of(String id) {
        return TagKey.create(Registries.ENCHANTMENT, OperationStarcleave.id(id));
    }
}
