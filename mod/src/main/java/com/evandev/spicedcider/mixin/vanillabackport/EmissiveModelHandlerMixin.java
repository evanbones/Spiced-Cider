package com.evandev.spicedcider.mixin.vanillabackport;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.AbstractMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Pseudo
@IfModLoaded("vanillabackport")
@Mixin(targets = "com.blackgear.vanillabackport.core.neoforge.emissive.EmissiveModelHandler", remap = false)
public class EmissiveModelHandlerMixin {
    @Shadow
    @Final
    private static String[] EMISSIVE_BLOCKS;

    @WrapOperation(method = "onModelBake", at = @At(value = "INVOKE", target = "Ljava/util/Map;entrySet()Ljava/util/Set;"))
    private static Set<Map.Entry<ModelResourceLocation, BakedModel>> spicedcider$onlyEmissiveBlocks(Map<ModelResourceLocation, BakedModel> models, Operation<Set<Map.Entry<ModelResourceLocation, BakedModel>>> original) {
        Set<Map.Entry<ModelResourceLocation, BakedModel>> entries = new LinkedHashSet<>();
        for (String block : EMISSIVE_BLOCKS) {
            ModelResourceLocation location = new ModelResourceLocation(ResourceLocation.withDefaultNamespace(block), "");
            BakedModel model = models.get(location);
            if (model != null) entries.add(new AbstractMap.SimpleEntry<>(location, model));
        }
        return entries;
    }
}
