package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.effect.LureEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMobEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, SpicedCider.MOD_ID);

    public static final DeferredHolder<MobEffect, MobEffect> LURE = MOB_EFFECTS.register("lure", LureEffect::new);

    public static final DeferredHolder<MobEffect, MobEffect> SPORAL_SPEED = MOB_EFFECTS.register("sporal_speed", () ->
            new SporalEffect(0xD7A329).addAttributeModifier(Attributes.MOVEMENT_SPEED,
                    ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "sporal_speed"), 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static final DeferredHolder<MobEffect, MobEffect> SPORAL_STRENGTH = MOB_EFFECTS.register("sporal_strength", () ->
            new SporalEffect(0xA64022).addAttributeModifier(Attributes.ATTACK_DAMAGE,
                    ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "sporal_strength"), 3.0, AttributeModifier.Operation.ADD_VALUE));

    private static class SporalEffect extends MobEffect {
        SporalEffect(int color) {
            super(MobEffectCategory.BENEFICIAL, color);
        }
    }
}
