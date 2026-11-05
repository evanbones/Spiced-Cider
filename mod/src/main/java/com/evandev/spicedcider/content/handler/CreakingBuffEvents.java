package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class CreakingBuffEvents {
    private static final ResourceLocation CREAKING = ResourceLocation.withDefaultNamespace("creaking");
    private static final ResourceLocation SPEED_ID = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "creaking_speed_buff");
    private static final ResourceLocation DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "creaking_damage_buff");

    @SubscribeEvent
    public static void onJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || !(event.getEntity() instanceof LivingEntity entity)) return;
        if (!CREAKING.equals(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()))) return;
        boolean enabled = SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.creakingBuff, false);
        apply(entity, Attributes.MOVEMENT_SPEED, SPEED_ID, 0.05, enabled);
        apply(entity, Attributes.ATTACK_DAMAGE, DAMAGE_ID, 3.0, enabled);
    }

    private static void apply(LivingEntity entity, Holder<Attribute> attribute, ResourceLocation id, double amount, boolean enabled) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return;
        if (enabled) {
            instance.addOrUpdateTransientModifier(new AttributeModifier(id, amount, AttributeModifier.Operation.ADD_VALUE));
        } else {
            instance.removeModifier(id);
        }
    }
}
