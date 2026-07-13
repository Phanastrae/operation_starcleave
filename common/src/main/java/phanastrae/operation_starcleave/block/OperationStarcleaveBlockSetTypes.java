package phanastrae.operation_starcleave.block;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class OperationStarcleaveBlockSetTypes {
    public static final BlockSetType STARBLEACHED =
            new BlockSetType(
                    "operation_starcleave:starbleached",
                    true,
                    true,
                    false,
                    BlockSetType.PressurePlateSensitivity.MOBS,
                    SoundType.STONE,
                    SoundEvents.CHERRY_WOOD_DOOR_CLOSE,
                    SoundEvents.CHERRY_WOOD_DOOR_OPEN,
                    SoundEvents.CHERRY_WOOD_TRAPDOOR_CLOSE,
                    SoundEvents.CHERRY_WOOD_TRAPDOOR_OPEN,
                    SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF,
                    SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON,
                    SoundEvents.STONE_BUTTON_CLICK_OFF,
                    SoundEvents.STONE_BUTTON_CLICK_ON
            );

    public static final BlockSetType STARFLAKED_BISMUTH =
            new BlockSetType(
                    "operation_starcleave:starflaked_bismuth",
                    false,
                    false,
                    false,
                    BlockSetType.PressurePlateSensitivity.EVERYTHING,
                    SoundType.COPPER,
                    SoundEvents.COPPER_DOOR_CLOSE,
                    SoundEvents.COPPER_DOOR_OPEN,
                    SoundEvents.COPPER_TRAPDOOR_CLOSE,
                    SoundEvents.COPPER_TRAPDOOR_OPEN,
                    SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF,
                    SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON,
                    SoundEvents.STONE_BUTTON_CLICK_OFF,
                    SoundEvents.STONE_BUTTON_CLICK_ON
            );

    public static final BlockSetType STARTOUCHED = new BlockSetType("operation_starcleave:startouched");
}
