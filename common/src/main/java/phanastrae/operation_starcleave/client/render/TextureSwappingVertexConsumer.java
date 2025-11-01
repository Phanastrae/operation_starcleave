package phanastrae.operation_starcleave.client.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class TextureSwappingVertexConsumer implements VertexConsumer {

    private final VertexConsumer wrapped;
    private final TextureAtlasSprite fromSprite;
    private final TextureAtlasSprite toSprite;

    public TextureSwappingVertexConsumer(VertexConsumer wrapped, TextureAtlasSprite fromSprite, TextureAtlasSprite toSprite) {
        this.wrapped = wrapped;
        this.fromSprite = fromSprite;
        this.toSprite = toSprite;
    }

    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        return this.wrapped.addVertex(x, y, z);
    }

    @Override
    public VertexConsumer setColor(int red, int green, int blue, int alpha) {
        return this.wrapped.setColor(red, green, blue, alpha);
    }

    @Override
    public VertexConsumer setUv(float u, float v) {
        float localU = (u - this.fromSprite.getU0()) / (this.fromSprite.getU1() - this.fromSprite.getU0());
        float localV = (v - this.fromSprite.getV0()) / (this.fromSprite.getV1() - this.fromSprite.getV0());

        float newU = this.toSprite.getU0() + (this.toSprite.getU1() - this.toSprite.getU0()) * localU;
        float newV = this.toSprite.getV0() + (this.toSprite.getV1() - this.toSprite.getV0()) * localV;

        return this.wrapped.setUv(newU, newV);
    }

    @Override
    public VertexConsumer setUv1(int u, int v) {
        return this.wrapped.setUv1(u, v);
    }

    @Override
    public VertexConsumer setUv2(int u, int v) {
        return this.wrapped.setUv2(u, v);
    }

    @Override
    public VertexConsumer setNormal(float normalX, float normalY, float normalZ) {
        return this.wrapped.setNormal(normalX, normalY, normalZ);
    }
}
