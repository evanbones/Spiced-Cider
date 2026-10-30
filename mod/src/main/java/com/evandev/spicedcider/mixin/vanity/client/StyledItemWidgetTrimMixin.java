package com.evandev.spicedcider.mixin.vanity.client;

import com.evandev.spicedcider.compat.vanity.TrimToolDisplay;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.vanity.client.components.display.ArmorDisplay;
import tech.thatgravyboat.vanity.client.components.display.Display;
import tech.thatgravyboat.vanity.client.components.display.StyledItemWidget;

@IfModLoaded("vanity")
@Mixin(value = StyledItemWidget.class, remap = false)
public class StyledItemWidgetTrimMixin {

    @Shadow
    private Display display;

    @Inject(method = "select", at = @At("TAIL"))
    private void spicedcider$previewTrim(ItemStack stack, CallbackInfo ci) {
        if (this.display != null || stack.isEmpty() || !stack.has(DataComponents.TRIM)) return;
        this.display = stack.getItem() instanceof ArmorItem ? new ArmorDisplay() : new TrimToolDisplay();
        this.display.setValue(stack, null);
    }
}
