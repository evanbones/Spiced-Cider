package com.evandev.spicedcider.compat.ponder;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.reliableremover.ReliableRemoverCompat;
import com.evandev.spicedcider.config.SpicedCiderConfig;
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
            ResourceLocation.fromNamespaceAndPath("create", "creative_motor_mojang"),
            ResourceLocation.fromNamespaceAndPath("create", "mechanical_saw/breaker"),
            ResourceLocation.fromNamespaceAndPath("create", "mechanical_saw/contraption")
    );

    public static final ResourceLocation VAULT_STORAGE = ResourceLocation.fromNamespaceAndPath("create", "item_vault/storage");

    private static boolean isReplacedVaultScene(StoryBoardEntry entry) {
        if (!VAULT_STORAGE.equals(entry.getSchematicLocation())) return false;
        boolean vaultInterface = SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.itemVaultInterface, false);
        return SpicedCider.MOD_ID.equals(entry.getNamespace()) != vaultInterface;
    }

    public static boolean isRemoved(ResourceLocation component) {
        if (!ReliableRemoverCompat.isLoaded()) return false;
        Item item = BuiltInRegistries.ITEM.get(component);
        return item != Items.AIR && ReliableRemoverCompat.isRemoved(item);
    }

    public static boolean isShown(StoryBoardEntry entry) {
        return !HIDDEN_SCHEMATICS.contains(entry.getSchematicLocation()) && !isReplacedVaultScene(entry) && !isRemoved(entry.getComponent());
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
