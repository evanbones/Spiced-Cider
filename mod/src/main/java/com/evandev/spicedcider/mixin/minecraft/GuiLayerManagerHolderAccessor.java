package com.evandev.spicedcider.mixin.minecraft;

import net.minecraft.client.gui.Gui;
import net.neoforged.neoforge.client.gui.GuiLayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Gui.class)
public interface GuiLayerManagerHolderAccessor {

    @Accessor(value = "layerManager", remap = false)
    GuiLayerManager spicedcider$getLayerManager();
}
