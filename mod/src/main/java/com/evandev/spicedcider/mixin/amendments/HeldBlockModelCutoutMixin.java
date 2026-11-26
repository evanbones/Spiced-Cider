package com.evandev.spicedcider.mixin.amendments;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.mehvahdjukaar.amendments.client.renderers.HeldItemRenderUtils;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("amendments")
@Mixin(value = HeldItemRenderUtils.class, remap = false)
public abstract class HeldBlockModelCutoutMixin {
    @WrapOperation(method = "renderBlockModel", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ItemBlockRenderTypes;getRenderType(Lnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/client/renderer/RenderType;"))
    private static RenderType spicedcider$cutoutShaded(BlockState state, boolean cull, Operation<RenderType> original) {
        RenderType type = original.call(state, cull);
        return type == Sheets.solidBlockSheet() ? Sheets.cutoutBlockSheet() : type;
    }

    @WrapOperation(method = "renderBlockModel", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ItemBlockRenderTypes;getMovingBlockRenderType(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/renderer/RenderType;"))
    private static RenderType spicedcider$cutoutUnshaded(BlockState state, Operation<RenderType> original) {
        RenderType type = original.call(state);
        return type == RenderType.solid() ? RenderType.cutout() : type;
    }
}
