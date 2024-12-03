package phanastrae.operation_starcleave.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.block.StarbleachCauldronBlock;
import phanastrae.operation_starcleave.block.StarbleachedPearlBlock;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;

import java.util.List;

import static net.minecraft.data.models.BlockModelGenerators.*;
import static phanastrae.operation_starcleave.block.OperationStarcleaveBlocks.*;

public class ModelProvider extends FabricModelProvider {
    public ModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators BSMG) {
        BSMG.family(STARBLEACHED_TILES)
                .slab(STARBLEACHED_TILE_SLAB)
                .stairs(STARBLEACHED_TILE_STAIRS)
                .wall(STARBLEACHED_TILE_WALL);

        BSMG.family(STELLAR_TILES)
                .slab(STELLAR_TILE_SLAB);

        BSMG.createTrivialCube(CHISELED_STARBLEACHED_TILES);
        BSMG.createTrivialCube(IMBUED_STARBLEACHED_TILES);

        BSMG.woodProvider(STARBLEACHED_LOG).logWithHorizontal(STARBLEACHED_LOG).wood(STARBLEACHED_WOOD);

        BSMG.createTrivialBlock(STARBLEACHED_LEAVES, TexturedModel.LEAVES);

        BSMG.createRotatedVariantBlock(STELLAR_SEDIMENT);
        BSMG.createRotatedVariantBlock(STARDUST_BLOCK);
        BSMG.createRotatedVariantBlock(PETRICHORIC_PLASMA);
        BSMG.createRotatedVariantBlock(PETRICHORIC_VAPOR);

        BSMG.createCrossBlockWithDefaultItem(SHORT_HOLY_MOSS, BlockModelGenerators.TintState.NOT_TINTED);
        registerUnevenCross(BSMG, MULCHBORNE_TUFT);

        registerGrassLikeBlock(BSMG, HOLY_MOSS, STELLAR_SEDIMENT);
        registerGrassLikeBlock(BSMG, STELLAR_MULCH, STELLAR_SEDIMENT);

        {
            TextureMapping textureMap = TextureMapping.column(NETHERITE_PUMPKIN);
            BSMG.createPumpkinVariant(NETHERITE_PUMPKIN, textureMap);
        }

        {
            ResourceLocation off = TexturedModel.CUBE.create(STARBLEACHED_PEARL_BLOCK, BSMG.modelOutput);
            ResourceLocation on = BSMG.createSuffixedVariant(STARBLEACHED_PEARL_BLOCK, "_on", ModelTemplates.CUBE_ALL, TextureMapping::cube);
            BSMG.blockStateOutput
                    .accept(MultiVariantGenerator.multiVariant(STARBLEACHED_PEARL_BLOCK).with(createBooleanModelDispatch(StarbleachedPearlBlock.TRIGGERED, on, off)));
        }

        {
            Block wool = BLESSED_CLOTH_BLOCK;
            Block carpet = BLESSED_CLOTH_CARPET;
            Block curtain = BLESSED_CLOTH_CURTAIN;

            BSMG.createTrivialCube(wool);
            TextureMapping textureMap = new TextureMapping().put(TextureSlot.PANE, TextureMapping.getBlockTexture(curtain)).put(TextureSlot.EDGE, TextureMapping.getBlockTexture(wool));
            ResourceLocation identifier = ModelTemplates.STAINED_GLASS_PANE_POST.create(curtain, textureMap, BSMG.modelOutput);
            ResourceLocation identifier2 = ModelTemplates.STAINED_GLASS_PANE_SIDE.create(curtain, textureMap, BSMG.modelOutput);
            ResourceLocation identifier3 = ModelTemplates.STAINED_GLASS_PANE_SIDE_ALT.create(curtain, textureMap, BSMG.modelOutput);
            ResourceLocation identifier4 = ModelTemplates.STAINED_GLASS_PANE_NOSIDE.create(curtain, textureMap, BSMG.modelOutput);
            ResourceLocation identifier5 = ModelTemplates.STAINED_GLASS_PANE_NOSIDE_ALT.create(curtain, textureMap, BSMG.modelOutput);
            Item item = curtain.asItem();
            ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item), TextureMapping.layer0(curtain), BSMG.modelOutput);
            BSMG.blockStateOutput
                    .accept(
                            MultiPartGenerator.multiPart(curtain)
                                    .with(Variant.variant().with(VariantProperties.MODEL, identifier))
                                    .with(Condition.condition().term(BlockStateProperties.NORTH, true), Variant.variant().with(VariantProperties.MODEL, identifier2))
                                    .with(
                                            Condition.condition().term(BlockStateProperties.EAST, true),
                                            Variant.variant().with(VariantProperties.MODEL, identifier2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                    )
                                    .with(Condition.condition().term(BlockStateProperties.SOUTH, true), Variant.variant().with(VariantProperties.MODEL, identifier3))
                                    .with(
                                            Condition.condition().term(BlockStateProperties.WEST, true),
                                            Variant.variant().with(VariantProperties.MODEL, identifier3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                    )
                                    .with(Condition.condition().term(BlockStateProperties.NORTH, false), Variant.variant().with(VariantProperties.MODEL, identifier4))
                                    .with(Condition.condition().term(BlockStateProperties.EAST, false), Variant.variant().with(VariantProperties.MODEL, identifier5))
                                    .with(
                                            Condition.condition().term(BlockStateProperties.SOUTH, false),
                                            Variant.variant().with(VariantProperties.MODEL, identifier5).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                    )
                                    .with(
                                            Condition.condition().term(BlockStateProperties.WEST, false),
                                            Variant.variant().with(VariantProperties.MODEL, identifier4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                    )
                    );
            ResourceLocation identifier6 = TexturedModel.CARPET.get(wool).create(carpet, BSMG.modelOutput);
            BSMG.blockStateOutput.accept(createSimpleBlock(carpet, identifier6));
        }

        {
            TextureMapping dryTextures = new TextureMapping().put(TextureSlot.DIRT, TextureMapping.getBlockTexture(STELLAR_SEDIMENT)).put(TextureSlot.TOP, TextureMapping.getBlockTexture(STELLAR_FARMLAND));
            TextureMapping moistTextures = new TextureMapping().put(TextureSlot.DIRT, TextureMapping.getBlockTexture(STELLAR_SEDIMENT)).put(TextureSlot.TOP, TextureMapping.getBlockTexture(STELLAR_FARMLAND, "_moist"));
            ResourceLocation dryModel = ModelTemplates.FARMLAND.create(STELLAR_FARMLAND, dryTextures, BSMG.modelOutput);
            ResourceLocation moistModel = ModelTemplates.FARMLAND.create(TextureMapping.getBlockTexture(STELLAR_FARMLAND, "_moist"), moistTextures, BSMG.modelOutput);
            BSMG.blockStateOutput
                    .accept(MultiVariantGenerator.multiVariant(STELLAR_FARMLAND).with(createEmptyOrFullDispatch(BlockStateProperties.MOISTURE, 7, moistModel, dryModel)));
        }

        registerFire(BSMG, PHLOGISTIC_FIRE);
        registerStarbleachCauldron(BSMG, STARBLEACH_CAULDRON);
    }

    private void registerFire(BlockModelGenerators BSMG, Block block) {
        Condition when = Condition.condition()
                .term(BlockStateProperties.NORTH, false)
                .term(BlockStateProperties.EAST, false)
                .term(BlockStateProperties.SOUTH, false)
                .term(BlockStateProperties.WEST, false)
                .term(BlockStateProperties.UP, false);
        List<ResourceLocation> list = BSMG.createFloorFireModels(block);
        List<ResourceLocation> list2 = BSMG.createSideFireModels(block);
        List<ResourceLocation> list3 = BSMG.createTopFireModels(block);
        BSMG.blockStateOutput
                .accept(
                        MultiPartGenerator.multiPart(block)
                                .with(when, wrapModels(list, blockStateVariant -> blockStateVariant))
                                .with(Condition.or(Condition.condition().term(BlockStateProperties.NORTH, true), when), wrapModels(list2, blockStateVariant -> blockStateVariant))
                                .with(
                                        Condition.or(Condition.condition().term(BlockStateProperties.EAST, true), when),
                                        wrapModels(list2, blockStateVariant -> blockStateVariant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                                )
                                .with(
                                        Condition.or(Condition.condition().term(BlockStateProperties.SOUTH, true), when),
                                        wrapModels(list2, blockStateVariant -> blockStateVariant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                                )
                                .with(
                                        Condition.or(Condition.condition().term(BlockStateProperties.WEST, true), when),
                                        wrapModels(list2, blockStateVariant -> blockStateVariant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                                )
                                .with(Condition.condition().term(BlockStateProperties.UP, true), wrapModels(list3, blockStateVariant -> blockStateVariant))
                );
    }

    private void registerStarbleachCauldron(BlockModelGenerators BSMG, Block block) {
        PropertyDispatch.C1<Integer> map = PropertyDispatch.property(StarbleachCauldronBlock.LEVEL_7);
        for(int i = 1; i <= 7; i++) {
            map = map.select(
                    i,
                    Variant.variant()
                            .with(
                                    VariantProperties.MODEL,
                                    OperationStarcleaveModels.getSevenLevelCauldron(i)
                                            .createWithSuffix(block, "_level" + i, TextureMapping.cauldron(OperationStarcleave.id("block/starbleach_still")), BSMG.modelOutput)
                            )
            );
        }
        BSMG.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block).with(map));
    }

    private void registerGrassLikeBlock(BlockModelGenerators BSMG, Block block, Block baseBlock) {
        ResourceLocation baseBlockIdentifier = TextureMapping.getBlockTexture(baseBlock);
        ResourceLocation modelId = TexturedModel.CUBE_TOP_BOTTOM
                .get(block)
                .updateTextures(textures -> textures.put(TextureSlot.BOTTOM, baseBlockIdentifier))
                .create(block, BSMG.modelOutput);
        BSMG.blockStateOutput
                .accept(MultiVariantGenerator.multiVariant(block, createRotatedVariants(modelId)));
    }

    private void registerUnevenCross(BlockModelGenerators BSMG, Block block) {
        BSMG.createSimpleFlatItemModel(block);

        TextureMapping textureMap = TextureMapping.cross(block);
        ResourceLocation modelId = OperationStarcleaveModels.UNEVEN_CROSS.create(block, textureMap, BSMG.modelOutput);
        ResourceLocation modelId2 = OperationStarcleaveModels.UNEVEN_CROSS_MIRRORED.create(block, textureMap, BSMG.modelOutput);
        BSMG.blockStateOutput.accept(MultiVariantGenerator.multiVariant(
                block,
                Variant.variant().with(VariantProperties.MODEL, modelId),
                Variant.variant().with(VariantProperties.MODEL, modelId2),
                Variant.variant().with(VariantProperties.MODEL, modelId).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90),
                Variant.variant().with(VariantProperties.MODEL, modelId2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
        ));
    }

    @Override
    public void generateItemModels(ItemModelGenerators IMG) {
        registerGenerated(IMG, OperationStarcleaveItems.STARCLEAVER_GOLEM_BUCKET);
        registerGenerated(IMG, OperationStarcleaveItems.BLESSED_BED);
        registerGenerated(IMG, OperationStarcleaveItems.BLESSED_CLOTH);
        registerGenerated(IMG, OperationStarcleaveItems.FIRMAMENT_REJUVENATOR);
        registerGenerated(IMG, OperationStarcleaveItems.HOLY_STRANDS);
        registerGenerated(IMG, OperationStarcleaveItems.SPLASH_STARBLEACH_BOTTLE);
        registerGenerated(IMG, OperationStarcleaveItems.STARBLEACH_BOTTLE);
        registerGenerated(IMG, OperationStarcleaveItems.STARBLEACHED_PEARL);
        registerGenerated(IMG, OperationStarcleaveItems.STARDUST_CLUSTER);
        registerGenerated(IMG, OperationStarcleaveItems.STARFRUIT);
        registerGenerated(IMG, OperationStarcleaveItems.HOLLOWED_SAC);
        registerGenerated(IMG, OperationStarcleaveItems.PHLOGISTON_SAC);

        IMG.generateFlatItem(OperationStarcleaveItems.FIRMAMENT_MANIPULATOR, ModelTemplates.FLAT_HANDHELD_ITEM);
    }

    private static void registerGenerated(ItemModelGenerators IMG, Item item) {
        IMG.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
    }
}
