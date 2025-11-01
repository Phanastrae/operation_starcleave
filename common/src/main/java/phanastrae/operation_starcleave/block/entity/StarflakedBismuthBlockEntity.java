package phanastrae.operation_starcleave.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class StarflakedBismuthBlockEntity extends BlockEntity {

    public StarflakedBismuthBlockEntity(BlockPos pos, BlockState state) {
        super(OperationStarcleaveBlockEntityTypes.STARFLAKED_BISMUTH_BLOCK, pos, state);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
