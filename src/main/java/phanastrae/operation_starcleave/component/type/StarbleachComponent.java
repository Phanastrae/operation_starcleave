package phanastrae.operation_starcleave.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import phanastrae.operation_starcleave.item.StarbleachCoating;

import java.util.function.Consumer;

public record StarbleachComponent(boolean showInTooltip) implements TooltipAppender {
    public static final Codec<StarbleachComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(Codec.BOOL.optionalFieldOf("show_in_tooltip", true).forGetter(StarbleachComponent::showInTooltip))
                    .apply(instance, StarbleachComponent::new)
    );
    public static final PacketCodec<ByteBuf, StarbleachComponent> PACKET_CODEC = PacketCodecs.BOOL
            .xmap(StarbleachComponent::new, StarbleachComponent::showInTooltip);

    public StarbleachComponent() {
        this(true);
    }

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type) {
        if(this.showInTooltip) {
            tooltip.accept(StarbleachCoating.getText());
        }
    }
}
