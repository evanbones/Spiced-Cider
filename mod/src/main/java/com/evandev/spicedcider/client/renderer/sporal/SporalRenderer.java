package com.evandev.spicedcider.client.renderer.sporal;

import com.evandev.spicedcider.client.models.sporal.SporalModel;
import com.evandev.spicedcider.registry.ModModelLayers;
import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.entities.sporal.Sporal;
import net.minecraft.Util;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public class SporalRenderer extends MobRenderer<Sporal, SporalModel> {
    private static final Map<Sporal.Variant, ResourceLocation> TEXTURES = Util.make(new EnumMap<>(Sporal.Variant.class), map -> {
        for (Sporal.Variant variant : Sporal.Variant.values()) {
            map.put(variant, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "textures/entity/sporal/sporal_" + variant.getSerializedName() + ".png"));
        }
    });

    public SporalRenderer(EntityRendererProvider.Context context) {
        super(context, new SporalModel(context.bakeLayer(ModModelLayers.SPORAL)), 0.4F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(Sporal entity) {
        return TEXTURES.get(entity.getVariant());
    }
}
