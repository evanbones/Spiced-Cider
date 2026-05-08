package com.evandev.spicedcider.command;

import com.evandev.spicedcider.SpicedCider;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceOrTagArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpicedCiderStructureCommand {

    public static final Map<ResourceLocation, Long> FEATURE_TIMINGS = new ConcurrentHashMap<>();
    public static final Map<ResourceLocation, Long> STRUCTURE_TIMINGS = new ConcurrentHashMap<>();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        dispatcher.register(Commands.literal("spicedcider_structures")
                .then(Commands.literal("getBiomeTags")
                        .then(Commands.argument("biome", ResourceOrTagArgument.resourceOrTag(buildContext, Registries.BIOME))
                                .executes(context -> {
                                    ResourceKey<Biome> biomeKey = ResourceOrTagArgument.getResourceOrTag(context, "biome", Registries.BIOME).unwrap().left().get().key();
                                    Registry<Biome> registry = context.getSource().registryAccess().registryOrThrow(Registries.BIOME);
                                    Holder.Reference<Biome> holder = registry.getHolderOrThrow(biomeKey);

                                    List<TagKey<Biome>> biomeTags = holder.tags().toList();
                                    context.getSource().sendSystemMessage(Component.literal("Biome tags for: " + biomeKey.location()).withStyle(ChatFormatting.GOLD));

                                    for (TagKey<Biome> tag : biomeTags) {
                                        context.getSource().sendSystemMessage(Component.literal("#" + tag.location()));
                                    }
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("showGenerationTimes")
                        .executes(context -> {
                            printTimings(context.getSource(), "Features", FEATURE_TIMINGS);
                            printTimings(context.getSource(), "Structures", STRUCTURE_TIMINGS);
                            return 1;
                        })
                )
        );
    }

    private static void printTimings(CommandSourceStack source, String type, Map<ResourceLocation, Long> map) {
        List<Map.Entry<ResourceLocation, Long>> sorted = new ArrayList<>(map.entrySet());
        sorted.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        source.sendSystemMessage(Component.literal(type + " timings:").withStyle(ChatFormatting.GOLD));
        SpicedCider.LOGGER.info("{} timings in ms:", type);

        int count = 0;
        for (Map.Entry<ResourceLocation, Long> entry : sorted) {
            long ms = entry.getValue() / 1_000_000L;
            if (++count < 5) {
                source.sendSystemMessage(Component.literal("#" + count + " id: " + entry.getKey() + " time: " + ms + "ms").withStyle(ChatFormatting.WHITE));
            }
            SpicedCider.LOGGER.info("#{}: {} time: {}ms", count, entry.getKey(), ms);
        }
    }
}