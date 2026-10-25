package com.evandev.spicedcider.mixin.cirrus;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@IfModLoaded("cirrus")
@Mixin(targets = "com.jvn.cirrus.client.CirrusCloudRenderer$LayerDefinition", remap = false)
public interface CirrusLayerDefinitionInvoker {

    @Invoker("speed")
    double spicedcider$speed();
}
