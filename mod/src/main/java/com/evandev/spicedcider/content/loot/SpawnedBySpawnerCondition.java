package com.evandev.spicedcider.content.loot;

import com.evandev.spicedcider.registry.ModLootConditions;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.Set;

public record SpawnedBySpawnerCondition() implements LootItemCondition {
    public static final SpawnedBySpawnerCondition INSTANCE = new SpawnedBySpawnerCondition();
    public static final MapCodec<SpawnedBySpawnerCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public LootItemConditionType getType() {
        return ModLootConditions.SPAWNED_BY_SPAWNER.get();
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.THIS_ENTITY);
    }

    @Override
    public boolean test(LootContext context) {
        return context.getParamOrNull(LootContextParams.THIS_ENTITY) instanceof Mob mob && mob.getSpawnType() == MobSpawnType.SPAWNER;
    }
}
