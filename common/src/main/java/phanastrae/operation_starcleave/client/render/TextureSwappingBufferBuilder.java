package phanastrae.operation_starcleave.client.render;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class TextureSwappingBufferBuilder extends BufferBuilder {

    private final TextureAtlasSprite fromSprite;
    private final TextureAtlasSprite toSprite;

    public TextureSwappingBufferBuilder(ByteBufferBuilder buffer, VertexFormat.Mode mode, VertexFormat format, TextureAtlasSprite fromSprite, TextureAtlasSprite toSprite) {
        super(buffer, mode, format);
        this.fromSprite = fromSprite;
        this.toSprite = toSprite;
    }

    public float transformU(float u) {
        float localU = (u - this.fromSprite.getU0()) / (this.fromSprite.getU1() - this.fromSprite.getU0());
        return this.toSprite.getU0() + (this.toSprite.getU1() - this.toSprite.getU0()) * localU;
    }

    public float transformV(float v) {
        float localV = (v - this.fromSprite.getV0()) / (this.fromSprite.getV1() - this.fromSprite.getV0());
        return this.toSprite.getV0() + (this.toSprite.getV1() - this.toSprite.getV0()) * localV;
    }

    @Override
    public void putBulkData(PoseStack.Pose pose, BakedQuad quad, float[] brightness, float red, float green, float blue, float alpha, int[] lightmap, int packedOverlay, boolean readAlpha) {
        // TODO this should probably be getting baked before hand somewhere
        int[] alteredVertices = quad.getVertices().clone();
        for (int i = 0; i < 4; i++) {
            int offset = i * 8;

            float u = Float.intBitsToFloat(alteredVertices[offset + 4]);
            float v = Float.intBitsToFloat(alteredVertices[offset + 5]);

            float newU = transformU(u);
            float newV = transformV(v);

            alteredVertices[offset + 4] = Float.floatToIntBits(newU);
            alteredVertices[offset + 5] = Float.floatToIntBits(newV);
        }

        BakedQuad alteredQuad = new BakedQuad(alteredVertices, quad.getTintIndex(), quad.getDirection(), quad.getSprite(), quad.isShade());
        super.putBulkData(pose, alteredQuad, brightness, red, green, blue, alpha, lightmap, packedOverlay, readAlpha);
    }
}
