package phanastrae.operation_starcleave.block;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Equipable;

public class EquipableNetheritePumpkinBlock extends NetheritePumpkinBlock implements Equipable {

    public EquipableNetheritePumpkinBlock(Properties settings) {
        super(settings);
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }
}
