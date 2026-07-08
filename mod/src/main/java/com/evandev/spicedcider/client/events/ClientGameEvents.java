package com.evandev.spicedcider.client.events;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.client.music.DeathSoundInstance;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.registry.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public class ClientGameEvents {
    private static boolean wasDead = false;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (!SpicedCiderConfig.CLIENT.customDeathSound.get()) return;

        Minecraft mc = Minecraft.getInstance();

        if (mc.player != null) {
            boolean isDead = mc.player.isDeadOrDying();

            if (isDead && !wasDead) {
                DeathSoundInstance deathSound = new DeathSoundInstance(ModSounds.PLAYER_DEATH.get(), 200);
                mc.getSoundManager().play(deathSound);
            }
            wasDead = isDead;
        } else {
            wasDead = false;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (!SpicedCiderConfig.CLIENT.hideMelancholicHungerTooltip.get()) return;

        event.getToolTip().removeIf(component ->
                component.getContents() instanceof TranslatableContents translatable &&
                        translatable.getKey().startsWith("gui.melancholic_hunger.regeneration_tooltip.")
        );
    }
}