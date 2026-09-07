package com.evandev.spicedcider.mixin.vista.accessor;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@IfModLoaded("vista")
@Mixin(GameRenderer.class)
public interface GameRendererAccessor {
    @Accessor("renderDistance")
    float spicedcider$getRenderDistance();
}
