package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.loot.SpawnedBySpawnerCondition;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModLootConditions {
    public static final DeferredRegister<LootItemConditionType> LOOT_CONDITION_TYPES =
            DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, SpicedCider.MOD_ID);

    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> SPAWNED_BY_SPAWNER =
            LOOT_CONDITION_TYPES.register("spawned_by_spawner", () -> new LootItemConditionType(SpawnedBySpawnerCondition.CODEC));
}
