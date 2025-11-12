package phanastrae.operation_starcleave.client.duck;

import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.client.render.extras_baking.SectionExtrasRebuildQueue;

public interface LevelRendererExtrasDuck {
    @Nullable
    SectionExtrasRebuildQueue operation_starcleave$getRebuildQueue();
}
