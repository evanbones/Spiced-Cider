package com.evandev.spicedcider.compat.ponder;

import com.evandev.spicedcider.compat.reliableremover.ReliableRemoverCompat;
import net.createmod.ponder.api.registration.StoryBoardEntry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class PonderFilter {

    private static final Set<ResourceLocation> HIDDEN_SCHEMATICS = Set.of(
            ResourceLocation.fromNamespaceAndPath("create", "chute/upward"),
            ResourceLocation.fromNamespaceAndPath("create", "creative_motor_mojang")
    );

    public static boolean isRemoved(ResourceLocation component) {
        if (!ReliableRemoverCompat.isLoaded()) return false;
        Item item = BuiltInRegistries.ITEM.get(component);
        return item != Items.AIR && ReliableRemoverCompat.isRemoved(item);
    }

    public static boolean isShown(StoryBoardEntry entry) {
        return !HIDDEN_SCHEMATICS.contains(entry.getSchematicLocation()) && !isRemoved(entry.getComponent());
    }

    public static List<StoryBoardEntry> shown(Collection<StoryBoardEntry> entries) {
        return entries.stream().filter(PonderFilter::isShown).toList();
    }

    public static List<Map.Entry<ResourceLocation, StoryBoardEntry>> shownEntries(Collection<Map.Entry<ResourceLocation, StoryBoardEntry>> entries) {
        return entries.stream().filter(entry -> isShown(entry.getValue())).toList();
    }

    public static Set<ResourceLocation> shownComponents(Set<ResourceLocation> components) {
        return components.stream().filter(component -> !isRemoved(component)).collect(Collectors.toUnmodifiableSet());
    }
}
