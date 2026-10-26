package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.item.PocketJukeboxContents;
import com.evandev.spicedcider.content.item.PocketJukeboxItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class PocketJukeboxEvents {
    private static final Map<UUID, Integer> SESSIONS = new HashMap<>();

    public static void use(ServerPlayer player, ItemStack stack, boolean shuffle) {
        PocketJukeboxContents contents = PocketJukeboxItem.contents(stack);
        long now = player.level().getGameTime();
        RandomSource random = player.getRandom();
        if (shuffle) {
            start(player, stack, contents.start(contents.randomDisc(random), 0, now, random));
        } else if (contents.playing()) {
            PocketJukeboxItem.setContents(stack, contents.pause(now));
            SESSIONS.remove(player.getUUID());
            player.displayClientMessage(Component.translatable("item.spicedcider.pocket_jukebox.paused"), true);
        } else if (contents.current() >= 0) {
            start(player, stack, contents.start(contents.current(), contents.progress(), now, random));
        } else {
            start(player, stack, contents.start(contents.randomDisc(random), 0, now, random));
        }
    }

    private static void start(ServerPlayer player, ItemStack stack, PocketJukeboxContents contents) {
        long now = player.level().getGameTime();
        forEachJukebox(player, other -> {
            PocketJukeboxContents otherContents = PocketJukeboxItem.contents(other);
            if (other != stack && otherContents.playing()) PocketJukeboxItem.setContents(other, otherContents.pause(now));
        });
        PocketJukeboxItem.setContents(stack, contents);
        SESSIONS.put(player.getUUID(), contents.session());
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        Integer session = SESSIONS.get(player.getUUID());
        long now = player.level().getGameTime();
        boolean[] found = {false};
        forEachJukebox(player, stack -> {
            PocketJukeboxContents contents = PocketJukeboxItem.contents(stack);
            if (!contents.playing()) return;
            Optional<JukeboxSong> song = contents.currentSong(player.registryAccess());
            if (session == null || contents.session() != session || found[0] || song.isEmpty()) {
                PocketJukeboxItem.setContents(stack, contents.stop());
                return;
            }
            found[0] = true;
            if (contents.elapsed(now) >= song.get().lengthInTicks()) {
                PocketJukeboxContents next = contents.start(contents.randomDisc(player.getRandom()), 0, now, player.getRandom());
                PocketJukeboxItem.setContents(stack, next);
                SESSIONS.put(player.getUUID(), next.session());
            }
        });
        if (session != null && !found[0]) SESSIONS.remove(player.getUUID());
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        SESSIONS.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        SESSIONS.clear();
    }

    private static void forEachJukebox(ServerPlayer player, Consumer<ItemStack> action) {
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.getItem() instanceof PocketJukeboxItem) action.accept(stack);
        }
        ItemStack carried = player.containerMenu.getCarried();
        if (carried.getItem() instanceof PocketJukeboxItem) action.accept(carried);
    }
}
