package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class ModModelLayers {
    public static final ModelLayerLocation COBWEB_PROJECTILE = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "cobweb_projectile"), "main"
    );
}
