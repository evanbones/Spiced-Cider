package com.evandev.spicedcider.content.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class AutoSmeltLootModifier extends LootModifier {
    public static final MapCodec<AutoSmeltLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
            codecStart(instance)
                    .and(TagKey.codec(Registries.ITEM).fieldOf("tools").forGetter(m -> m.tools))
                    .and(TagKey.codec(Registries.ITEM).optionalFieldOf("exempt_items").forGetter(m -> m.exemptItems))
                    .and(TagKey.codec(Registries.BLOCK).optionalFieldOf("exempt_blocks").forGetter(m -> m.exemptBlocks))
                    .apply(instance, AutoSmeltLootModifier::new));

    private final TagKey<Item> tools;
    private final Optional<TagKey<Item>> exemptItems;
    private final Optional<TagKey<Block>> exemptBlocks;

    public AutoSmeltLootModifier(LootItemCondition[] conditions, TagKey<Item> tools, Optional<TagKey<Item>> exemptItems, Optional<TagKey<Block>> exemptBlocks) {
        super(conditions);
        this.tools = tools;
        this.exemptItems = exemptItems;
        this.exemptBlocks = exemptBlocks;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(@NotNull ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);
        ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);
        if (state == null || tool == null || tool.isEmpty() || !tool.is(tools)) return generatedLoot;
        if (exemptBlocks.isPresent() && state.is(exemptBlocks.get())) return generatedLoot;
        ServerLevel level = context.getLevel();
        if (EnchantmentHelper.getItemEnchantmentLevel(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH), tool) > 0) {
            return generatedLoot;
        }

        boolean smelted = false;
        ObjectArrayList<ItemStack> result = new ObjectArrayList<>(generatedLoot.size());
        for (ItemStack stack : generatedLoot) {
            ItemStack out = smelt(level, stack);
            if (out == stack) {
                result.add(stack);
                continue;
            }
            smelted = true;
            int total = out.getCount() * stack.getCount();
            while (total > 0) {
                int count = Math.min(total, out.getMaxStackSize());
                result.add(out.copyWithCount(count));
                total -= count;
            }
        }
        if (!smelted) return generatedLoot;

        Vec3 origin = context.getParamOrNull(LootContextParams.ORIGIN);
        if (origin != null) {
            BlockPos pos = BlockPos.containing(origin);
            level.sendParticles(ParticleTypes.FLAME, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 4, 0.25, 0.25, 0.25, 0.01);
            level.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 0.2F, 1.6F + level.random.nextFloat() * 0.3F);
        }
        return result;
    }

    private ItemStack smelt(ServerLevel level, ItemStack stack) {
        if (stack.isEmpty() || exemptItems.isPresent() && stack.is(exemptItems.get())) return stack;
        SingleRecipeInput input = new SingleRecipeInput(stack.copyWithCount(1));
        return level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, input, level)
                .map(holder -> holder.value().assemble(input, level.registryAccess()))
                .filter(out -> !out.isEmpty())
                .orElse(stack);
    }

    @Override
    public @NotNull MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
