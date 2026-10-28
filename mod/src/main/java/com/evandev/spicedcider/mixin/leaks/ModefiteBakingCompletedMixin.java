package com.evandev.spicedcider.mixin.leaks;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.ModelEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@IfModLoaded("modefite")
@Mixin(targets = "timmychips.modefiteitemdefinitions.neoforge.NeoForgeClientEvents", remap = false)
public class ModefiteBakingCompletedMixin {

    @Inject(method = "onBakingCompleted", at = @At("TAIL"))
    private static void spicedcider$keepOnlyItemModels(ModelEvent.BakingCompleted event, CallbackInfo ci) {
        Map<ResourceLocation, BakedModel> models = new HashMap<>();
        Collection<ResourceLocation> ids = ModefiteClientCommonAccessor.spicedcider$modelIds();
        if (ids != null) {
            for (ResourceLocation id : ids) {
                BakedModel model = event.getModels().get(new ModelResourceLocation(id, "standalone"));
                if (model != null) models.put(id, model);
            }
        }
        ModefiteResolveRecursiveAccessor.spicedcider$setModelProvider(models::get);
    }
}
