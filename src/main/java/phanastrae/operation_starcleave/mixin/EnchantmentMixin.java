package phanastrae.operation_starcleave.mixin;

import net.minecraft.component.ComponentType;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.effect.EnchantmentEffectEntry;
import net.minecraft.enchantment.effect.EnchantmentValueEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.entity.OperationStarcleaveDamageTypeTags;

import java.util.List;

import static net.minecraft.enchantment.Enchantment.createEnchantedDamageLootContext;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {

    @Shadow public abstract <T> List<T> getEffect(ComponentType<List<T>> type);

    @Inject(method = "modifyDamageProtection", at = @At("RETURN"))
    private void operation_starcleave$treatPhlogisticAsFire(ServerWorld world, int level, ItemStack stack, Entity user, DamageSource damageSource, MutableFloat damageProtection, CallbackInfo ci) {
        if(damageSource.isIn(OperationStarcleaveDamageTypeTags.IS_PHLOGISTIC_FIRE) && !damageSource.isIn(DamageTypeTags.IS_FIRE)) {
            RegistryEntry<DamageType> fire = world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(DamageTypes.ON_FIRE);
            DamageSource altSource = new DamageSource(fire, damageSource.getSource(), damageSource.getAttacker());
            LootContext altLootContext = createEnchantedDamageLootContext(world, level, user, altSource);

            for (EnchantmentEffectEntry<EnchantmentValueEffect> enchantmentEffectEntry : this.getEffect(EnchantmentEffectComponentTypes.DAMAGE_PROTECTION)) {
                if (enchantmentEffectEntry.test(altLootContext)) {
                    float f = enchantmentEffectEntry.effect().apply(level, user.getRandom(), damageProtection.floatValue());
                    if(f > damageProtection.floatValue()) {
                        damageProtection.setValue(f);
                    }
                }
            }
        }
    }
}
