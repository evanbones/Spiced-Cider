package com.evandev.spicedcider.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class CopperPatina {

    public static final String[] STAGES = {"exposed_", "weathered_", "oxidized_"};
    private static final Pattern COPPER = Pattern.compile("(?<=^|_)copper(?=_|\\.)");
    private static final Map<ResourceLocation, Boolean> EXISTS = new ConcurrentHashMap<>();

    public static int stage(ItemStack stack) {
        if (!stack.isDamageableItem() || stack.getMaxDamage() <= 0) return -1;
        float damage = (float) stack.getDamageValue() / stack.getMaxDamage();
        return damage >= 0.75F ? 2 : damage >= 0.5F ? 1 : damage >= 0.25F ? 0 : -1;
    }

    public static ResourceLocation texture(ResourceLocation texture, ItemStack stack) {
        int stage = stage(stack);
        if (stage < 0) return texture;

        String path = texture.getPath();
        int slash = path.lastIndexOf('/') + 1;
        String file = path.substring(slash);
        String aged = COPPER.matcher(file).replaceFirst(STAGES[stage] + "copper");
        if (aged.equals(file)) return texture;

        ResourceLocation agedTexture = ResourceLocation.fromNamespaceAndPath(texture.getNamespace(), path.substring(0, slash) + aged);
        return exists(agedTexture) ? agedTexture : texture;
    }

    public static void clearCache() {
        EXISTS.clear();
    }

    private static boolean exists(ResourceLocation texture) {
        return EXISTS.computeIfAbsent(texture, key -> Minecraft.getInstance().getResourceManager().getResource(key).isPresent());
    }
}
