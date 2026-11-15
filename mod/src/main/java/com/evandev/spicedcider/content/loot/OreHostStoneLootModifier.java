package com.evandev.spicedcider.content.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OreHostStoneLootModifier extends LootModifier {
    public record Host(TagKey<Block> ores, ResourceLocation block) {
        public static final Codec<Host> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                TagKey.codec(Registries.BLOCK).fieldOf("ores").forGetter(Host::ores),
                ResourceLocation.CODEC.fieldOf("block").forGetter(Host::block)
        ).apply(instance, Host::new));
    }

    public static final MapCodec<OreHostStoneLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
            codecStart(instance)
                    .and(Host.CODEC.listOf().fieldOf("hosts").forGetter(m -> m.hosts))
                    .apply(instance, OreHostStoneLootModifier::new));

    private final List<Host> hosts;

    public OreHostStoneLootModifier(LootItemCondition[] conditions, List<Host> hosts) {
        super(conditions);
        this.hosts = hosts;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(@NotNull ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);
        if (state == null) return generatedLoot;

        Item self = state.getBlock().asItem();
        if (generatedLoot.stream().anyMatch(stack -> stack.is(self))) return generatedLoot;

        for (Host host : hosts) {
            if (!state.is(host.ores())) continue;
            Block block = BuiltInRegistries.BLOCK.getOptional(host.block()).orElse(null);
            if (block == null || block == state.getBlock()) return generatedLoot;
            context.getResolver().get(Registries.LOOT_TABLE, block.getLootTable()).ifPresent(table ->
                    table.value().getRandomItemsRaw(context, LootTable.createStackSplitter(context.getLevel(), generatedLoot::add)));
            return generatedLoot;
        }
        return generatedLoot;
    }

    @Override
    public @NotNull MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
