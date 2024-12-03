package phanastrae.operation_starcleave.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.LootContext;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.entity.OperationStarcleaveDamageTypeTags;

import java.util.List;

import static net.minecraft.world.item.enchantment.Enchantment.damageContext;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {

    @Shadow public abstract <T> List<T> getEffects(DataComponentType<List<T>> component);

    @Inject(method = "modifyDamageProtection", at = @At("RETURN"))
    private void operation_starcleave$treatPhlogisticAsFire(ServerLevel world, int level, ItemStack stack, Entity user, DamageSource damageSource, MutableFloat damageProtection, CallbackInfo ci) {
        if(damageSource.is(OperationStarcleaveDamageTypeTags.IS_PHLOGISTIC_FIRE) && !damageSource.is(DamageTypeTags.IS_FIRE)) {
            Holder<DamageType> fire = world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.ON_FIRE);
            DamageSource altSource = new DamageSource(fire, damageSource.getDirectEntity(), damageSource.getEntity());
            LootContext altLootContext = damageContext(world, level, user, altSource);

            for (ConditionalEffect<EnchantmentValueEffect> enchantmentEffectEntry : this.getEffects(EnchantmentEffectComponents.DAMAGE_PROTECTION)) {
                if (enchantmentEffectEntry.matches(altLootContext)) {
                    float f = enchantmentEffectEntry.effect().process(level, user.getRandom(), damageProtection.floatValue());
                    if(f > damageProtection.floatValue()) {
                        damageProtection.setValue(f);
                    }
                }
            }
        }
    }
}
