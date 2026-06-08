package phanastrae.operation_starcleave.fabric.data;

import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureSlot;
import phanastrae.operation_starcleave.OperationStarcleave;

import java.util.Optional;

public class OperationStarcleaveModelTemplates {
    public static final TextureSlot LEFT = TextureSlot.create("left", TextureSlot.ALL);
    public static final TextureSlot RIGHT = TextureSlot.create("right", TextureSlot.ALL);

    public static final ModelTemplate UNEVEN_CROSS = create("uneven_cross", TextureSlot.CROSS);
    public static final ModelTemplate UNEVEN_CROSS_MIRRORED = create("uneven_cross_mirrored", "_mirrored", TextureSlot.CROSS);

    public static final ModelTemplate SIDED_STAIRS_STRAIGHT = create("sided_stairs", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.FRONT, TextureSlot.BACK, LEFT, RIGHT);
    public static final ModelTemplate SIDED_STAIRS_INNER = create("sided_inner_stairs", "_inner", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.BACK, LEFT, RIGHT);
    public static final ModelTemplate SIDED_STAIRS_OUTER = create("sided_outer_stairs", "_outer", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.FRONT, TextureSlot.BACK, LEFT, RIGHT); // include back slot for particles

    public static final ModelTemplate SIDED_WALL_POST = create("template_sided_wall_post", "_post", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
    public static final ModelTemplate SIDED_WALL_LOW_SIDE = create("template_sided_wall_side", "_side", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE, TextureSlot.WALL);
    public static final ModelTemplate SIDED_WALL_TALL_SIDE = create("template_sided_wall_side_tall", "_side_tall", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
    public static final ModelTemplate SIDED_WALL_INVENTORY = create("sided_wall_inventory", "_inventory", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE, TextureSlot.WALL);

    private static final ModelTemplate[] TEMPLATE_CAULDRON_LEVELS = createCauldronArray();

    private static ModelTemplate create(String parent, TextureSlot... requiredTextureKeys) {
        return new ModelTemplate(Optional.of(OperationStarcleave.id("block/" + parent)), Optional.empty(), requiredTextureKeys);
    }

    private static ModelTemplate create(String parent, String variant, TextureSlot... requiredTextureKeys) {
        return new ModelTemplate(Optional.of(OperationStarcleave.id("block/" + parent)), Optional.of(variant), requiredTextureKeys);
    }

    private static ModelTemplate[] createCauldronArray() {
        ModelTemplate[] array = new ModelTemplate[7];
        for (int i = 1; i <= 7; i++) {
            array[i - 1] = create(
                    "template_cauldron_7_level" + i, TextureSlot.CONTENT, TextureSlot.INSIDE, TextureSlot.PARTICLE, TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE);
        }
        return array;
    }

    public static ModelTemplate getSevenLevelCauldron(int level) {
        return TEMPLATE_CAULDRON_LEVELS[level - 1];
    }
}
