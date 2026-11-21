package com.evandev.spicedcider.compat.modernfix;

import com.evandev.spicedcider.client.models.CrucibleCampfireModel;
import com.evandev.spicedcider.client.models.CrucibleCampfires;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import org.embeddedt.modernfix.ModernFixClient;
import org.embeddedt.modernfix.api.entrypoint.ModernFixClientIntegration;
import org.embeddedt.modernfix.duck.IExtendedModelBakery;

import java.util.Map;

public final class CrucibleCampfireLazyModelWrapping implements ModernFixClientIntegration {
    private Map<ModelResourceLocation, Float> locations;

    public static void register() {
        ModernFixClient.CLIENT_INTEGRATIONS.add(new CrucibleCampfireLazyModelWrapping());
    }

    public static boolean bakesLazily(ModelBakery bakery) {
        return bakery instanceof IExtendedModelBakery;
    }

    @Override
    public BakedModel onBakedModelLoad(ModelResourceLocation location, UnbakedModel unbaked, BakedModel baked, ModelState state, ModelBakery bakery, ModelBakery.TextureGetter textures) {
        if (baked == null || baked instanceof CrucibleCampfireModel || !bakesLazily(bakery)) return baked;
        if (locations == null) locations = CrucibleCampfires.modelLocations();
        Float bottom = locations.get(location);
        return bottom == null ? baked : CrucibleCampfires.wrap(baked, bottom);
    }
}
