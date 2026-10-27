package com.evandev.spicedcider.mixin.mapatlases;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("map_atlases")
@Mixin(targets = "pepjebs.mapatlases.client.MapAtlasesClient", remap = false)
public class MapAtlasesHudBackgroundMixin {

    @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lpepjebs/mapatlases/MapAtlasesMod;res(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"))
    private static ResourceLocation spicedcider$vanillaHudBackground(String path, Operation<ResourceLocation> original) {
        if ("textures/gui/hud/map_background.png".equals(path)) return ResourceLocation.withDefaultNamespace("textures/map/map_background.png");
        return original.call(path);
    }
}
