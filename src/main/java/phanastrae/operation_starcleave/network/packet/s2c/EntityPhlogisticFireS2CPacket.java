package phanastrae.operation_starcleave.network.packet.s2c;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import phanastrae.operation_starcleave.network.packet.OperationStarcleavePacketTypes;

public class EntityPhlogisticFireS2CPacket implements FabricPacket {

    public final int id;
    public final boolean onPhlogisticFire;

    public EntityPhlogisticFireS2CPacket(Entity entity, boolean onPhlogisticFire) {
        this.id = entity.getId();
        this.onPhlogisticFire = onPhlogisticFire;
    }

    public EntityPhlogisticFireS2CPacket(PacketByteBuf buf) {
        this.id = buf.readInt();
        this.onPhlogisticFire = buf.readBoolean();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(id);
        buf.writeBoolean(onPhlogisticFire);
    }

    @Override
    public PacketType<?> getType() {
        return OperationStarcleavePacketTypes.ENTITY_PHLOGISTIC_FIRE_PACKET_S2C;
    }
}
