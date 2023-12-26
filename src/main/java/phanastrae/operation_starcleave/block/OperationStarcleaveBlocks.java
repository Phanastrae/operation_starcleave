package phanastrae.operation_starcleave.block;

import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import phanastrae.operation_starcleave.OperationStarcleave;

public class OperationStarcleaveBlocks {

    public static Block NETHERITE_PUMPKIN = new NetheritePumpkinBlock(
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.BLACK)
                    .requiresTool()
                    .strength(10.0F, 1200.0F)
                    .sounds(BlockSoundGroup.NETHERITE)
                    .allowsSpawning(Blocks::always)
                    .pistonBehavior(PistonBehavior.DESTROY)
    );

    public static void init() {
        register(NETHERITE_PUMPKIN, "netherite_pumpkin");
    }

    public static <T extends Block> void register(T item, String name) {
        Registry.register(Registries.BLOCK, OperationStarcleave.id(name), item);
    }
}
