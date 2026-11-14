package com.evandev.spicedcider.wind;

import com.evandev.spicedcider.SpicedCider;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Locale;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class WindEvents {
    private static final String[] COMPASS = {"south", "southwest", "west", "northwest", "north", "northeast", "east", "southeast"};

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel level && Wind.enabled()) WindData.get(level).tick(level);
    }

    @SubscribeEvent
    public static void onLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        send(event);
    }

    @SubscribeEvent
    public static void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        send(event);
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        send(event);
    }

    private static void send(PlayerEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && Wind.enabled()) {
            PacketDistributor.sendToPlayer(player, WindData.get(player.serverLevel()).payload());
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        if (!Wind.enabled()) return;
        event.getDispatcher().register(Commands.literal("wind")
                .requires(source -> source.hasPermission(2))
                .executes(context -> query(context.getSource()))
                .then(Commands.literal("strength")
                        .then(Commands.argument("strength", FloatArgumentType.floatArg(0.0F, WindSample.MAX_STRENGTH))
                                .executes(context -> strength(context.getSource(), FloatArgumentType.getFloat(context, "strength"), 0))
                                .then(Commands.argument("duration", TimeArgument.time(1))
                                        .executes(context -> strength(context.getSource(), FloatArgumentType.getFloat(context, "strength"), IntegerArgumentType.getInteger(context, "duration"))))))
                .then(Commands.literal("direction")
                        .then(Commands.argument("degrees", FloatArgumentType.floatArg(-360.0F, 360.0F))
                                .executes(context -> direction(context.getSource(), FloatArgumentType.getFloat(context, "degrees")))))
                .then(Commands.literal("gust")
                        .executes(context -> gust(context.getSource(), 0.5F))
                        .then(Commands.argument("strength", FloatArgumentType.floatArg(0.0F, WindSample.MAX_STRENGTH))
                                .executes(context -> gust(context.getSource(), FloatArgumentType.getFloat(context, "strength")))))
                .then(Commands.literal("reset")
                        .executes(context -> reset(context.getSource()))));
    }

    private static int query(CommandSourceStack source) {
        WindSample sample = Wind.sample(source.getLevel());
        String strength = String.format(Locale.ROOT, "%.2f", sample.strength());
        String compass = COMPASS[Math.floorMod(Math.round(sample.headingDegrees() / 45.0F), 8)];
        source.sendSuccess(() -> Component.translatable("commands.spicedcider.wind.query", strength,
                Component.translatable("commands.spicedcider.wind.compass." + compass)), false);
        return Math.round(sample.strength() * 100.0F);
    }

    private static int strength(CommandSourceStack source, float strength, int ticks) {
        WindData.get(source.getLevel()).force(source.getLevel(), strength, ticks);
        source.sendSuccess(() -> Component.translatable("commands.spicedcider.wind.strength", String.format(Locale.ROOT, "%.2f", strength)), true);
        return 1;
    }

    private static int direction(CommandSourceStack source, float degrees) {
        WindData.get(source.getLevel()).turn(source.getLevel(), degrees);
        source.sendSuccess(() -> Component.translatable("commands.spicedcider.wind.direction", String.format(Locale.ROOT, "%.0f", degrees)), true);
        return 1;
    }

    private static int gust(CommandSourceStack source, float strength) {
        WindData.get(source.getLevel()).gust(source.getLevel(), strength);
        source.sendSuccess(() -> Component.translatable("commands.spicedcider.wind.gust"), true);
        return 1;
    }

    private static int reset(CommandSourceStack source) {
        WindData.get(source.getLevel()).release(source.getLevel());
        source.sendSuccess(() -> Component.translatable("commands.spicedcider.wind.reset"), true);
        return 1;
    }
}
