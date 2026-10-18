package com.evandev.spicedcider.mixin.reliableadvancements;

import com.evandev.reliable_advancements.gui.EnhancedAdvancementWidget;
import com.evandev.reliable_advancements.util.ConnectionRouter;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;

@IfModLoaded("reliable_advancements")
@Mixin(EnhancedAdvancementWidget.class)
public class AdvancementIslandLinesMixin {

    @Unique
    private static final Set<ResourceLocation> spicedcider$ISLAND_ROOTS = Set.of(
            ResourceLocation.fromNamespaceAndPath("spicedcider", "adventure/root"),
            ResourceLocation.fromNamespaceAndPath("spicedcider", "husbandry/root"),
            ResourceLocation.fromNamespaceAndPath("spicedcider", "industry/root")
    );

    @Shadow
    private ConnectionRouter.Side incomingSide;

    @WrapOperation(method = "drawConnectivity", at = @At(value = "INVOKE", target = "Lcom/evandev/reliable_advancements/gui/EnhancedAdvancementWidget;drawConnection(Lnet/minecraft/client/gui/GuiGraphics;Lcom/evandev/reliable_advancements/gui/EnhancedAdvancementWidget;IIZ)V"))
    private void spicedcider$skipIslandRootLines(EnhancedAdvancementWidget self, GuiGraphics guiGraphics, EnhancedAdvancementWidget parent, int scrollX, int scrollY, boolean drawInside, Operation<Void> original) {
        if (spicedcider$ISLAND_ROOTS.contains(parent.getAdvancement().holder().id())) {
            this.incomingSide = ConnectionRouter.Side.LEFT;
            return;
        }
        original.call(self, guiGraphics, parent, scrollX, scrollY, drawInside);
    }
}
