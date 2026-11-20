package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.registry.ModTags;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class StaticHorseStatsEvents {
    private static final ResourceLocation SPEED_ID = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "static_horse_speed");
    private static final ResourceLocation JUMP_ID = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "static_horse_jump");

    @SubscribeEvent
    public static void onJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || !(event.getEntity() instanceof AbstractHorse horse)) return;
        if (!horse.getType().is(ModTags.EntityTypes.STATIC_HORSE_STATS)) return;
        boolean enabled = SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.staticHorseStats, false);
        apply(horse, Attributes.MOVEMENT_SPEED, SPEED_ID, enabled, SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.staticHorseSpeed, 0.3375));
        apply(horse, Attributes.JUMP_STRENGTH, JUMP_ID, enabled, SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.staticHorseJump, 1.0));
    }

    private static void apply(AbstractHorse horse, Holder<Attribute> attribute, ResourceLocation id, boolean enabled, double target) {
        AttributeInstance instance = horse.getAttribute(attribute);
        if (instance == null) return;
        if (enabled) {
            instance.addOrUpdateTransientModifier(new AttributeModifier(id, target - instance.getBaseValue(), AttributeModifier.Operation.ADD_VALUE));
        } else {
            instance.removeModifier(id);
        }
    }
}
