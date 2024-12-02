package phanastrae.operation_starcleave.client.render.block.entity;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.BedPart;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.LightmapCoordinatesRetriever;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.block.entity.BlessedBedBlockEntity;

public class BlessedBedBlockEntityRenderer implements BlockEntityRenderer<BlessedBedBlockEntity> {
    public static final SpriteIdentifier sprite = new SpriteIdentifier(TexturedRenderLayers.BEDS_ATLAS_TEXTURE, OperationStarcleave.id("entity/bed/blessed"));

    private final ModelPart bedHead;
    private final ModelPart bedFoot;

    public BlessedBedBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.bedHead = ctx.getLayerModelPart(EntityModelLayers.BED_HEAD);
        this.bedFoot = ctx.getLayerModelPart(EntityModelLayers.BED_FOOT);
    }

    public static TexturedModelData getHeadTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("main", ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 6.0F), ModelTransform.NONE);
        modelPartData.addChild(
                EntityModelPartNames.LEFT_LEG,
                ModelPartBuilder.create().uv(50, 6).cuboid(0.0F, 6.0F, 0.0F, 3.0F, 3.0F, 3.0F),
                ModelTransform.rotation((float) (Math.PI / 2), 0.0F, (float) (Math.PI / 2))
        );
        modelPartData.addChild(
                EntityModelPartNames.RIGHT_LEG,
                ModelPartBuilder.create().uv(50, 18).cuboid(-16.0F, 6.0F, 0.0F, 3.0F, 3.0F, 3.0F),
                ModelTransform.rotation((float) (Math.PI / 2), 0.0F, (float) Math.PI)
        );
        return TexturedModelData.of(modelData, 64, 64);
    }

    public static TexturedModelData getFootTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("main", ModelPartBuilder.create().uv(0, 22).cuboid(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 6.0F), ModelTransform.NONE);
        modelPartData.addChild(
                EntityModelPartNames.LEFT_LEG,
                ModelPartBuilder.create().uv(50, 0).cuboid(0.0F, 6.0F, -16.0F, 3.0F, 3.0F, 3.0F),
                ModelTransform.rotation((float) (Math.PI / 2), 0.0F, 0.0F)
        );
        modelPartData.addChild(
                EntityModelPartNames.RIGHT_LEG,
                ModelPartBuilder.create().uv(50, 12).cuboid(-16.0F, 6.0F, -16.0F, 3.0F, 3.0F, 3.0F),
                ModelTransform.rotation((float) (Math.PI / 2), 0.0F, (float) (Math.PI * 3.0 / 2.0))
        );
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void render(BlessedBedBlockEntity bedBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        SpriteIdentifier spriteIdentifier = sprite;
        World world = bedBlockEntity.getWorld();
        if (world != null) {
            BlockState blockState = bedBlockEntity.getCachedState();
            DoubleBlockProperties.PropertySource<? extends BedBlockEntity> propertySource = DoubleBlockProperties.toPropertySource(
                    BlockEntityType.BED,
                    BedBlock::getBedPart,
                    BedBlock::getOppositePartDirection,
                    ChestBlock.FACING,
                    blockState,
                    world,
                    bedBlockEntity.getPos(),
                    (worldx, pos) -> false
            );
            int k = ((Int2IntFunction)propertySource.apply(new LightmapCoordinatesRetriever())).get(i);
            this.renderPart(
                    matrixStack,
                    vertexConsumerProvider,
                    blockState.get(BedBlock.PART) == BedPart.HEAD ? this.bedHead : this.bedFoot,
                    (Direction)blockState.get(BedBlock.FACING),
                    spriteIdentifier,
                    k,
                    j,
                    false
            );
        } else {
            this.renderPart(matrixStack, vertexConsumerProvider, this.bedHead, Direction.SOUTH, spriteIdentifier, i, j, false);
            this.renderPart(matrixStack, vertexConsumerProvider, this.bedFoot, Direction.SOUTH, spriteIdentifier, i, j, true);
        }
    }

    private void renderPart(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            ModelPart part,
            Direction direction,
            SpriteIdentifier sprite,
            int light,
            int overlay,
            boolean isFoot
    ) {
        matrices.push();
        matrices.translate(0.0F, 0.5625F, isFoot ? -1.0F : 0.0F);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
        matrices.translate(0.5F, 0.5F, 0.5F);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F + direction.asRotation()));
        matrices.translate(-0.5F, -0.5F, -0.5F);
        VertexConsumer vertexConsumer = sprite.getVertexConsumer(vertexConsumers, RenderLayer::getEntitySolid);
        part.render(matrices, vertexConsumer, light, overlay);
        matrices.pop();
    }
}
