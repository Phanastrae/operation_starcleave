package phanastrae.operation_starcleave.fabric.client.renderer;

import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.material.ShadeMode;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Supplier;

public class ForceAoBakedModel extends ForwardingBakedModel {
    private static final Renderer RENDERER = RendererAccess.INSTANCE.getRenderer();
    private static final RenderMaterial FORCE_AO_MATERIAL = RENDERER.materialFinder().shadeMode(ShadeMode.VANILLA).ambientOcclusion(TriState.TRUE).find();

    public ForceAoBakedModel(BakedModel wrapped) {
        super(wrapped);
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        // use the normal emitBlockQuads method, but with ambientOcclusion set as TriState.TRUE
        QuadEmitter emitter = context.getEmitter();

        for (int i = 0; i <= ModelHelper.NULL_FACE_ID; i++) {
            final Direction cullFace = ModelHelper.faceFromIndex(i);

            if (!context.hasTransform() && context.isFaceCulled(cullFace)) {
                // Skip entire quad list if possible.
                continue;
            }

            List<BakedQuad> quads = this.wrapped.getQuads(state, cullFace, randomSupplier.get());
            int count = quads.size();

            for (int j = 0; j < count; j++) {
                BakedQuad quad = quads.get(j);
                emitter.fromVanilla(quad, FORCE_AO_MATERIAL, cullFace);
                emitter.emit();
            }
        }
    }
}
