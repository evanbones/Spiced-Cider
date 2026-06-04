package com.evandev.spicedcider.client.events;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.client.music.DeathSoundInstance;
import com.evandev.spicedcider.registry.ModSounds;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.EmiStackInteraction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientGameEvents {

    private static boolean wasDead = false;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
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

    @SubscribeEvent
    public static void onKeyPressed(ScreenEvent.KeyPressed.Pre event) {
        if (FMLLoader.isProduction() || event.getKeyCode() != GLFW.GLFW_KEY_C) {
            return;
        }

        EmiStackInteraction interaction = EmiApi.getHoveredStack(true);

        if (interaction != null && !interaction.isEmpty()) {
            EmiIngredient ingredient = interaction.getStack();

            if (!ingredient.isEmpty() && !ingredient.getEmiStacks().isEmpty()) {
                EmiStack emiStack = ingredient.getEmiStacks().getFirst();
                ItemStack stack = emiStack.getItemStack();

                if (stack != null && !stack.isEmpty()) {
                    String descriptionId = stack.getItem().getDescriptionId();
                    String clipboardData = "\"%s\": \"%s\"".formatted(descriptionId, I18n.get(descriptionId));

                    Minecraft.getInstance().keyboardHandler.setClipboard(clipboardData);
                }
            }
        }
    }
}