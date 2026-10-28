package com.evandev.spicedcider.content.brewing;

import com.evandev.spicedcider.registry.ModDataMaps;
import com.evandev.spicedcider.registry.ModPotions;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CauldronBrewing {
    public static final int BASE_SECONDS = 150;
    public static final int MAX_SECONDS = 1200;
    public static final int MAX_INGREDIENTS = 5;
    public static final int TIER_TWO = 30;
    public static final int TIER_THREE = 45;
    public static final int TAINTED_COLOR = 0x6B6A4A;
    public static final int CLEAR_COLOR = 0xC6E4F7;

    @Nullable
    public static ModDataMaps.BrewingIngredient ingredient(ItemStack stack) {
        return stack.getItemHolder().getData(ModDataMaps.BREWING_INGREDIENTS);
    }

    public static boolean isWater(Optional<Holder<Potion>> potion) {
        return potion.isPresent() && potion.get().is(Potions.WATER);
    }

    public static boolean isClear(Optional<Holder<Potion>> potion) {
        return potion.isPresent() && potion.get().is(Potions.AWKWARD);
    }

    public static boolean isTainted(Optional<Holder<Potion>> potion) {
        return potion.isPresent() && potion.get().is(ModPotions.TAINTED.getKey());
    }

    public static boolean same(Holder<Potion> a, Holder<Potion> b) {
        return a.unwrapKey().isPresent() && a.unwrapKey().equals(b.unwrapKey());
    }

    public static int tier(int potency) {
        return potency >= TIER_THREE ? 2 : potency >= TIER_TWO ? 1 : 0;
    }

    public static int durationTicks(int seconds) {
        return Math.min(BASE_SECONDS + seconds, MAX_SECONDS) * 20;
    }

    public static List<MobEffectInstance> effects(Holder<Potion> potion, int seconds, int potency) {
        int tier = tier(potency);
        List<MobEffectInstance> effects = new ArrayList<>();
        for (MobEffectInstance base : potion.value().getEffects()) {
            boolean instant = base.getEffect().value().isInstantenous();
            effects.add(new MobEffectInstance(base.getEffect(), instant ? 1 : durationTicks(seconds), base.getAmplifier() + tier,
                    base.isAmbient(), base.isVisible(), base.showIcon()));
        }
        return effects;
    }
}
