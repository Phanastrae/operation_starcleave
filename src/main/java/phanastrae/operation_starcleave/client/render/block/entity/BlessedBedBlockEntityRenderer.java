package phanastrae.operation_starcleave.client.render.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.block.entity.BlessedBedBlockEntity;

public class BlessedBedBlockEntityRenderer implements BlockEntityRenderer<BlessedBedBlockEntity> {
    public static final Material sprite = new Material(Sheets.BED_SHEET, OperationStarcleave.id("entity/bed/blessed"));

    private final ModelPart bedHead;
    private final ModelPart bedFoot;

    public BlessedBedBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.bedHead = ctx.bakeLayer(ModelLayers.BED_HEAD);
        this.bedFoot = ctx.bakeLayer(ModelLayers.BED_FOOT);
    }

    public static LayerDefinition getHeadTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 6.0F), PartPose.ZERO);
        modelPartData.addOrReplaceChild(
                PartNames.LEFT_LEG,
                CubeListBuilder.create().texOffs(50, 6).addBox(0.0F, 6.0F, 0.0F, 3.0F, 3.0F, 3.0F),
                PartPose.rotation((float) (Math.PI / 2), 0.0F, (float) (Math.PI / 2))
        );
        modelPartData.addOrReplaceChild(
                PartNames.RIGHT_LEG,
                CubeListBuilder.create().texOffs(50, 18).addBox(-16.0F, 6.0F, 0.0F, 3.0F, 3.0F, 3.0F),
                PartPose.rotation((float) (Math.PI / 2), 0.0F, (float) Math.PI)
        );
        return LayerDefinition.create(modelData, 64, 64);
    }

    public static LayerDefinition getFootTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 22).addBox(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 6.0F), PartPose.ZERO);
        modelPartData.addOrReplaceChild(
                PartNames.LEFT_LEG,
                CubeListBuilder.create().texOffs(50, 0).addBox(0.0F, 6.0F, -16.0F, 3.0F, 3.0F, 3.0F),
                PartPose.rotation((float) (Math.PI / 2), 0.0F, 0.0F)
        );
        modelPartData.addOrReplaceChild(
                PartNames.RIGHT_LEG,
                CubeListBuilder.create().texOffs(50, 12).addBox(-16.0F, 6.0F, -16.0F, 3.0F, 3.0F, 3.0F),
                PartPose.rotation((float) (Math.PI / 2), 0.0F, (float) (Math.PI * 3.0 / 2.0))
        );
        return LayerDefinition.create(modelData, 64, 64);
    }

    @Override
    public void render(BlessedBedBlockEntity bedBlockEntity, float f, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, int j) {
        Material spriteIdentifier = sprite;
        Level world = bedBlockEntity.getLevel();
        if (world != null) {
            BlockState blockState = bedBlockEntity.getBlockState();
            DoubleBlockCombiner.NeighborCombineResult<? extends BedBlockEntity> propertySource = DoubleBlockCombiner.combineWithNeigbour(
                    BlockEntityType.BED,
                    BedBlock::getBlockType,
                    BedBlock::getConnectedDirection,
                    ChestBlock.FACING,
                    blockState,
                    world,
                    bedBlockEntity.getBlockPos(),
                    (worldx, pos) -> false
            );
            int k = ((Int2IntFunction)propertySource.apply(new BrightnessCombiner())).get(i);
            this.renderPart(
                    matrixStack,
                    vertexConsumerProvider,
                    blockState.getValue(BedBlock.PART) == BedPart.HEAD ? this.bedHead : this.bedFoot,
                    (Direction)blockState.getValue(BedBlock.FACING),
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
            PoseStack matrices,
            MultiBufferSource vertexConsumers,
            ModelPart part,
            Direction direction,
            Material sprite,
            int light,
            int overlay,
            boolean isFoot
    ) {
        matrices.pushPose();
        matrices.translate(0.0F, 0.5625F, isFoot ? -1.0F : 0.0F);
        matrices.mulPose(Axis.XP.rotationDegrees(90.0F));
        matrices.translate(0.5F, 0.5F, 0.5F);
        matrices.mulPose(Axis.ZP.rotationDegrees(180.0F + direction.toYRot()));
        matrices.translate(-0.5F, -0.5F, -0.5F);
        VertexConsumer vertexConsumer = sprite.buffer(vertexConsumers, RenderType::entitySolid);
        part.render(matrices, vertexConsumer, light, overlay);
        matrices.popPose();
    }
}
