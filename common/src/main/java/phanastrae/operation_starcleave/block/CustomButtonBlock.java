package phanastrae.operation_starcleave.block;

import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class CustomButtonBlock extends ButtonBlock {
    public CustomButtonBlock(BlockSetType type, int ticksToStayPressed, Properties properties) {
        super(type, ticksToStayPressed, properties);
    }
}
