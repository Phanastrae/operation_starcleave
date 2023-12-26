package phanastrae.operation_starcleave.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.BlockItem;

import java.util.UUID;

public class NetheritePumpkinItem extends BlockItem {

    private static final UUID HELMET_UUID = UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150");
    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    public NetheritePumpkinItem(Block block, Settings settings) {
        super(block, settings);

        ArmorMaterials materials = ArmorMaterials.NETHERITE;

        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(
                EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(HELMET_UUID, "Armor modifier", 12, EntityAttributeModifier.Operation.ADDITION)
        );
        builder.put(
                EntityAttributes.GENERIC_ARMOR_TOUGHNESS,
                new EntityAttributeModifier(HELMET_UUID, "Armor toughness", materials.getToughness(), EntityAttributeModifier.Operation.ADDITION)
        );
        builder.put(
                EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,
                new EntityAttributeModifier(HELMET_UUID, "Armor knockback resistance", materials.getKnockbackResistance(), EntityAttributeModifier.Operation.ADDITION)
        );

        this.attributeModifiers = builder.build();
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.HEAD ? this.attributeModifiers : super.getAttributeModifiers(slot);
    }
}
