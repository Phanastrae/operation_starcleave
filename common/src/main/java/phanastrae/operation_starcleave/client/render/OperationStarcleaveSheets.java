package phanastrae.operation_starcleave.client.render;

import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;

import java.util.function.Function;

public class OperationStarcleaveSheets {

    private static final Function<Integer, RenderType> IRIDESCENCE_BLOCK_SHEET = Util.memoize(iridescenceId -> OperationStarcleaveRenderTypes.entityIridescence(TextureAtlas.LOCATION_BLOCKS, iridescenceId));

    public static RenderType iridescenceBlockSheet(int iridescenceId) {
        return IRIDESCENCE_BLOCK_SHEET.apply(iridescenceId);
    }
}
