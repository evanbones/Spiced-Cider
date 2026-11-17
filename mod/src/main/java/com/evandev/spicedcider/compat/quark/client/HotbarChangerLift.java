package com.evandev.spicedcider.compat.quark.client;

import com.evandev.spicedcider.mixin.minecraft.GuiLayerManagerAccessor;
import com.evandev.spicedcider.mixin.minecraft.GuiLayerManagerHolderAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.GuiLayerManager;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.violetmoon.quark.content.management.module.HotbarChangerModule;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HotbarChangerLift {
    private static final Set<ResourceLocation> LIFTED = new HashSet<>();
    private static int layerCount = -1;
    private static boolean pushed;

    static void pre(RenderGuiLayerEvent.Pre event) {
        if (event.isCanceled() || HotbarChangerModule.height <= 0.0F && HotbarChangerModule.oldHeight <= 0.0F) return;
        if (!lifted().contains(event.getName())) return;
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(true);
        float shift = 22.0F - Mth.lerp(partialTick, HotbarChangerModule.oldHeight, HotbarChangerModule.height) * 90.0F;
        if (shift >= 0.0F) return;
        event.getGuiGraphics().pose().pushPose();
        event.getGuiGraphics().pose().translate(0.0F, shift, 0.0F);
        pushed = true;
    }

    static void post(RenderGuiLayerEvent.Post event) {
        if (!pushed) return;
        pushed = false;
        event.getGuiGraphics().pose().popPose();
    }

    private static Set<ResourceLocation> lifted() {
        GuiLayerManager manager = ((GuiLayerManagerHolderAccessor) Minecraft.getInstance().gui).spicedcider$getLayerManager();
        List<GuiLayerManager.NamedLayer> layers = ((GuiLayerManagerAccessor) manager).spicedcider$getLayers();
        if (layers.size() != layerCount) {
            layerCount = layers.size();
            LIFTED.clear();
            boolean lifting = false;
            for (GuiLayerManager.NamedLayer layer : layers) {
                if (layer.name().equals(VanillaGuiLayers.EXPERIENCE_BAR)) lifting = true;
                else if (layer.name().equals(VanillaGuiLayers.EFFECTS)) lifting = false;
                if (lifting) LIFTED.add(layer.name());
            }
        }
        return LIFTED;
    }
}
