package com.evandev.spicedcider.lodgen;

import com.evandev.spicedcider.SpicedCider;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class LodGenEvents {

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        LodGen.start(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        LodGen.stop();
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        LodGen.tick(event.getServer());
    }

    @SubscribeEvent
    public static void onLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) LodGen.forget(player);
    }

    @SubscribeEvent
    public static void onChunkSent(ChunkWatchEvent.Sent event) {
        LodGen.chunkSent(event.getPlayer(), event.getPos());
    }

    @SubscribeEvent
    public static void onChunkUnwatched(ChunkWatchEvent.UnWatch event) {
        LodGen.chunkUnwatched(event.getPlayer(), event.getLevel(), event.getPos());
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("lodgen")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("status").executes(context -> status(context.getSource())))
                .then(Commands.literal("stats").then(Commands.literal("reset").executes(context -> {
                    LodStats.reset();
                    return 1;
                })))
                .then(Commands.literal("reset")
                        .executes(context -> reset(context.getSource(), List.of(context.getSource().getPlayerOrException())))
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(context -> reset(context.getSource(), EntityArgument.getPlayers(context, "targets"))))));
    }

    private static int status(CommandSourceStack source) {
        source.sendSuccess(() -> Component.literal(String.format(Locale.ROOT, "LoD generation: %.0f chunks/s", LodGen.rate())), false);
        for (String line : LodStats.lines()) source.sendSuccess(() -> Component.literal(line), false);
        int count = 0;
        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            if (!LodGen.tracked(player)) continue;
            count++;
            int remaining = LodGen.remaining(player.getUUID());
            int queued = LodGen.queued(player.getUUID());
            source.sendSuccess(() -> Component.literal(String.format(Locale.ROOT, "  %s: %s left, %d queued",
                    player.getGameProfile().getName(), remaining < 0 ? "?" : Integer.toString(remaining), queued)), false);
        }
        return count;
    }

    private static int reset(CommandSourceStack source, Collection<ServerPlayer> players) throws CommandSyntaxException {
        for (ServerPlayer player : players) LodGen.reset(player);
        source.sendSuccess(() -> Component.literal("Reset LoD generation for " + players.size() + " player(s)"), true);
        return players.size();
    }
}
