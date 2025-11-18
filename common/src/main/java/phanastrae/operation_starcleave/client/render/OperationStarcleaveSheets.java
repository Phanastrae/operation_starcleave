package phanastrae.operation_starcleave.client.render;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;

public class OperationStarcleaveSheets {

    private static final RenderType IRIDESCENCE_BLOCK_SHEET = OperationStarcleaveRenderTypes.entityIridescence(TextureAtlas.LOCATION_BLOCKS);

    public static RenderType iridescenceBlockSheet() {
        return IRIDESCENCE_BLOCK_SHEET;
    }
}
