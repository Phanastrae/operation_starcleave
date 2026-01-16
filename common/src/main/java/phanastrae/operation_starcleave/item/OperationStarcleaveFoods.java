package phanastrae.operation_starcleave.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import phanastrae.operation_starcleave.entity.effect.OperationStarcleaveStatusEffects;

public class OperationStarcleaveFoods {
    public static final FoodProperties STARFRUIT = new FoodProperties.Builder()
            .nutrition(2)
            .saturationModifier(0.5F)
            .alwaysEdible()
            .fast()
            .build();

    public static final FoodProperties STARBLEACH_BOTTLE = new FoodProperties.Builder()
            .nutrition(12)
            .saturationModifier(5F)
            .effect(new MobEffectInstance(OperationStarcleaveStatusEffects.STARBLEACHED_INSIDES_ENTRY, 6000, 5), 1)
            .alwaysEdible()
            .build();

    public static final FoodProperties STARBLEACH_BUCKET = new FoodProperties.Builder()
            .nutrition(48)
            .saturationModifier(5F)
            .effect(new MobEffectInstance(OperationStarcleaveStatusEffects.STARBLEACHED_INSIDES_ENTRY, 18000, 9), 1)
            .alwaysEdible()
            .build();

    public static final FoodProperties MUCKY_SINGUTS = new FoodProperties.Builder()
            .nutrition(8)
            .saturationModifier(0.1F)
            .effect(new MobEffectInstance(MobEffects.WITHER, 200, 1), 1.0F)
            .effect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 0), 1.0F)
            .build();
    public static final FoodProperties CLEANSED_SINGUTS = new FoodProperties.Builder()
            .nutrition(5)
            .saturationModifier(1.0F)
            .build();
}
