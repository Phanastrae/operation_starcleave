package phanastrae.operation_starcleave.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.block.StarbleachCauldronBlock;
import phanastrae.operation_starcleave.block.StarbleachedPearlBlock;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;

import java.util.List;

import static net.minecraft.data.client.BlockStateModelGenerator.*;
import static phanastrae.operation_starcleave.block.OperationStarcleaveBlocks.*;

public class ModelProvider extends FabricModelProvider {
    public ModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator BSMG) {
        BSMG.registerCubeAllModelTexturePool(STARBLEACHED_TILES)
                .slab(STARBLEACHED_TILE_SLAB)
                .stairs(STARBLEACHED_TILE_STAIRS)
                .wall(STARBLEACHED_TILE_WALL);

        BSMG.registerCubeAllModelTexturePool(STELLAR_TILES)
                .slab(STELLAR_TILE_SLAB);

        BSMG.registerSimpleCubeAll(CHISELED_STARBLEACHED_TILES);
        BSMG.registerSimpleCubeAll(IMBUED_STARBLEACHED_TILES);

        BSMG.registerLog(STARBLEACHED_LOG).log(STARBLEACHED_LOG).wood(STARBLEACHED_WOOD);

        BSMG.registerSingleton(STARBLEACHED_LEAVES, TexturedModel.LEAVES);

        BSMG.registerRotatable(STELLAR_SEDIMENT);
        BSMG.registerRotatable(STARDUST_BLOCK);
        BSMG.registerRotatable(PETRICHORIC_PLASMA);
        BSMG.registerRotatable(PETRICHORIC_VAPOR);

        BSMG.registerTintableCross(SHORT_HOLY_MOSS, BlockStateModelGenerator.TintType.NOT_TINTED);
        registerUnevenCross(BSMG, MULCHBORNE_TUFT);

        registerGrassLikeBlock(BSMG, HOLY_MOSS, STELLAR_SEDIMENT);
        registerGrassLikeBlock(BSMG, STELLAR_MULCH, STELLAR_SEDIMENT);

        {
            TextureMap textureMap = TextureMap.sideEnd(NETHERITE_PUMPKIN);
            BSMG.registerNorthDefaultHorizontalRotatable(NETHERITE_PUMPKIN, textureMap);
        }

        {
            Identifier off = TexturedModel.CUBE_ALL.upload(STARBLEACHED_PEARL_BLOCK, BSMG.modelCollector);
            Identifier on = BSMG.createSubModel(STARBLEACHED_PEARL_BLOCK, "_on", Models.CUBE_ALL, TextureMap::all);
            BSMG.blockStateCollector
                    .accept(VariantsBlockStateSupplier.create(STARBLEACHED_PEARL_BLOCK).coordinate(createBooleanModelMap(StarbleachedPearlBlock.TRIGGERED, on, off)));
        }

        {
            Block wool = BLESSED_CLOTH_BLOCK;
            Block carpet = BLESSED_CLOTH_CARPET;
            Block curtain = BLESSED_CLOTH_CURTAIN;

            BSMG.registerSimpleCubeAll(wool);
            TextureMap textureMap = new TextureMap().put(TextureKey.PANE, TextureMap.getId(curtain)).put(TextureKey.EDGE, TextureMap.getId(wool));
            Identifier identifier = Models.TEMPLATE_GLASS_PANE_POST.upload(curtain, textureMap, BSMG.modelCollector);
            Identifier identifier2 = Models.TEMPLATE_GLASS_PANE_SIDE.upload(curtain, textureMap, BSMG.modelCollector);
            Identifier identifier3 = Models.TEMPLATE_GLASS_PANE_SIDE_ALT.upload(curtain, textureMap, BSMG.modelCollector);
            Identifier identifier4 = Models.TEMPLATE_GLASS_PANE_NOSIDE.upload(curtain, textureMap, BSMG.modelCollector);
            Identifier identifier5 = Models.TEMPLATE_GLASS_PANE_NOSIDE_ALT.upload(curtain, textureMap, BSMG.modelCollector);
            Item item = curtain.asItem();
            Models.GENERATED.upload(ModelIds.getItemModelId(item), TextureMap.layer0(curtain), BSMG.modelCollector);
            BSMG.blockStateCollector
                    .accept(
                            MultipartBlockStateSupplier.create(curtain)
                                    .with(BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
                                    .with(When.create().set(Properties.NORTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
                                    .with(
                                            When.create().set(Properties.EAST, true),
                                            BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                    )
                                    .with(When.create().set(Properties.SOUTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier3))
                                    .with(
                                            When.create().set(Properties.WEST, true),
                                            BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                    )
                                    .with(When.create().set(Properties.NORTH, false), BlockStateVariant.create().put(VariantSettings.MODEL, identifier4))
                                    .with(When.create().set(Properties.EAST, false), BlockStateVariant.create().put(VariantSettings.MODEL, identifier5))
                                    .with(
                                            When.create().set(Properties.SOUTH, false),
                                            BlockStateVariant.create().put(VariantSettings.MODEL, identifier5).put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                    )
                                    .with(
                                            When.create().set(Properties.WEST, false),
                                            BlockStateVariant.create().put(VariantSettings.MODEL, identifier4).put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                    )
                    );
            Identifier identifier6 = TexturedModel.CARPET.get(wool).upload(carpet, BSMG.modelCollector);
            BSMG.blockStateCollector.accept(createSingletonBlockState(carpet, identifier6));
        }

        {
            TextureMap dryTextures = new TextureMap().put(TextureKey.DIRT, TextureMap.getId(STELLAR_SEDIMENT)).put(TextureKey.TOP, TextureMap.getId(STELLAR_FARMLAND));
            TextureMap moistTextures = new TextureMap().put(TextureKey.DIRT, TextureMap.getId(STELLAR_SEDIMENT)).put(TextureKey.TOP, TextureMap.getSubId(STELLAR_FARMLAND, "_moist"));
            Identifier dryModel = Models.TEMPLATE_FARMLAND.upload(STELLAR_FARMLAND, dryTextures, BSMG.modelCollector);
            Identifier moistModel = Models.TEMPLATE_FARMLAND.upload(TextureMap.getSubId(STELLAR_FARMLAND, "_moist"), moistTextures, BSMG.modelCollector);
            BSMG.blockStateCollector
                    .accept(VariantsBlockStateSupplier.create(STELLAR_FARMLAND).coordinate(createValueFencedModelMap(Properties.MOISTURE, 7, moistModel, dryModel)));
        }

        registerFire(BSMG, PHLOGISTIC_FIRE);
        registerStarbleachCauldron(BSMG, STARBLEACH_CAULDRON);
    }

    private void registerFire(BlockStateModelGenerator BSMG, Block block) {
        When when = When.create()
                .set(Properties.NORTH, false)
                .set(Properties.EAST, false)
                .set(Properties.SOUTH, false)
                .set(Properties.WEST, false)
                .set(Properties.UP, false);
        List<Identifier> list = BSMG.getFireFloorModels(block);
        List<Identifier> list2 = BSMG.getFireSideModels(block);
        List<Identifier> list3 = BSMG.getFireUpModels(block);
        BSMG.blockStateCollector
                .accept(
                        MultipartBlockStateSupplier.create(block)
                                .with(when, buildBlockStateVariants(list, blockStateVariant -> blockStateVariant))
                                .with(When.anyOf(When.create().set(Properties.NORTH, true), when), buildBlockStateVariants(list2, blockStateVariant -> blockStateVariant))
                                .with(
                                        When.anyOf(When.create().set(Properties.EAST, true), when),
                                        buildBlockStateVariants(list2, blockStateVariant -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.R90))
                                )
                                .with(
                                        When.anyOf(When.create().set(Properties.SOUTH, true), when),
                                        buildBlockStateVariants(list2, blockStateVariant -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.R180))
                                )
                                .with(
                                        When.anyOf(When.create().set(Properties.WEST, true), when),
                                        buildBlockStateVariants(list2, blockStateVariant -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.R270))
                                )
                                .with(When.create().set(Properties.UP, true), buildBlockStateVariants(list3, blockStateVariant -> blockStateVariant))
                );
    }

    private void registerStarbleachCauldron(BlockStateModelGenerator BSMG, Block block) {
        BlockStateVariantMap.SingleProperty<Integer> map = BlockStateVariantMap.create(StarbleachCauldronBlock.LEVEL_7);
        for(int i = 1; i <= 7; i++) {
            map = map.register(
                    i,
                    BlockStateVariant.create()
                            .put(
                                    VariantSettings.MODEL,
                                    OperationStarcleaveModels.getSevenLevelCauldron(i)
                                            .upload(block, "_level" + i, TextureMap.cauldron(OperationStarcleave.id("block/starbleach_still")), BSMG.modelCollector)
                            )
            );
        }
        BSMG.blockStateCollector.accept(VariantsBlockStateSupplier.create(block).coordinate(map));
    }

    private void registerGrassLikeBlock(BlockStateModelGenerator BSMG, Block block, Block baseBlock) {
        Identifier baseBlockIdentifier = TextureMap.getId(baseBlock);
        Identifier modelId = TexturedModel.CUBE_BOTTOM_TOP
                .get(block)
                .textures(textures -> textures.put(TextureKey.BOTTOM, baseBlockIdentifier))
                .upload(block, BSMG.modelCollector);
        BSMG.blockStateCollector
                .accept(VariantsBlockStateSupplier.create(block, createModelVariantWithRandomHorizontalRotations(modelId)));
    }

    private void registerUnevenCross(BlockStateModelGenerator BSMG, Block block) {
        BSMG.registerItemModel(block);

        TextureMap textureMap = TextureMap.cross(block);
        Identifier modelId = OperationStarcleaveModels.UNEVEN_CROSS.upload(block, textureMap, BSMG.modelCollector);
        Identifier modelId2 = OperationStarcleaveModels.UNEVEN_CROSS_MIRRORED.upload(block, textureMap, BSMG.modelCollector);
        BSMG.blockStateCollector.accept(VariantsBlockStateSupplier.create(
                block,
                BlockStateVariant.create().put(VariantSettings.MODEL, modelId),
                BlockStateVariant.create().put(VariantSettings.MODEL, modelId2),
                BlockStateVariant.create().put(VariantSettings.MODEL, modelId).put(VariantSettings.Y, VariantSettings.Rotation.R90),
                BlockStateVariant.create().put(VariantSettings.MODEL, modelId2).put(VariantSettings.Y, VariantSettings.Rotation.R90)
        ));
    }

    @Override
    public void generateItemModels(ItemModelGenerator IMG) {
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

        IMG.register(OperationStarcleaveItems.FIRMAMENT_MANIPULATOR, Models.HANDHELD);
    }

    private static void registerGenerated(ItemModelGenerator IMG, Item item) {
        IMG.register(item, Models.GENERATED);
    }
}
