package com.evandev.spicedcider.mixin.emf;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@IfModLoaded("entity_model_features")
@IfModLoaded("tiny_takeover_backport")
@Mixin(targets = "traben.entity_model_features.EMFManager", remap = false)
public class EMFTinyTakeoverLayerMixin {
    @ModifyVariable(method = "injectIntoModelRootGetter", at = @At("HEAD"), argsOnly = true)
    private ModelLayerLocation spicedcider$vanillaBabyLayer(ModelLayerLocation layer) {
        ResourceLocation model = layer.getModel();
        if (!"tiny_takeover_backport".equals(model.getNamespace())) return layer;
        return new ModelLayerLocation(ResourceLocation.withDefaultNamespace(model.getPath()), layer.getLayer());
    }
}
