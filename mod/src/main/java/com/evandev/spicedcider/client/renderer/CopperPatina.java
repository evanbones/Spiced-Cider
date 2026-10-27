package com.evandev.spicedcider.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.regex.Pattern;

public class CopperPatina {

    private static final String[] STAGES = {"exposed_", "weathered_", "oxidized_"};
    private static final Pattern COPPER = Pattern.compile("(?<=^|_)copper(?=_|\\.)");

    public static ResourceLocation texture(ResourceLocation texture, ItemStack stack) {
        if (!stack.isDamageableItem() || stack.getMaxDamage() <= 0) return texture;

        float damage = (float) stack.getDamageValue() / stack.getMaxDamage();
        int stage = damage >= 0.75F ? 2 : damage >= 0.5F ? 1 : damage >= 0.25F ? 0 : -1;
        if (stage < 0) return texture;

        String path = texture.getPath();
        int slash = path.lastIndexOf('/') + 1;
        String file = path.substring(slash);
        String aged = COPPER.matcher(file).replaceFirst(STAGES[stage] + "copper");
        if (aged.equals(file)) return texture;

        return ResourceLocation.fromNamespaceAndPath(texture.getNamespace(), path.substring(0, slash) + aged);
    }
}
