package com.evandev.spicedcider.compat.continuity;

import me.pepperbell.continuity.client.mixinterface.ModelLoaderExtension;
import me.pepperbell.continuity.client.resource.ModelWrappingHandler;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import org.embeddedt.modernfix.ModernFixClient;
import org.embeddedt.modernfix.api.entrypoint.ModernFixClientIntegration;
import org.embeddedt.modernfix.duck.IExtendedModelBakery;

public final class ContinuityLazyModelWrapping implements ModernFixClientIntegration {
    private ContinuityLazyModelWrapping() {
    }

    public static void register() {
        ModernFixClient.CLIENT_INTEGRATIONS.add(new ContinuityLazyModelWrapping());
    }

    public static boolean bakesLazily(ModelBakery bakery) {
        return bakery instanceof IExtendedModelBakery;
    }

    @Override
    public BakedModel onBakedModelLoad(ModelResourceLocation location, UnbakedModel unbaked, BakedModel baked, ModelState state, ModelBakery bakery, ModelBakery.TextureGetter textures) {
        ModelWrappingHandler handler = ((ModelLoaderExtension) bakery).continuity$getModelWrappingHandler();
        return handler == null ? baked : handler.wrap(baked, null, location);
    }
}
