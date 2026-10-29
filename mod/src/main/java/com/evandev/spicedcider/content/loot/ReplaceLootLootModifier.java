package com.evandev.spicedcider.content.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ReplaceLootLootModifier extends LootModifier {
    public static final MapCodec<ReplaceLootLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
            codecStart(instance)
                    .and(BuiltInRegistries.ITEM.byNameCodec().optionalFieldOf("item").forGetter(m -> m.item))
                    .and(ResourceKey.codec(Registries.LOOT_TABLE).optionalFieldOf("table").forGetter(m -> m.table))
                    .and(Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter(m -> m.chance))
                    .apply(instance, ReplaceLootLootModifier::new));

    private final Optional<Item> item;
    private final Optional<ResourceKey<LootTable>> table;
    private final float chance;

    public ReplaceLootLootModifier(LootItemCondition[] conditions, Optional<Item> item, Optional<ResourceKey<LootTable>> table, float chance) {
        super(conditions);
        this.item = item;
        this.table = table;
        this.chance = chance;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(@NotNull ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (context.getRandom().nextFloat() >= chance) return generatedLoot;
        ObjectArrayList<ItemStack> replacement = new ObjectArrayList<>();
        item.ifPresent(i -> replacement.add(new ItemStack(i)));
        table.flatMap(key -> context.getResolver().get(Registries.LOOT_TABLE, key)).ifPresent(extra ->
                extra.value().getRandomItemsRaw(context, LootTable.createStackSplitter(context.getLevel(), replacement::add)));
        return replacement.isEmpty() ? generatedLoot : replacement;
    }

    @Override
    public @NotNull MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
