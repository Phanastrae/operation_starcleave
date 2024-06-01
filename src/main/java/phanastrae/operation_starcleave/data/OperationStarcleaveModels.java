package phanastrae.operation_starcleave.data;

import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import phanastrae.operation_starcleave.OperationStarcleave;

import java.util.Optional;

public class OperationStarcleaveModels {
    public static final Model UNEVEN_CROSS = block("uneven_cross", TextureKey.CROSS);
    public static final Model UNEVEN_CROSS_MIRRORED = block("uneven_cross_mirrored", "_mirrored", TextureKey.CROSS);

    private static final Model[] TEMPLATE_CAULDRON_LEVELS = createCauldronArray();

    private static Model block(String parent, TextureKey... requiredTextureKeys) {
        return new Model(Optional.of(OperationStarcleave.id("block/" + parent)), Optional.empty(), requiredTextureKeys);
    }

    private static Model block(String parent, String variant, TextureKey... requiredTextureKeys) {
        return new Model(Optional.of(OperationStarcleave.id("block/" + parent)), Optional.of(variant), requiredTextureKeys);
    }

    private static Model[] createCauldronArray() {
        Model[] array = new Model[7];
        for(int i = 1; i <= 7; i++) {
            array[i - 1] = block(
                    "template_cauldron_7_level" + i, TextureKey.CONTENT, TextureKey.INSIDE, TextureKey.PARTICLE, TextureKey.TOP, TextureKey.BOTTOM, TextureKey.SIDE);
        }
        return array;
    }

    public static Model getSevenLevelCauldron(int level) {
        return TEMPLATE_CAULDRON_LEVELS[level - 1];
    }
}
