package phanastrae.operation_starcleave.item.enchantment;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.AddValue;
import net.minecraft.world.level.block.Block;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.item.tag.OperationStarcleaveItemTags;

public class OperationStarcleaveEnchantments {
    public static final ResourceKey<Enchantment> STOCKPILE = key("stockpile");

    public static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<DamageType> damageTypeGetter = context.lookup(Registries.DAMAGE_TYPE);
        HolderGetter<Enchantment> enchantmentGetter = context.lookup(Registries.ENCHANTMENT);
        HolderGetter<Item> itemGetter = context.lookup(Registries.ITEM);
        HolderGetter<Block> blockGetter = context.lookup(Registries.BLOCK);

        register(
                context,
                STOCKPILE,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        itemGetter.getOrThrow(OperationStarcleaveItemTags.STOCKPILE_ENCHANTABLE),
                                        5,
                                        4,
                                        Enchantment.dynamicCost(1, 13),
                                        Enchantment.dynamicCost(14, 13),
                                        2,
                                        EquipmentSlotGroup.MAINHAND,
                                        EquipmentSlotGroup.OFFHAND
                                )
                        )
                        .withSpecialEffect(OperationStarcleaveEnchantmentEffectComponents.STORAGE_BONUS, new AddValue(LevelBasedValue.perLevel(1.0F)))
        );
    }

    private static void register(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
        context.register(key, builder.build(key.location()));
    }

    private static ResourceKey<Enchantment> key(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, OperationStarcleave.id(name));
    }
}
