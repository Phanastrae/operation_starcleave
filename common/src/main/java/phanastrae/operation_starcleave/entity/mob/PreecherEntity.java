package phanastrae.operation_starcleave.entity.mob;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

public class PreecherEntity extends Creeper {
    // TODO custom behaviour

    public PreecherEntity(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.MAX_HEALTH, 24.0)
                .add(Attributes.ARMOR, 2.0);
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        // needed for mob spawning to work on bright blocks
        return 0.0F;
    }
}
