package com.evandev.spicedcider.content.loot;

import com.evandev.spicedcider.registry.ModDataMaps;
import com.evandev.spicedcider.registry.ModItems;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class CleaverSkullDropLootModifier extends LootModifier {
    public static final MapCodec<CleaverSkullDropLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
            codecStart(instance).apply(instance, CleaverSkullDropLootModifier::new));

    public CleaverSkullDropLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(@NotNull ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        DamageSource damageSource = context.getParamOrNull(LootContextParams.DAMAGE_SOURCE);
        ItemStack weapon = damageSource != null ? damageSource.getWeaponItem() : null;
        if (weapon == null || !weapon.is(ModItems.CLEAVER_OF_BEHEADING.get())) {
            return generatedLoot;
        }

        Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (entity == null) {
            return generatedLoot;
        }

        ModDataMaps.SkullDrop skullDrop = entity.getType().builtInRegistryHolder().getData(ModDataMaps.SKULL_DROPS);
        if (skullDrop == null) {
            return generatedLoot;
        }

        generatedLoot.removeIf(stack -> stack.is(skullDrop.skull()));
        if (context.getRandom().nextFloat() < skullDrop.chance()) {
            generatedLoot.add(new ItemStack(skullDrop.skull()));
        }
        return generatedLoot;
    }

    @Override
    public @NotNull MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
