package com.evandev.spicedcider.content.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MusicDiscSourcesLootModifier extends LootModifier {
    public record Source(List<ResourceLocation> tables, Optional<Float> chance) {
        public static final Codec<Source> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.listOf().fieldOf("tables").forGetter(Source::tables),
                Codec.floatRange(0.0F, 1.0F).optionalFieldOf("chance").forGetter(Source::chance)
        ).apply(instance, Source::new));
    }

    public static final MapCodec<MusicDiscSourcesLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
            codecStart(instance)
                    .and(Codec.unboundedMap(ResourceLocation.CODEC, Source.CODEC).fieldOf("discs").forGetter(m -> m.discs))
                    .apply(instance, MusicDiscSourcesLootModifier::new));

    private final Map<ResourceLocation, Source> discs;

    public MusicDiscSourcesLootModifier(LootItemCondition[] conditions, Map<ResourceLocation, Source> discs) {
        super(conditions);
        this.discs = discs;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(@NotNull ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        ResourceLocation table = context.getQueriedLootTableId();

        generatedLoot.removeIf(stack -> {
            if (!stack.has(DataComponents.JUKEBOX_PLAYABLE)) return false;
            Source source = discs.get(BuiltInRegistries.ITEM.getKey(stack.getItem()));
            return source == null || source.chance().isPresent() || !source.tables().contains(table);
        });

        discs.forEach((disc, source) -> {
            if (source.chance().isEmpty() || !source.tables().contains(table)) return;
            if (context.getRandom().nextFloat() >= source.chance().get()) return;
            BuiltInRegistries.ITEM.getOptional(disc).ifPresent(item -> generatedLoot.add(new ItemStack(item)));
        });

        return generatedLoot;
    }

    @Override
    public @NotNull MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
