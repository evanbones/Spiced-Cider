package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.loot.BonusBlockDropLootModifier;
import com.evandev.spicedcider.content.loot.CleaverSkullDropLootModifier;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModLootModifiers {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, SpicedCider.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<CleaverSkullDropLootModifier>> CLEAVER_SKULL_DROP =
            LOOT_MODIFIER_SERIALIZERS.register("cleaver_skull_drop", () -> CleaverSkullDropLootModifier.CODEC);

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<BonusBlockDropLootModifier>> BONUS_BLOCK_DROP =
            LOOT_MODIFIER_SERIALIZERS.register("bonus_block_drop", () -> BonusBlockDropLootModifier.CODEC);
}
