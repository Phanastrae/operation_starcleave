package phanastrae.operation_starcleave.client.render.extras_baking;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.SectionBufferBuilderPool;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.client.render.OperationStarcleaveRenderTypes;
import phanastrae.operation_starcleave.mixin.client.accessor.SectionBufferBuilderPoolAccessor;

import java.util.ArrayList;
import java.util.List;

public class ExtrasSectionBufferBuilderPack extends SectionBufferBuilderPack {
    public static final List<RenderType> RENDER_TYPES = List.of(OperationStarcleaveRenderTypes.getIridescence());
    public static final int TOTAL_EXTRA_BUFFERS_SIZE = RENDER_TYPES.stream().mapToInt(RenderType::bufferSize).sum();

    // check SectionBufferBuilderPackMixin for more functionality

    public static SectionBufferBuilderPool allocateExtras(int bufferCount) {
        double maxMemoryProportion = 0.05;
        int bufferCap = Math.max(1, (int)((double)Runtime.getRuntime().maxMemory() * maxMemoryProportion) / ExtrasSectionBufferBuilderPack.TOTAL_EXTRA_BUFFERS_SIZE);
        int finalBufferCount = Math.max(1, Math.min(bufferCount, bufferCap));
        List<SectionBufferBuilderPack> list = new ArrayList<>(finalBufferCount);

        try {
            for (int i = 0; i < finalBufferCount; i++) {
                list.add(new ExtrasSectionBufferBuilderPack());
            }
        } catch (OutOfMemoryError outofmemoryerror) {
            OperationStarcleave.LOGGER.warn("Allocated only {}/{} buffers for extras", list.size(), finalBufferCount);
            int l = Math.min(list.size() * 2 / 3, list.size() - 1);

            for (int i = 0; i < l; i++) {
                list.remove(list.size() - 1).close();
            }
        }

        return SectionBufferBuilderPoolAccessor.invokeInit(list);
    }
}
