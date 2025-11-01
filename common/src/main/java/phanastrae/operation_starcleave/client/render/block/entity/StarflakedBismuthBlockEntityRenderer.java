package phanastrae.operation_starcleave.client.render.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.block.entity.StarflakedBismuthBlockEntity;
import phanastrae.operation_starcleave.client.render.OperationStarcleaveRenderTypes;
import phanastrae.operation_starcleave.client.render.TextureSwappingVertexConsumer;

import java.util.function.Function;

public class StarflakedBismuthBlockEntityRenderer implements BlockEntityRenderer<StarflakedBismuthBlockEntity> {

    private final BlockRenderDispatcher blockRenderer;

    public StarflakedBismuthBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    public void render(StarflakedBismuthBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Level level = blockEntity.getLevel();

        if (level != null) {
            BlockState state = blockEntity.getBlockState();
            BlockPos pos = blockEntity.getBlockPos();
            BakedModel model = this.blockRenderer.getBlockModel(state);
            RandomSource randomSource = RandomSource.create();
            long seed = state.getSeed(pos);

            this.blockRenderer.getModelRenderer().tesselateBlock(
                    level,
                    model,
                    state,
                    pos,
                    poseStack,
                    bufferSource.getBuffer(RenderType.cutout()),
                    true,
                    randomSource,
                    seed,
                    packedOverlay
            );

            Function<ResourceLocation, TextureAtlasSprite> func = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS);
            TextureAtlasSprite bismuthSprite = func.apply(OperationStarcleave.id("block/starflaked_bismuth_block"));
            TextureAtlasSprite normalSprite = func.apply(OperationStarcleave.id("block/starflaked_bismuth_block_normal"));

            this.blockRenderer.getModelRenderer().tesselateBlock(
                    level,
                    model,
                    state,
                    pos,
                    poseStack,
                    new TextureSwappingVertexConsumer(bufferSource.getBuffer(OperationStarcleaveRenderTypes.getIridescence()), bismuthSprite, normalSprite),
                    true,
                    randomSource,
                    seed,
                    packedOverlay
            );
        }
    }

    @Override
    public int getViewDistance() {
        return 1024;
    }
}
