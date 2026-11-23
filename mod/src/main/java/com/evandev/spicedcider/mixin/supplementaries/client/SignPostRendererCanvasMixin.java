package com.evandev.spicedcider.mixin.supplementaries.client;

import com.evandev.spicedcider.compat.supplementaries.CanvasWaySigns;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.mehvahdjukaar.supplementaries.client.renderers.tiles.SignPostBlockTileRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.ModelResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("supplementaries")
@Mixin(value = SignPostBlockTileRenderer.class, remap = false)
public abstract class SignPostRendererCanvasMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void spicedcider$canvasModels(BlockEntityRendererProvider.Context context, CallbackInfo ci) {
        var models = Minecraft.getInstance().getModelManager();
        for (WoodType type : CanvasWaySigns.types()) {
            SignPostBlockTileRenderer.MODELS.put(type, models.getModel(ModelResourceLocation.standalone(CanvasWaySigns.modelId(type))));
        }
    }
}
