package phanastrae.operation_starcleave.item;

import com.google.common.base.Suppliers;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class NetheritePumpkinItem extends BlockItem implements Equipable {

    protected final ArmorItem.Type type;
    protected final Holder<ArmorMaterial> material;

    private final Supplier<ItemAttributeModifiers> attributeModifiers;

    public NetheritePumpkinItem(Block block, Properties settings) {
        super(block, settings);
        this.material = ArmorMaterials.NETHERITE;
        this.type = ArmorItem.Type.HELMET;
        this.attributeModifiers = Suppliers.memoize(
                () -> {
                    int protection = 5;
                    float toughness = material.value().toughness();
                    float knockbackResistance = material.value().knockbackResistance();

                    ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
                    EquipmentSlotGroup attributeModifierSlot = EquipmentSlotGroup.bySlot(this.type.getSlot());
                    ResourceLocation identifier = ResourceLocation.withDefaultNamespace("armor." + this.type.getName());

                    builder.add(
                            Attributes.ARMOR, new AttributeModifier(identifier, protection, AttributeModifier.Operation.ADD_VALUE), attributeModifierSlot
                    );
                    builder.add(
                            Attributes.ARMOR_TOUGHNESS,
                            new AttributeModifier(identifier, toughness, AttributeModifier.Operation.ADD_VALUE),
                            attributeModifierSlot
                    );
                    if (knockbackResistance > 0.0F) {
                        builder.add(
                                Attributes.KNOCKBACK_RESISTANCE,
                                new AttributeModifier(identifier, knockbackResistance, AttributeModifier.Operation.ADD_VALUE),
                                attributeModifierSlot
                        );
                    }

                    return builder.build();
                }
        );
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers() {
        return this.attributeModifiers.get();
    }

    public ArmorItem.Type getType() {
        return this.type;
    }

    public Holder<ArmorMaterial> getMaterial() {
        return this.material;
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return this.type.getSlot();
    }

    @Override
    public Holder<SoundEvent> getEquipSound() {
        return this.getMaterial().value().equipSound();
    }
}
