package phanastrae.operation_starcleave.item;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import phanastrae.operation_starcleave.OperationStarcleave;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class OperationStarcleaveArmorMaterials {

    public static final ArmorMaterial BISMUTH = create(id("bismuth"), Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
        map.put(ArmorItem.Type.BOOTS, 2);
        map.put(ArmorItem.Type.LEGGINGS, 5);
        map.put(ArmorItem.Type.CHESTPLATE, 6);
        map.put(ArmorItem.Type.HELMET, 2);
        map.put(ArmorItem.Type.BODY, 5);
    }), 20, SoundEvents.ARMOR_EQUIP_IRON, 1.5F, 0.0F, () -> Ingredient.of(Items.IRON_INGOT));

    public static Holder<ArmorMaterial> BISMUTH_ENTRY;

    public static void init(OperationStarcleave.HolderRegisterHelper<ArmorMaterial> hrh) {
        BISMUTH_ENTRY = hrh.register("bismuth", BISMUTH);
    }

    private static ResourceLocation id(String path) {
        return OperationStarcleave.id(path);
    }

    private static ArmorMaterial create(
            ResourceLocation location,
            EnumMap<ArmorItem.Type, Integer> defense,
            int enchantmentValue,
            Holder<SoundEvent> equipSound,
            float toughness,
            float knockbackResistance,
            Supplier<Ingredient> repairIngredient
    ) {
        List<ArmorMaterial.Layer> list = List.of(new ArmorMaterial.Layer(location));
        return create(defense, enchantmentValue, equipSound, toughness, knockbackResistance, repairIngredient, list);
    }

    private static ArmorMaterial create(
            EnumMap<ArmorItem.Type, Integer> defense,
            int enchantmentValue,
            Holder<SoundEvent> equipSound,
            float toughness,
            float knockbackResistance,
            Supplier<Ingredient> repairIngridient,
            List<ArmorMaterial.Layer> layers
    ) {
        EnumMap<ArmorItem.Type, Integer> enummap = new EnumMap<>(ArmorItem.Type.class);

        for (ArmorItem.Type armoritem$type : ArmorItem.Type.values()) {
            enummap.put(armoritem$type, defense.get(armoritem$type));
        }

        return new ArmorMaterial(enummap, enchantmentValue, equipSound, repairIngridient, layers, toughness, knockbackResistance);
    }
}
