package phanastrae.operation_starcleave.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import phanastrae.operation_starcleave.item.StarbleachCoating;

import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

public record StarbleachComponent(boolean showInTooltip) implements TooltipProvider {
    public static final Codec<StarbleachComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(Codec.BOOL.optionalFieldOf("show_in_tooltip", true).forGetter(StarbleachComponent::showInTooltip))
                    .apply(instance, StarbleachComponent::new)
    );
    public static final StreamCodec<ByteBuf, StarbleachComponent> PACKET_CODEC = ByteBufCodecs.BOOL
            .map(StarbleachComponent::new, StarbleachComponent::showInTooltip);

    public StarbleachComponent() {
        this(true);
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltip, TooltipFlag type) {
        if(this.showInTooltip) {
            tooltip.accept(StarbleachCoating.getText());
        }
    }
}
