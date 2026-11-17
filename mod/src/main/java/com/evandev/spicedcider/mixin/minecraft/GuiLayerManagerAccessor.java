package com.evandev.spicedcider.mixin.minecraft;

import net.neoforged.neoforge.client.gui.GuiLayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = GuiLayerManager.class, remap = false)
public interface GuiLayerManagerAccessor {

    @Accessor("layers")
    List<GuiLayerManager.NamedLayer> spicedcider$getLayers();
}
