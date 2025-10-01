package phanastrae.operation_starcleave.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.world.BossEventExtras;

import java.util.UUID;

public record ClientboundBossEventExtrasPayload(UUID id, Operation operation) implements CustomPacketPayload {
    // this is essentially just the vanilla ClientboundBossEventPacket but with different settings and as a payload
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundBossEventExtrasPayload> PACKET_CODEC = CustomPacketPayload.codec(ClientboundBossEventExtrasPayload::write, ClientboundBossEventExtrasPayload::new);
    public static final CustomPacketPayload.Type<ClientboundBossEventExtrasPayload> PACKET_ID = new CustomPacketPayload.Type<>(OperationStarcleave.id("boss_event_extras"));

    public ClientboundBossEventExtrasPayload(FriendlyByteBuf buf) {
        this(buf.readUUID(), readOperation(buf));
    }

    private static Operation readOperation(FriendlyByteBuf buf) {
        OperationType type = buf.readEnum(OperationType.class);
        return type.reader.decode(buf);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(this.id);
        buf.writeEnum(operation.getType());
        this.operation.write(buf);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

    public void dispatch(Handler handler) {
        this.operation.dispatch(handler);
    }

    static int encodeBonusProperties(boolean isMini) {
        int i = 0;
        if (isMini) {
            i |= 1;
        }

        return i;
    }

    public static ClientboundBossEventExtrasPayload createAddBonusPacket(BossEventExtras extras) {
        return new ClientboundBossEventExtrasPayload(
                extras.getEvent().getId(),
                new AddBonusOperation(extras)
        );
    }

    public static ClientboundBossEventExtrasPayload createUpdateBonusPropertiesPacket(BossEventExtras extras) {
        return new ClientboundBossEventExtrasPayload(
                extras.getEvent().getId(),
                new UpdateBonusPropertiesOperation(
                        extras.isMini()
                )
        );
    }

    public interface Handler {
        default void add(
                boolean isMini
        ) {
        }

        default void updateBonusProperties(boolean isMini) {
        }
    }

    interface Operation {
        OperationType getType();

        void dispatch(Handler handler);

        void write(FriendlyByteBuf buffer);
    }

    enum OperationType {
        ADD_BONUS(AddBonusOperation::new),
        UPDATE_BONUS_PROPERTIES(UpdateBonusPropertiesOperation::new);

        final StreamDecoder<FriendlyByteBuf, Operation> reader;

        OperationType(StreamDecoder<FriendlyByteBuf, Operation> reader) {
            this.reader = reader;
        }
    }

    static class AddBonusOperation implements Operation {
        private final boolean isMini;

        AddBonusOperation(BossEventExtras extras) {
            this.isMini = extras.isMini();
        }

        private AddBonusOperation(FriendlyByteBuf buffer) {
            int i = buffer.readUnsignedByte();
            this.isMini = (i & 1) > 0;
        }

        @Override
        public OperationType getType() {
            return OperationType.ADD_BONUS;
        }

        @Override
        public void dispatch(Handler handler) {
            handler.add(this.isMini);
        }

        @Override
        public void write(FriendlyByteBuf buffer) {
            buffer.writeByte(encodeBonusProperties(this.isMini));
        }
    }

    static class UpdateBonusPropertiesOperation implements Operation {
        private final boolean isMini;

        UpdateBonusPropertiesOperation(boolean isMini) {
            this.isMini = isMini;
        }

        private UpdateBonusPropertiesOperation(FriendlyByteBuf buffer) {
            int i = buffer.readUnsignedByte();
            this.isMini = (i & 1) > 0;
        }

        @Override
        public OperationType getType() {
            return OperationType.UPDATE_BONUS_PROPERTIES;
        }

        @Override
        public void dispatch(Handler handler) {
            handler.updateBonusProperties(this.isMini);
        }

        @Override
        public void write(FriendlyByteBuf buffer) {
            buffer.writeByte(encodeBonusProperties(this.isMini));
        }
    }
}
