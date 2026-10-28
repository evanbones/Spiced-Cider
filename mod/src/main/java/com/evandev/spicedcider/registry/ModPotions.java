package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(Registries.POTION, SpicedCider.MOD_ID);

    public static final DeferredHolder<Potion, Potion> TAINTED = POTIONS.register("tainted", () -> new Potion(
            new MobEffectInstance(MobEffects.CONFUSION, 200), new MobEffectInstance(MobEffects.HUNGER, 600)));
    public static final DeferredHolder<Potion, Potion> HASTE = POTIONS.register("haste", () -> new Potion(
            new MobEffectInstance(MobEffects.DIG_SPEED, 3600)));

    static {
        if (ModList.get().isLoaded("neapolitan")) {
            effectPotion("agility", ResourceLocation.fromNamespaceAndPath("neapolitan", "agility"));
            effectPotion("harmony", ResourceLocation.fromNamespaceAndPath("neapolitan", "harmony"));
        }
    }

    private static void effectPotion(String name, ResourceLocation effect) {
        POTIONS.register(name, () -> {
            Holder<MobEffect> holder = BuiltInRegistries.MOB_EFFECT.getHolder(effect).orElseThrow();
            return new Potion(new MobEffectInstance(holder, 3600));
        });
    }
}
