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
    public void generateBlockStateModels(BlockModelGenerators BMG) {
        BMG.createTrivialCube(CHISELED_STARBLEACHED_TILES);
        BMG.createTrivialCube(IMBUED_STARBLEACHED_TILES);
        BMG.createTrivialCube(NUCLEOSYNTHESEED);
        BMG.createTrivialCube(PLASMA_ICE);

        BMG.createRotatedVariantBlock(COAGULATED_PLASMA);
        BMG.createRotatedVariantBlock(STELLAR_SEDIMENT);
        BMG.createRotatedVariantBlock(STARDUST_BLOCK);
        BMG.createRotatedVariantBlock(PETRICHORIC_PLASMA);
        BMG.createRotatedVariantBlock(PETRICHORIC_VAPOR);

        BMG.createTrivialBlock(STARBLEACHED_LEAVES, TexturedModel.LEAVES);
        BMG.createTrivialBlock(NUCLEIC_FISSURELEAVES, TexturedModel.LEAVES);

        BMG.woodProvider(STARBLEACHED_LOG).logWithHorizontal(STARBLEACHED_LOG).wood(STARBLEACHED_WOOD);
        BMG.woodProvider(NUCLEIC_FISSUREROOT).logWithHorizontal(NUCLEIC_FISSUREROOT);

        BMG.family(STARBLEACHED_TILES)
                .slab(STARBLEACHED_TILE_SLAB)
                .stairs(STARBLEACHED_TILE_STAIRS)
                .wall(STARBLEACHED_TILE_WALL);

        BMG.family(STELLAR_TILES)
                .slab(STELLAR_TILE_SLAB);


        BMG.createCrossBlockWithDefaultItem(SHORT_HOLY_MOSS, BlockModelGenerators.TintState.NOT_TINTED);
        registerUnevenCross(BMG, MULCHBORNE_TUFT);

        registerGrassLikeBlock(BMG, HOLY_MOSS, STELLAR_SEDIMENT);
        registerGrassLikeBlock(BMG, STELLAR_MULCH, STELLAR_SEDIMENT);

        BMG.createCropBlock(BISREEDS, BlockStateProperties.AGE_3, 0, 1, 2, 3);

        {
            TextureMapping textureMap = TextureMapping.column(NETHERITE_PUMPKIN);
            BMG.createPumpkinVariant(NETHERITE_PUMPKIN, textureMap);
        }

        {
            ResourceLocation off = TexturedModel.CUBE.create(STARBLEACHED_PEARL_BLOCK, BMG.modelOutput);
            ResourceLocation on = BMG.createSuffixedVariant(STARBLEACHED_PEARL_BLOCK, "_on", ModelTemplates.CUBE_ALL, TextureMapping::cube);
            BMG.blockStateOutput
                    .accept(MultiVariantGenerator.multiVariant(STARBLEACHED_PEARL_BLOCK).with(createBooleanModelDispatch(StarbleachedPearlBlock.TRIGGERED, on, off)));
        }

        {
            Block wool = BLESSED_CLOTH_BLOCK;
            Block carpet = BLESSED_CLOTH_CARPET;
            Block curtain = BLESSED_CLOTH_CURTAIN;

            BMG.createTrivialCube(wool);
            TextureMapping textureMap = new TextureMapping().put(TextureSlot.PANE, TextureMapping.getBlockTexture(curtain)).put(TextureSlot.EDGE, TextureMapping.getBlockTexture(wool));
            ResourceLocation identifier = ModelTemplates.STAINED_GLASS_PANE_POST.create(curtain, textureMap, BMG.modelOutput);
            ResourceLocation identifier2 = ModelTemplates.STAINED_GLASS_PANE_SIDE.create(curtain, textureMap, BMG.modelOutput);
            ResourceLocation identifier3 = ModelTemplates.STAINED_GLASS_PANE_SIDE_ALT.create(curtain, textureMap, BMG.modelOutput);
            ResourceLocation identifier4 = ModelTemplates.STAINED_GLASS_PANE_NOSIDE.create(curtain, textureMap, BMG.modelOutput);
            ResourceLocation identifier5 = ModelTemplates.STAINED_GLASS_PANE_NOSIDE_ALT.create(curtain, textureMap, BMG.modelOutput);
            Item item = curtain.asItem();
            ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item), TextureMapping.layer0(curtain), BMG.modelOutput);
            BMG.blockStateOutput
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
            ResourceLocation identifier6 = TexturedModel.CARPET.get(wool).create(carpet, BMG.modelOutput);
            BMG.blockStateOutput.accept(createSimpleBlock(carpet, identifier6));
        }

        {
            TextureMapping dryTextures = new TextureMapping().put(TextureSlot.DIRT, TextureMapping.getBlockTexture(STELLAR_SEDIMENT)).put(TextureSlot.TOP, TextureMapping.getBlockTexture(STELLAR_FARMLAND));
            TextureMapping moistTextures = new TextureMapping().put(TextureSlot.DIRT, TextureMapping.getBlockTexture(STELLAR_SEDIMENT)).put(TextureSlot.TOP, TextureMapping.getBlockTexture(STELLAR_FARMLAND, "_moist"));
            ResourceLocation dryModel = ModelTemplates.FARMLAND.create(STELLAR_FARMLAND, dryTextures, BMG.modelOutput);
            ResourceLocation moistModel = ModelTemplates.FARMLAND.create(TextureMapping.getBlockTexture(STELLAR_FARMLAND, "_moist"), moistTextures, BMG.modelOutput);
            BMG.blockStateOutput
                    .accept(MultiVariantGenerator.multiVariant(STELLAR_FARMLAND).with(createEmptyOrFullDispatch(BlockStateProperties.MOISTURE, 7, moistModel, dryModel)));
        }

        registerFire(BMG, PHLOGISTIC_FIRE);
        registerStarbleachCauldron(BMG, STARBLEACH_CAULDRON);
    }

    private void registerFire(BlockModelGenerators BMG, Block block) {
        Condition when = Condition.condition()
                .term(BlockStateProperties.NORTH, false)
                .term(BlockStateProperties.EAST, false)
                .term(BlockStateProperties.SOUTH, false)
                .term(BlockStateProperties.WEST, false)
                .term(BlockStateProperties.UP, false);
        List<ResourceLocation> list = BMG.createFloorFireModels(block);
        List<ResourceLocation> list2 = BMG.createSideFireModels(block);
        List<ResourceLocation> list3 = BMG.createTopFireModels(block);
        BMG.blockStateOutput
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

    private void registerStarbleachCauldron(BlockModelGenerators BMG, Block block) {
        PropertyDispatch.C1<Integer> map = PropertyDispatch.property(StarbleachCauldronBlock.LEVEL_7);
        for(int i = 1; i <= 7; i++) {
            map = map.select(
                    i,
                    Variant.variant()
                            .with(
                                    VariantProperties.MODEL,
                                    OperationStarcleaveModels.getSevenLevelCauldron(i)
                                            .createWithSuffix(block, "_level" + i, TextureMapping.cauldron(OperationStarcleave.id("block/starbleach_still")), BMG.modelOutput)
                            )
            );
        }
        BMG.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block).with(map));
    }

    private void registerGrassLikeBlock(BlockModelGenerators BMG, Block block, Block baseBlock) {
        ResourceLocation baseBlockIdentifier = TextureMapping.getBlockTexture(baseBlock);
        ResourceLocation modelId = TexturedModel.CUBE_TOP_BOTTOM
                .get(block)
                .updateTextures(textures -> textures.put(TextureSlot.BOTTOM, baseBlockIdentifier))
                .create(block, BMG.modelOutput);
        BMG.blockStateOutput
                .accept(MultiVariantGenerator.multiVariant(block, createRotatedVariants(modelId)));
    }

    private void registerUnevenCross(BlockModelGenerators BMG, Block block) {
        BMG.createSimpleFlatItemModel(block);

        TextureMapping textureMap = TextureMapping.cross(block);
        ResourceLocation modelId = OperationStarcleaveModels.UNEVEN_CROSS.create(block, textureMap, BMG.modelOutput);
        ResourceLocation modelId2 = OperationStarcleaveModels.UNEVEN_CROSS_MIRRORED.create(block, textureMap, BMG.modelOutput);
        BMG.blockStateOutput.accept(MultiVariantGenerator.multiVariant(
                block,
                Variant.variant().with(VariantProperties.MODEL, modelId),
                Variant.variant().with(VariantProperties.MODEL, modelId2),
                Variant.variant().with(VariantProperties.MODEL, modelId).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90),
                Variant.variant().with(VariantProperties.MODEL, modelId2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
        ));
    }

    @Override
    public void generateItemModels(ItemModelGenerators IMG) {
        generateFlat(IMG,
                OperationStarcleaveItems.STARCLEAVER_GOLEM_BUCKET,

                OperationStarcleaveItems.BISMUTH_FLAKE,
                OperationStarcleaveItems.STARFLAKED_BISMUTH,

                OperationStarcleaveItems.STARDUST_CLUSTER,

                OperationStarcleaveItems.BLESSED_BED,

                OperationStarcleaveItems.STARBLEACH_BOTTLE,
                OperationStarcleaveItems.SPLASH_STARBLEACH_BOTTLE,

                OperationStarcleaveItems.STARBLEACHED_PEARL,
                OperationStarcleaveItems.STARFRUIT,

                OperationStarcleaveItems.HOLY_STRANDS,
                OperationStarcleaveItems.BLESSED_CLOTH,

                OperationStarcleaveItems.HOLLOWED_SAC,
                OperationStarcleaveItems.PHLOGISTON_SAC,

                OperationStarcleaveItems.BISMUTH_PEGASUS_ARMOR,
                OperationStarcleaveItems.FIRMAMENT_REJUVENATOR
        );

        IMG.generateFlatItem(OperationStarcleaveItems.FIRMAMENT_MANIPULATOR, ModelTemplates.FLAT_HANDHELD_ITEM);
    }

    private static void generateFlat(ItemModelGenerators IMG, Item... items) {
        for(Item item : items) {
            generateFlat(IMG, item);
        }
    }

    private static void generateFlat(ItemModelGenerators IMG, Item item) {
        IMG.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
    }
}
