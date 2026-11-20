package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class StepUpEvents {
    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "step_up");
    private static final AttributeModifier STEP_UP = new AttributeModifier(ID, 0.4, AttributeModifier.Operation.ADD_VALUE);

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        AttributeInstance stepHeight = player.getAttribute(Attributes.STEP_HEIGHT);
        if (stepHeight == null) return;
        boolean wanted = !player.isShiftKeyDown() && SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.stepUp, false)
                && (!player.level().isClientSide() || SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.stepUpEnabled, true));
        boolean present = stepHeight.hasModifier(ID);
        if (wanted && !present) {
            stepHeight.addTransientModifier(STEP_UP);
        } else if (!wanted && present) {
            stepHeight.removeModifier(ID);
        }
    }
}
