package com.evandev.spicedcider.compat.wherewindsblow;

import com.jvn.wherewindsblow.client.foliage.ResponsiveFoliage;
import com.jvn.wherewindsblow.client.lantern.LanternSway;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.neoforged.neoforge.client.event.ModelEvent;
import org.embeddedt.modernfix.ModernFixClient;
import org.embeddedt.modernfix.api.entrypoint.ModernFixClientIntegration;
import org.embeddedt.modernfix.duck.IExtendedModelBakery;

import java.util.HashMap;
import java.util.Map;

public final class WhereWindsBlowLazyModelWrapping implements ModernFixClientIntegration {

    public static void register() {
        ModernFixClient.CLIENT_INTEGRATIONS.add(new WhereWindsBlowLazyModelWrapping());
    }

    public static boolean bakesLazily(ModelBakery bakery) {
        return bakery instanceof IExtendedModelBakery;
    }

    @Override
    public BakedModel onBakedModelLoad(ModelResourceLocation location, UnbakedModel unbaked, BakedModel baked, ModelState state, ModelBakery bakery, ModelBakery.TextureGetter textures) {
        Map<ModelResourceLocation, BakedModel> models = new HashMap<>(1);
        models.put(location, baked);
        ModelEvent.ModifyBakingResult event = new ModelEvent.ModifyBakingResult(models, null, bakery);
        ResponsiveFoliage.wrapModels(event);
        LanternSway.wrapModels(event);
        return models.get(location);
    }
}
