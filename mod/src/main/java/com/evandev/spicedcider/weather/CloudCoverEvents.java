package com.evandev.spicedcider.weather;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.networking.CloudCoverPayload;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class CloudCoverEvents {

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel level && level.dimension() == Level.OVERWORLD) {
            CloudCoverData.get(level).tick(level);
        }
    }

    @SubscribeEvent
    public static void onLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            CloudCover cover = CloudCoverData.get(player.server.overworld()).cover();
            PacketDistributor.sendToPlayer(player, new CloudCoverPayload(cover, false));
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        LiteralArgumentBuilder<CommandSourceStack> weather = Commands.literal("weather").requires(source -> source.hasPermission(2));
        for (CloudCover cover : CloudCover.values()) {
            weather.then(Commands.literal(cover.getSerializedName())
                    .executes(context -> set(context.getSource(), cover, -1))
                    .then(Commands.argument("duration", TimeArgument.time(1))
                            .executes(context -> set(context.getSource(), cover, IntegerArgumentType.getInteger(context, "duration")))));
        }
        dispatcher.register(weather);
    }

    private static int set(CommandSourceStack source, CloudCover cover, int time) {
        ServerLevel overworld = source.getServer().overworld();
        int clearTime = time == -1 ? ServerLevel.RAIN_DELAY.sample(overworld.getRandom()) : time;
        int coverTime = time == -1 ? cover.sampleDuration(overworld.getRandom()) : time;
        overworld.setWeatherParameters(clearTime, 0, false, false);
        CloudCoverData.get(overworld).command(overworld, cover, coverTime);
        source.sendSuccess(() -> Component.translatable("commands.weather.set." + cover.getSerializedName()), true);
        return time;
    }
}
