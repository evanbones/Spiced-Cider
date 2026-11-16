package com.evandev.spicedcider.compat.modernfix;

import com.evandev.spicedcider.client.fruit.HangingFruitSway;
import com.evandev.spicedcider.client.fruit.SwayingFruitModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import org.embeddedt.modernfix.ModernFixClient;
import org.embeddedt.modernfix.api.entrypoint.ModernFixClientIntegration;
import org.embeddedt.modernfix.duck.IExtendedModelBakery;

import java.util.Set;

public final class HangingFruitLazyModelWrapping implements ModernFixClientIntegration {
    private Set<ModelResourceLocation> locations;

    public static void register() {
        ModernFixClient.CLIENT_INTEGRATIONS.add(new HangingFruitLazyModelWrapping());
    }

    public static boolean bakesLazily(ModelBakery bakery) {
        return bakery instanceof IExtendedModelBakery;
    }

    @Override
    public BakedModel onBakedModelLoad(ModelResourceLocation location, UnbakedModel unbaked, BakedModel baked, ModelState state, ModelBakery bakery, ModelBakery.TextureGetter textures) {
        if (baked == null || baked instanceof SwayingFruitModel || !bakesLazily(bakery)) return baked;
        if (locations == null) locations = HangingFruitSway.modelLocations();
        return locations.contains(location) ? new SwayingFruitModel(baked) : baked;
    }
}
