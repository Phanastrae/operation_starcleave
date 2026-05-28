package phanastrae.operation_starcleave.network.packet.s2c;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.world.BossEventExtras;

import java.util.UUID;

public record BossEventExtrasPayload(UUID id, Operation operation) implements CustomPacketPayload {
    // this is essentially just the vanilla ClientboundBossEventPacket but with different settings and as a payload
    public static final CustomPacketPayload.Type<BossEventExtrasPayload> TYPE = new CustomPacketPayload.Type<>(OperationStarcleave.id("boss_event_extras"));
    public static final StreamCodec<RegistryFriendlyByteBuf, BossEventExtrasPayload> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            BossEventExtrasPayload::id,
            Operation.STREAM_CODEC,
            BossEventExtrasPayload::operation,
            BossEventExtrasPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void dispatch(Handler handler) {
        this.operation.dispatch(handler);
    }

    private static int encodeBonusProperties(boolean isMini) {
        int i = 0;
        if (isMini) {
            i |= 1;
        }

        return i;
    }

    public static BossEventExtrasPayload createAddBonusPacket(BossEventExtras extras) {
        return new BossEventExtrasPayload(
                extras.getEvent().getId(),
                new AddBonusOperation(extras)
        );
    }

    public static BossEventExtrasPayload createUpdateBonusPropertiesPacket(BossEventExtras extras) {
        return new BossEventExtrasPayload(
                extras.getEvent().getId(),
                new UpdateBonusPropertiesOperation(
                        extras.isMini()
                )
        );
    }

    public interface Handler {
        default void add(boolean isMini) {
        }

        default void updateBonusProperties(boolean isMini) {
        }
    }

    private interface Operation {
        StreamCodec<RegistryFriendlyByteBuf, Operation> STREAM_CODEC = StreamCodec.of(
                Operation::write,
                Operation::read
        );

        private static void write(FriendlyByteBuf buf, Operation operation) {
            buf.writeEnum(operation.getType());
            operation.write(buf);
        }

        private static Operation read(FriendlyByteBuf buf) {
            OperationType type = buf.readEnum(OperationType.class);
            return type.reader.decode(buf);
        }

        OperationType getType();

        void dispatch(Handler handler);

        void write(FriendlyByteBuf buffer);
    }

    private enum OperationType {
        ADD_BONUS(AddBonusOperation::new),
        UPDATE_BONUS_PROPERTIES(UpdateBonusPropertiesOperation::new);

        final StreamDecoder<FriendlyByteBuf, Operation> reader;

        OperationType(StreamDecoder<FriendlyByteBuf, Operation> reader) {
            this.reader = reader;
        }
    }

    private static class AddBonusOperation implements Operation {
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

    private static class UpdateBonusPropertiesOperation implements Operation {
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
