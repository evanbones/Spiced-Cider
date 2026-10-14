package com.evandev.spicedcider.mixin.c2me.accessor;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.ArrayList;

@IfModLoaded("c2me")
@IfModLoaded("architectury")
@Mixin(targets = "dev.architectury.event.EventFactory$EventImpl", remap = false)
public interface ArchitecturyEventAccessor {

    @Accessor("listeners")
    ArrayList<?> spicedcider$getListeners();
}
