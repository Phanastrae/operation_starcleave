package phanastrae.operation_starcleave.data;

import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import phanastrae.operation_starcleave.OperationStarcleave;

import java.util.Optional;

public class OperationStarcleaveModels {
    public static final Model UNEVEN_CROSS = block("uneven_cross", TextureKey.CROSS);
    public static final Model UNEVEN_CROSS_MIRRORED = block("uneven_cross_mirrored", "_mirrored", TextureKey.CROSS);

    private static Model block(String parent, TextureKey... requiredTextureKeys) {
        return new Model(Optional.of(OperationStarcleave.id("block/" + parent)), Optional.empty(), requiredTextureKeys);
    }

    private static Model block(String parent, String variant, TextureKey... requiredTextureKeys) {
        return new Model(Optional.of(OperationStarcleave.id("block/" + parent)), Optional.of(variant), requiredTextureKeys);
    }
}
