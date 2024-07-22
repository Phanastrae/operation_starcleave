package phanastrae.operation_starcleave.item;

import com.google.common.base.Suppliers;
import net.minecraft.block.Block;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class NetheritePumpkinItem extends BlockItem implements Equipment {

    protected final ArmorItem.Type type;
    protected final RegistryEntry<ArmorMaterial> material;

    private final Supplier<AttributeModifiersComponent> attributeModifiers;

    public NetheritePumpkinItem(Block block, Settings settings) {
        super(block, settings);
        this.material = ArmorMaterials.NETHERITE;
        this.type = ArmorItem.Type.HELMET;
        this.attributeModifiers = Suppliers.memoize(
                () -> {
                    int protection = 5;
                    float toughness = material.value().toughness();
                    float knockbackResistance = material.value().knockbackResistance();

                    AttributeModifiersComponent.Builder builder = AttributeModifiersComponent.builder();
                    AttributeModifierSlot attributeModifierSlot = AttributeModifierSlot.forEquipmentSlot(this.type.getEquipmentSlot());
                    Identifier identifier = Identifier.ofVanilla("armor." + this.type.getName());

                    builder.add(
                            EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(identifier, protection, EntityAttributeModifier.Operation.ADD_VALUE), attributeModifierSlot
                    );
                    builder.add(
                            EntityAttributes.GENERIC_ARMOR_TOUGHNESS,
                            new EntityAttributeModifier(identifier, toughness, EntityAttributeModifier.Operation.ADD_VALUE),
                            attributeModifierSlot
                    );
                    if (knockbackResistance > 0.0F) {
                        builder.add(
                                EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,
                                new EntityAttributeModifier(identifier, knockbackResistance, EntityAttributeModifier.Operation.ADD_VALUE),
                                attributeModifierSlot
                        );
                    }

                    return builder.build();
                }
        );
    }

    @Override
    public AttributeModifiersComponent getAttributeModifiers() {
        return this.attributeModifiers.get();
    }

    public ArmorItem.Type getType() {
        return this.type;
    }

    public RegistryEntry<ArmorMaterial> getMaterial() {
        return this.material;
    }

    @Override
    public EquipmentSlot getSlotType() {
        return this.type.getEquipmentSlot();
    }

    @Override
    public RegistryEntry<SoundEvent> getEquipSound() {
        return this.getMaterial().value().equipSound();
    }
}
