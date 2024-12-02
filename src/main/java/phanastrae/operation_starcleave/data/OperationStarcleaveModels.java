package phanastrae.operation_starcleave.data;

import phanastrae.operation_starcleave.OperationStarcleave;

import java.util.Optional;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureSlot;

public class OperationStarcleaveModels {
    public static final ModelTemplate UNEVEN_CROSS = block("uneven_cross", TextureSlot.CROSS);
    public static final ModelTemplate UNEVEN_CROSS_MIRRORED = block("uneven_cross_mirrored", "_mirrored", TextureSlot.CROSS);

    private static final ModelTemplate[] TEMPLATE_CAULDRON_LEVELS = createCauldronArray();

    private static ModelTemplate block(String parent, TextureSlot... requiredTextureKeys) {
        return new ModelTemplate(Optional.of(OperationStarcleave.id("block/" + parent)), Optional.empty(), requiredTextureKeys);
    }

    private static ModelTemplate block(String parent, String variant, TextureSlot... requiredTextureKeys) {
        return new ModelTemplate(Optional.of(OperationStarcleave.id("block/" + parent)), Optional.of(variant), requiredTextureKeys);
    }

    private static ModelTemplate[] createCauldronArray() {
        ModelTemplate[] array = new ModelTemplate[7];
        for(int i = 1; i <= 7; i++) {
            array[i - 1] = block(
                    "template_cauldron_7_level" + i, TextureSlot.CONTENT, TextureSlot.INSIDE, TextureSlot.PARTICLE, TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE);
        }
        return array;
    }

    public static ModelTemplate getSevenLevelCauldron(int level) {
        return TEMPLATE_CAULDRON_LEVELS[level - 1];
    }
}
