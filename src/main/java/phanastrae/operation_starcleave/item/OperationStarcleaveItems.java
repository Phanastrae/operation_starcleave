package phanastrae.operation_starcleave.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import phanastrae.operation_starcleave.OperationStarcleave;

public class OperationStarcleaveItems {

    public static Item FIRMAMENT_MANIPULATOR = new FirmamentManipulatorItem(new FabricItemSettings());

    public static void init() {
        register(FIRMAMENT_MANIPULATOR, "firmament_manipulator");
    }

    public static <T extends Item> void register(T item, String name) {
        Registry.register(Registries.ITEM, OperationStarcleave.id(name), item);
    }
}
