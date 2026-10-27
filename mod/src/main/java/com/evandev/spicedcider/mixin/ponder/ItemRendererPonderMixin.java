package com.evandev.spicedcider.mixin.ponder;

import com.evandev.spicedcider.compat.ponder.PonderSubstitutes;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@IfModLoaded("ponder")
@Mixin(ItemRenderer.class)
public class ItemRendererPonderMixin {

    @ModifyVariable(method = "getModel", at = @At("HEAD"), argsOnly = true)
    private ItemStack spicedcider$ponderModel(ItemStack stack) {
        return PonderSubstitutes.renderedItem(stack);
    }

    @ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true)
    private ItemStack spicedcider$ponderRender(ItemStack stack) {
        return PonderSubstitutes.renderedItem(stack);
    }
}
