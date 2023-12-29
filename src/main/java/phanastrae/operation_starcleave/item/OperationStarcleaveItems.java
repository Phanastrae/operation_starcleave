package phanastrae.operation_starcleave.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;

public class OperationStarcleaveItems {

    public static Item NETHERITE_PUMPKIN = new NetheritePumpkinItem(OperationStarcleaveBlocks.NETHERITE_PUMPKIN, new FabricItemSettings().rarity(Rarity.UNCOMMON));

    public static final Item STARCLEAVER_GOLEM_SPAWN_EGG = new SpawnEggItem(OperationStarcleaveEntityTypes.STARCLEAVER_GOLEM, 0x292725, 0x61eddf, new Item.Settings());

    public static Item FIRMAMENT_MANIPULATOR = new FirmamentManipulatorItem(new FabricItemSettings().rarity(Rarity.EPIC).maxCount(1));

    public static final ItemGroup OPERATION_STARCLEAVE_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(NETHERITE_PUMPKIN))
            .displayName(Text.translatable("itemGroup.operation_starcleave.group"))
            .build();

    public static void init() {
        Registry.register(Registries.ITEM_GROUP, OperationStarcleave.id("operation_starcleave"), OPERATION_STARCLEAVE_GROUP);

        registerWithIG(NETHERITE_PUMPKIN, "netherite_pumpkin");
        registerWithIG(STARCLEAVER_GOLEM_SPAWN_EGG, "starcleaver_golem_spawn_egg");
        registerWithIG(FIRMAMENT_MANIPULATOR, "firmament_manipulator");

        addToVanillaItemGroups();
    }

    public static void addToVanillaItemGroups() {
        addItemToGroupBefore(NETHERITE_PUMPKIN, ItemGroups.COMBAT, Items.TURTLE_HELMET);
        addItemToGroupAfter(NETHERITE_PUMPKIN, ItemGroups.FUNCTIONAL, Items.DRAGON_HEAD);

        addItemToGroup(STARCLEAVER_GOLEM_SPAWN_EGG, ItemGroups.SPAWN_EGGS);

        addItemToGroup(FIRMAMENT_MANIPULATOR, ItemGroups.OPERATOR);
    }

    public static <T extends Item> void registerWithIG(T item, String name) {
        register(item, name);
        addItemToGroup(item, RegistryKey.of(Registries.ITEM_GROUP.getKey(), OperationStarcleave.id("operation_starcleave")));
    }

    public static <T extends Item> void register(T item, String name) {
        Registry.register(Registries.ITEM, OperationStarcleave.id(name), item);
    }

    public static <T extends Item> void addItemToGroup(T newItem, RegistryKey<ItemGroup> group) {
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.add(newItem));
    }

    public static <T extends Item> void addItemToGroupBefore(T newItem, RegistryKey<ItemGroup> group, T beforeFirst) {
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.addBefore(beforeFirst, newItem));
    }

    public static <T extends Item> void addItemToGroupAfter(T newItem, RegistryKey<ItemGroup> group, T afterLast) {
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.addAfter(afterLast, newItem));
    }
}
