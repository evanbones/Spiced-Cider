package com.evandev.spicedcider.client.music;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.musictweaks.MusicTweaksOffset;
import com.evandev.spicedcider.content.item.PocketJukeboxContents;
import com.evandev.spicedcider.content.item.PocketJukeboxItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.Optional;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public class PocketJukeboxClient {
    private static final boolean MUSIC_TWEAKS = ModList.get().isLoaded("music_tweaks");

    private static PocketJukeboxSound sound;
    private static Integer session;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        PocketJukeboxContents playing = minecraft.player != null && minecraft.level != null ? find(minecraft.player) : null;
        if (playing == null) {
            stop(minecraft);
            return;
        }
        if (!MUSIC_TWEAKS) minecraft.getMusicManager().stopPlaying();
        if (session != null && session == playing.session()) return;
        stop(minecraft);
        session = playing.session();
        Optional<JukeboxSong> song = playing.currentSong(minecraft.level.registryAccess());
        if (song.isEmpty()) return;
        float offset = playing.elapsed(minecraft.level.getGameTime()) / 20.0F;
        if (offset >= song.get().lengthInSeconds()) return;
        sound = new PocketJukeboxSound(song.get().soundEvent().value());
        if (MUSIC_TWEAKS && offset > 0.5F) MusicTweaksOffset.skip(offset);
        minecraft.getSoundManager().play(sound);
        minecraft.gui.setNowPlaying(song.get().description());
    }

    public static float playingProperty(ItemStack stack, ClientLevel level, LivingEntity entity, int seed) {
        return PocketJukeboxItem.contents(stack).playing() ? 1.0F : 0.0F;
    }

    private static PocketJukeboxContents find(LocalPlayer player) {
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            PocketJukeboxContents contents = playing(inventory.getItem(i));
            if (contents != null) return contents;
        }
        return playing(player.containerMenu.getCarried());
    }

    private static PocketJukeboxContents playing(ItemStack stack) {
        if (!(stack.getItem() instanceof PocketJukeboxItem)) return null;
        PocketJukeboxContents contents = PocketJukeboxItem.contents(stack);
        return contents.playing() ? contents : null;
    }

    private static void stop(Minecraft minecraft) {
        if (sound != null) {
            sound.halt();
            minecraft.getSoundManager().stop(sound);
            sound = null;
        }
        session = null;
    }
}
