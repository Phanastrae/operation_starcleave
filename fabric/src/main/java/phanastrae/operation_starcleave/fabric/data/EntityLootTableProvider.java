package phanastrae.operation_starcleave.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static net.minecraft.world.item.Items.GOLD_NUGGET;
import static phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes.STARCLEAVER_GOLEM;
import static phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes.SUBCAELIC_TORPEDO;
import static phanastrae.operation_starcleave.item.OperationStarcleaveItems.HOLLOWED_SAC;
import static phanastrae.operation_starcleave.item.OperationStarcleaveItems.SUBCAELIC_PHLOGLIGHT;

public class EntityLootTableProvider extends SimpleFabricLootTableProvider {

    private final CompletableFuture<HolderLookup.Provider> registryLookup;

    public EntityLootTableProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup, LootContextParamSets.ENTITY);
        this.registryLookup = registryLookup;
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> lootTableBiConsumer) {
        // generate() should be getting run inside of run() which should have got the registryLookup by the time this gets run
        HolderLookup.Provider registries = this.registryLookup.getNow(null);
        Objects.requireNonNull(registries);

        lootTableBiConsumer.accept(STARCLEAVER_GOLEM.getDefaultLootTable(), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(GOLD_NUGGET)))
        );

        lootTableBiConsumer.accept(SUBCAELIC_TORPEDO.getDefaultLootTable(), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(HOLLOWED_SAC)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0.0F, 1.0F)))
                                .when(this.killedByFrog().invert())
                        )
                        .add(LootItem.lootTableItem(SUBCAELIC_PHLOGLIGHT)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .when(this.killedByFrog())
                        )
                )
        );
    }

    protected LootItemCondition.Builder killedByFrog() {
        return DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().source(EntityPredicate.Builder.entity().of(EntityType.FROG)));
    }
}
