package phanastrae.operation_starcleave.world.firmament.data;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import phanastrae.operation_starcleave.world.firmament.FirmamentSubRegion;

public class FirmamentSubRegionData {
    public static final StreamCodec<RegistryFriendlyByteBuf, FirmamentSubRegionData> STREAM_CODEC = StreamCodec.of(
            (buf, data) -> data.write(buf),
            FirmamentSubRegionData::new
    );

    public final byte[] damageData;

    public FirmamentSubRegionData(FirmamentSubRegion firmamentSubRegion) {
        this.damageData = FirmamentSubRegion.getAsByteArray(firmamentSubRegion.damage);
    }

    public FirmamentSubRegionData(FriendlyByteBuf packetByteBuf) {
        this.damageData = packetByteBuf.readByteArray();
    }

    public void write(FriendlyByteBuf packetByteBuf) {
        packetByteBuf.writeByteArray(this.damageData);
    }
}
