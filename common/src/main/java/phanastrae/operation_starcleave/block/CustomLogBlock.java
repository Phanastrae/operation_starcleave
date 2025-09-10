package phanastrae.operation_starcleave.block;

import net.minecraft.world.level.block.RotatedPillarBlock;

public abstract class CustomLogBlock extends RotatedPillarBlock {
    // on neoforge, add IBlockExtension to this class for log stripping

    public CustomLogBlock(Properties properties) {
        super(properties);
    }
}
