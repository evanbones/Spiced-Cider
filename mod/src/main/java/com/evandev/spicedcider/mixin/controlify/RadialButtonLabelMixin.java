package com.evandev.spicedcider.mixin.controlify;

import com.evandev.spicedcider.client.gui.WheelLabel;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.isxander.controlify.gui.screen.RadialMenuScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineLabel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("controlify")
@Mixin(targets = "dev.isxander.controlify.gui.screen.RadialMenuScreen$RadialButton", remap = false)
public class RadialButtonLabelMixin {

    @Shadow
    private RadialMenuScreen.RadialItem item;

    @Shadow
    @Final
    RadialMenuScreen this$0;

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/MultiLineLabel;renderCentered(Lnet/minecraft/client/gui/GuiGraphics;II)V"))
    private void spicedcider$wheelLabel(MultiLineLabel label, GuiGraphics graphics, int x, int y, Operation<Void> original) {
        Font font = Minecraft.getInstance().font;
        float radius = ((RadialMenuScreenAccessor) this$0).spicedcider$radialRadius();
        float scale = WheelLabel.snap(WheelLabel.SCALE);
        int wrap = (int) ((radius - 19.0F) * 2.0F / scale) - 8;
        WheelLabel.render(graphics, font, WheelLabel.split(font, item.name(), wrap), this$0.width / 2.0F, this$0.height / 2.0F, scale);
    }
}
